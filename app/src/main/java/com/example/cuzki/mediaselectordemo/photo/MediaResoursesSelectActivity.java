/**
 * Created on 2016/5/9
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.example.cuzki.mediaselectordemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * <p>
 *
 * @author Cuzki
 */
public class MediaResoursesSelectActivity extends FragmentActivity implements AlbumDetailFragment.AblumFragmentCallBack, AlbumListFragment.AlbumListCallBack {
    public static final String ALBUM_SELECT_LIST = "ALBUM_SELECT_LIST";
    public static final String ALBUM_SELECTED_LIMIT = "multi_select_num";
    public static final String HAVING_SELECT_PHOTO = "select_path_list";
    public static final String ALBUM_TOAST_TIP = "ALBUM_TOAST_TIP";
    public static final String ALBUM_COMPRESS = "ALBUM_COMPRESS";
    public static final String ALBUM_PHOTO = "ALBUM_PHOTO";
    public static final String ALBUM_ALLOW_PICK = "ALBUM_ALLOW_PICK";
    public static final String PICK_PHOTO = "PICK_PHOTO";
    public static final String PICK_VIDEO = "PICK_VIDEO";
    public static final int TAKE_PICTURE = 0xFF1;
    public static final int PICK_ALBUM = 0xFF2;
    public static final int CROP_ALBUM = 0xFF3;
    public static final int PICK_LOCAL_ALBUM = 0xFF6;
    public static final int PREVIEW_ALBUM = 0xFF4;
    public static final int SD_NOT_EXISTS = 0x0;
    public static final int CAPTURE_SUCCESS = 0x1;
    public static final int CANNOT_FIND_CAMERA = 0x2;
    public static final int COMMIT_RESULT = 0x2d1;
    private int maxSelectPhotoNum;
    private String mToastTip;
    private boolean mIsAlllowPick;
    private boolean mIsPhoto;
    private boolean mIsNeedCompress = false;
    private static final String ALBUM_DETAIL = "ALBUM_DETAIL";
    private static final String ALBUM_LIST = "ALBUM_LIST";

