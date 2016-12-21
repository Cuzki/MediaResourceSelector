package com.example.cuzki.mediaselectordemo.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cuzki.mediaselectordemo.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * Created by Cuzki on 2015/3/18.
 */
public class PreviewPhotoActivity extends Activity {

    private static final int LOADING_SUCCESSFUL = 1;
    private ArrayList<String> mPhotoList;
    private int mIndex;
    private boolean mDeleteable;
    private PreviewPagerAdapter mAdapter;
    public static final int PREVIEW_ALBUM = 2858;
    public static final String PREVIEW_PHOTO_LIST = "priview_photo_list";
    public static final String PREVIEW_PHOTO_INDEX = "priview_photo_index";
    public static final String PREVIEW_PHOTO_DELETEABLE = "PREVIEW_PHOTO_DELETEABLE";
    HackyViewPager mVpContainer;

    private TextView mBtnReturn;
    private TextView mBtnCancle;
    private TextView mTvnProgress;
    private Activity mActivity;

    /**
     * 启动预览界面，用于图片上传界面及首页图片预览
     * @param activity 启动预览界面的上下文
     * @param photoList 预览图片链接，注意：直接传入ImageView显示，如果有前缀等需要在传入前添加
     * @param deleteable 是否支持删除（删除按钮是否可见，该界面finish后会将剩余图片路径返回）
     */
    public static void startPreViewPhoto(Activity activity, List<String> photoList, boolean deleteable){
        if(activity==null||photoList==null||photoList.size()==0){
            return;
        }
        Intent intent=new Intent(activity,PreviewPhotoActivity.class);
        intent.putStringArrayListExtra(PreviewPhotoActivity.PREVIEW_PHOTO_LIST, (ArrayList<String>) photoList);
        intent.putExtra(PreviewPhotoActivity.PREVIEW_PHOTO_DELETEABLE, deleteable);
        activity.startActivityForResult(intent, PREVIEW_ALBUM);
    }

    /**
     * 启动预览界面，用于图片上传界面及首页图片预览
     * @param activity 启动预览界面的上下文
     * @param photoList 预览图片链接，注意：直接传入ImageView显示，如果有前缀等需要在传入前添加
     * @param deleteable 是否支持删除（删除按钮是否可见，该界面finish后会将剩余图片路径返回）
     */
    public static void startPreViewPhoto(Activity activity, List<String> photoList, boolean deleteable, int index){
        if(activity==null||photoList==null||photoList.size()==0||index<0||index>=photoList.size()){
            return;
        }
        Intent intent=new Intent(activity,PreviewPhotoActivity.class);
        intent.putStringArrayListExtra(PreviewPhotoActivity.PREVIEW_PHOTO_LIST, (ArrayList<String>) photoList);
        intent.putExtra(PreviewPhotoActivity.PREVIEW_PHOTO_DELETEABLE, deleteable);
        intent.putExtra(PreviewPhotoActivity.PREVIEW_PHOTO_INDEX,index);
        activity.startActivityForResult(intent, PREVIEW_ALBUM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);
        mActivity = this;
        initView();
        getIntentData();
        initEvent();
        initAdapter();
        mVpContainer.setCurrentItem(mIndex);
    }

    private void initView() {
        mVpContainer = (HackyViewPager) findViewById(R.id.vp_content);
        mBtnReturn = (TextView) findViewById(R.id.btn_return);
        mBtnCancle = (TextView) findViewById(R.id.btn_cancle);
        mTvnProgress = (TextView) findViewById(R.id.tv_progress);
    }


    private void initEvent() {
        mVpContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
                setProgress();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitWithResult();
            }
        });
        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoList == null || mPhotoList.size() == 0 || mIndex >= mPhotoList.size()) {
                    return;
                }
                mPhotoList.remove(mIndex);
                if (mPhotoList.size() == 0) {
                    exitWithResult();
                    return;
                }
                mIndex--;
                if (mIndex < 0) {
                    mIndex = 0;
                }
                initAdapter();
                mVpContainer.setCurrentItem(mIndex);
                setProgress();
            }
        });
    }

    private void initAdapter() {
        mAdapter = new PreviewPagerAdapter();
        mVpContainer.setAdapter(mAdapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoList = intent.getStringArrayListExtra(PREVIEW_PHOTO_LIST);
            mIndex = intent.getIntExtra(PREVIEW_PHOTO_INDEX, 0);
            mDeleteable=intent.getBooleanExtra(PREVIEW_PHOTO_DELETEABLE,false);
            if (mPhotoList == null || mPhotoList.isEmpty() || mPhotoList.size() <= mIndex || mIndex < 0) {
                finish();
                return;
            }
            if(mDeleteable){
                mBtnCancle.setVisibility(View.VISIBLE);
            }else {
                mBtnCancle.setVisibility(View.GONE);
            }
            setProgress();
        }else {
            finish();
        }
    }

    private void setProgress() {
        if(mIndex<0){
            mIndex=0;
        }
        mTvnProgress.setText((mIndex + 1) + "/" + mPhotoList.size());
    }

    class PreviewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mPhotoList != null) {
                return mPhotoList.size();
            }
            return 0;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            container.addView(photoView, params);
            Glide.with(mActivity)
                    .load( mPhotoList.get(position))
                    .into(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void onBackPressed() {
        exitWithResult();
    }

    private void exitWithResult(){
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PREVIEW_PHOTO_LIST, mPhotoList);
        this.setResult(RESULT_OK,intent);
        this.finish();
    }

}