    private int mCompressId = 0;
    private boolean mIsCommiting = false;
    /**
     * 最近使用fragment
     */
    private android.support.v4.app.Fragment mCurrentFragment;
    /**
     * 已选中图片信息 路径
     */
    private List<String> mSelectPhoto = new ArrayList<String>();
    private List<MediaResourseBean> mAllPhotoList = new ArrayList<MediaResourseBean>();
    HashMap<String, List<MediaResourseBean>> mAlbumMap = new HashMap<String, List<MediaResourseBean>>();
    private static final int LOADING_DELAY = 1;
    private MediaScanner mScanner;
    private android.os.Handler mHandler;
    private List<String> mResultList;
    /**
     * 拍照、拍摄本地存储路径
     */
    private String mNewPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        setContentView(R.layout.activity_album);
        getIntentData();
        initHander();
        mScanner = new MediaScanner(MediaResoursesSelectActivity.this, new MediaScannerListener());
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                initStoragePhoto();
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(new Action0() {
            @Override
            public void call() {
                showDetail(mIsPhoto ? "相机胶卷" : "全部视频");
            }
        }).subscribe();
    }


    private void initHander() {
        mHandler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                final int event = msg.what;
                switch (event) {
                    case LOADING_DELAY:
                        Observable.create(new Observable.OnSubscribe<Object>() {
                            @Override
                            public void call(Subscriber<? super Object> subscriber) {
                                initStoragePhoto();
                                subscriber.onCompleted();
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                if (mCurrentFragment != null && mCurrentFragment instanceof AlbumDetailFragment) {
                                    ((AlbumDetailFragment) mCurrentFragment).refresh();
                                }
                            }
                        }).subscribe();
                        break;
                    case COMMIT_RESULT:
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra(ALBUM_SELECT_LIST, (ArrayList<String>) mResultList);
                        MediaResoursesSelectActivity.this.setResult(RESULT_OK, intent);
                        MediaResoursesSelectActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showDetail(String fatherName) {
        AlbumDetailFragment detailFragment = AlbumDetailFragment.getInstance(fatherName, maxSelectPhotoNum, mToastTip, mIsAlllowPick && ("相机胶卷".equals(fatherName) || "全部视频".equals(fatherName)) ? true : false, mIsPhoto);
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.album_page_container, detailFragment, ALBUM_DETAIL);
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        mCurrentFragment = detailFragment;
        transaction.commitAllowingStateLoss();
    }

    private void showList() {
        AlbumListFragment listFragment = AlbumListFragment.getInstance(mAlbumMap);
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.album_page_container, listFragment, ALBUM_LIST);
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        mCurrentFragment = listFragment;
        transaction.commitAllowingStateLoss();
    }


    private void initStoragePhoto() {
        if (this == null || this.isFinishing()) {
            return;
        }
        getPhotoList(this.getContentResolver().query(mIsPhoto ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, (mIsPhoto ? MediaStore.Images.Media.DATE_ADDED : MediaStore.Video.Media.DATE_ADDED) + " desc"));
    }

    private void getPhotoList(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        if (mAllPhotoList == null) {
            mAllPhotoList = new ArrayList<MediaResourseBean>();
        } else {
            mAllPhotoList.clear();
        }
        if (mAlbumMap == null) {
            mAlbumMap = new HashMap<String, List<MediaResourseBean>>();
        } else {
            mAlbumMap.clear();
        }
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(mIsPhoto ? MediaStore.Images.Media.DATA : MediaStore.Video.Media.DATA));
            String fatherName = new File(path).getParentFile().getName();
            if (!TextUtils.isEmpty(path)) {
                mAllPhotoList.add(new MediaResourseBean(fatherName, path, path));
                if (!mAlbumMap.containsKey(fatherName)) {
                    List<MediaResourseBean> beans = new ArrayList<MediaResourseBean>();
                    beans.add(new MediaResourseBean(fatherName, path, path));
                    mAlbumMap.put(fatherName, beans);
                } else {
                    mAlbumMap.get(fatherName).add(new MediaResourseBean(fatherName, path, path));
                }
            }
        }
        mAlbumMap.put(mIsPhoto ? "相机胶卷" : "全部视频", mAllPhotoList);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            maxSelectPhotoNum = intent.getIntExtra(ALBUM_SELECTED_LIMIT, 9);
            List<String> data = intent.getStringArrayListExtra(HAVING_SELECT_PHOTO);
            if (data != null && !data.isEmpty()) {
                if (maxSelectPhotoNum >= data.size()) {
                    mSelectPhoto.addAll(data);
                } else {
                    for (int i = 0; i < maxSelectPhotoNum; i++) {
                        mSelectPhoto.add(data.get(i));
                    }
                }
            }
            mIsAlllowPick = intent.getBooleanExtra(ALBUM_ALLOW_PICK, false);
            mIsPhoto = intent.getBooleanExtra(ALBUM_PHOTO, true);
            mToastTip = intent.getStringExtra(ALBUM_TOAST_TIP);
            mIsNeedCompress=intent.getBooleanExtra(ALBUM_COMPRESS, false);
        }
    }

    /**
     * 启动相册选取图片，oActivityForResult中获取选取的图片路径 key=ALBUM_SELECT_LIST （List<String ></>本地存储路径）
     *
     * @param activity         上下文
     * @param maxSelectedCount 打开相册可选图片数量限制，目前不开放已选图片传入
     */
    public static void pickAlbumPhoto(Activity activity, int maxSelectedCount, boolean allowPick, boolean isPhoto,boolean isCompress,List<String> filterList) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(activity, MediaResoursesSelectActivity.class);
        intent.putExtra(ALBUM_SELECTED_LIMIT, maxSelectedCount);
        intent.putExtra(ALBUM_ALLOW_PICK, allowPick);
        intent.putExtra(ALBUM_PHOTO, isPhoto);
        intent.putExtra(ALBUM_COMPRESS, isCompress);
        activity.startActivityForResult(intent, PICK_LOCAL_ALBUM);
    }

    /**
     * 启动相册选取图片，oActivityForResult中获取选取的图片路径 key=ALBUM_SELECT_LIST （List<String ></>本地存储路径）
     *
     * @param activity         上下文
     * @param maxSelectedCount 打开相册可选图片数量限制，目前不开放已选图片传入
     * @param tip              超过maxSelectedCount需要给出的toast提示语
     */
    public static void pickAlbumPhoto(Activity activity, int maxSelectedCount, String tip) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent(activity, MediaResoursesSelectActivity.class);
        intent.putExtra(ALBUM_SELECTED_LIMIT, maxSelectedCount);
        intent.putExtra(ALBUM_TOAST_TIP, tip);
        activity.startActivityForResult(intent, PICK_LOCAL_ALBUM);
    }


    public void commitResult(List<MediaResourseBean> result) {
        if (mIsCommiting || result == null || result.size() == 0) {
            return;
        }
        mIsCommiting = true;
        mCompressId = 0;
        Log.i("album", "----提交" + result.size() + "个资源---");
        mResultList = new ArrayList<String>();
        for (MediaResourseBean bean : result) {
            Log.i("album", "结果：" + bean.getRealPath());
            mResultList.add(bean.getRealPath());
        }
        if(mIsNeedCompress&&mIsPhoto){
            compressPhoto();
        }else {
            mHandler.sendEmptyMessage(COMMIT_RESULT);
        }

    }


    private void compressPhoto() {
        if (mCompressId == mResultList.size() ) {
            mHandler.sendEmptyMessage(COMMIT_RESULT);
            return;
        }
        Luban.get(this)
                .load(new File(mResultList.get(mCompressId)))
                .putGear(Luban.FIRST_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
                    @Override
                    public Observable<? extends File> call(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Log.i("album", "----压缩完第"+mCompressId+"个文件");
                        String d= mResultList.get(mCompressId);
                        mResultList.set(mCompressId,file.getAbsolutePath());
                        mCompressId++;
                        compressPhoto();
                    }
                });
    }

    @Override
    public void toggleList() {
        showList();
    }

    @Override
    public List<MediaResourseBean> getData(String fatherName) {
        return mAlbumMap.get(fatherName);
    }

    @Override
    public void pick() {
        setNewPhotoPath();
        if (mIsPhoto) {//拍照
            PhotoUtils.takePhoto(MediaResoursesSelectActivity.this, mNewPath);
        } else {//摄影
            PhotoUtils.makeVideo(MediaResoursesSelectActivity.this, mNewPath);
        }
    }

    @Override
    public void play(MediaResourseBean resourse) {
        if (resourse == null) {
            return;
        }
        Uri uri = Uri.parse("file:///" + resourse.getRealPath());
        //调用系统自带的播放器
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        MediaResoursesSelectActivity.this.startActivity(intent);
    }


    private void setNewPhotoPath() {
        mNewPath = PhotoUtils.getFilePath(mIsPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PhotoUtils.MAKE_VIDEO || requestCode == TAKE_PICTURE) && resultCode == Activity.RESULT_OK && this != null && !this.isFinishing()) {
            notifySystemUpdateMedia();
        }
    }

    private void notifySystemUpdateMedia() {
        File file = new File(mNewPath);
        if (file != null && file.exists()) {
            if (mScanner == null) {
                mScanner = new MediaScanner(MediaResoursesSelectActivity.this, new MediaScannerListener());
            }
            mScanner.scanFile(mNewPath, null);
        }
    }

    @Override
    public void toggleDetail(String fatherName) {
        showDetail(fatherName);
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.Fragment fg = null;
        if ((fg = getSupportFragmentManager().findFragmentByTag(ALBUM_DETAIL)) != null && fg.getChildFragmentManager().getBackStackEntryCount() > 0) {
            fg.getChildFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    private class MediaScannerListener implements MediaScanner.MediaCallBack {
        @Override
        public void onScannerStart() {
            if (MediaResoursesSelectActivity.this != null && !MediaResoursesSelectActivity.this.isFinishing()) {
//                showLoading();
            }
        }

        @Override
        public void onScannerEnd() {
            if (MediaResoursesSelectActivity.this != null && !MediaResoursesSelectActivity.this.isFinishing()) {
                if (mHandler == null) {
                    initHander();
                }
                mHandler.sendEmptyMessageDelayed(LOADING_DELAY, 150);
            }
        }
    }
}
