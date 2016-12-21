/**
 * Created on 2016/5/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuzki.mediaselectordemo.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumDetailFragment extends android.support.v4.app.Fragment implements AlbumPreviewFragment.AlbumPreviewCallBack {
    private GridView mGvList;
    private Button mBtnConfirm;
    private Button mBtnReturn;
    private Button mBtnCancel;
    private Button mBtnShow;
    private TextView mTvSelectedCount;
    private TextView mTvTitle;
    private AblumAdapter mAdapter;
    private AblumFragmentCallBack mParentCallback;
    /**
     * 已选中图片信息 路径
     */
    private List<MediaResourseBean> mSelectPhoto=new ArrayList<MediaResourseBean>();

    private List<MediaResourseBean> mPhotoList;
    private  int maxSelectPhotoNum;
    private String mTitle;
    private String mToastTip;
    private  boolean mIsAllowPick=false;
    private  boolean mIsPhoto=true;
    private ItemCallBack mCallBack=new ItemCallBack() {
        @Override
        public AlbumSelectedResult doOnToggle(int pos) {
            AlbumSelectedResult result=new AlbumSelectedResult();
            result.selectedCount=mGvList.getCheckedItemCount();
            if(mGvList.isItemChecked(pos)){
                result.result=false;
                mGvList.setItemChecked(pos, result.result);
            }else {
                if(result.selectedCount>=maxSelectPhotoNum){
                    Toast.makeText(getActivity(),mToastTip, Toast.LENGTH_SHORT).show();
                    result.result=false;
                    return result;
                }
                result.result=true;
                mGvList.setItemChecked(pos, result.result);
            }
            checkFinishButtonState();
            result.selectedCount=mGvList.getCheckedItemCount();
            mTvSelectedCount.setText(result.selectedCount + "");
            return result;
        }

        @Override
        public void showPhoto(int position,MediaResourseBean bean) {
            if(bean==null){
                return;
            }
            if(position==0&&(MediaResoursesSelectActivity.PICK_PHOTO.equals(bean.getThumbnails())||MediaResoursesSelectActivity.PICK_VIDEO.equals(bean.getThumbnails()))){
                //拍摄照片或摄影
                if(mParentCallback!=null){
                    mParentCallback.pick();
                }
            }else {
                if(mIsPhoto){
                    boolean result=mIsAllowPick&&mTitle.equals("相机胶卷");
                    showAlbumPreview((result?new ArrayList<MediaResourseBean>(mPhotoList.subList(1,mPhotoList.size())):mPhotoList),(result?position-1:position));
                }else {
                    if(mParentCallback!=null){
                        mParentCallback.play(bean);
                    }
                }
            }
        }
    };

    private void computeSelectPhoto(){
        mSelectPhoto.clear();
        SparseBooleanArray checkedArray=mGvList.getCheckedItemPositions();
        for (int i = 0; i < checkedArray.size(); i++) {
            if (checkedArray.valueAt(i)){
                mSelectPhoto.add(mPhotoList.get(checkedArray.keyAt(i)));
            }
        }
    }

    private void showAlbumPreview(List<MediaResourseBean> photoList, int index){
        AlbumPreviewFragment fg=AlbumPreviewFragment.getInstance(photoList,index,mGvList.getCheckedItemCount());
        android.support.v4.app.FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.album_preview_container, fg, "preview");
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public static AlbumDetailFragment getInstance(String title, int max, String tip, boolean allowPick, boolean isPhoto){
        AlbumDetailFragment fg=new AlbumDetailFragment();
        fg.maxSelectPhotoNum=max;
        fg.mTitle=title;
        fg.mToastTip=tip;
        fg.mIsAllowPick=allowPick;
        fg.mIsPhoto=isPhoto;
        return fg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fg_album_detail, container, false);
        initView(view);
        initEvent();
        refresh();
        return view;
    }

    public void refresh() {
        if(mParentCallback!=null){
            mPhotoList=new ArrayList<MediaResourseBean>();
            List<MediaResourseBean> data=mParentCallback.getData(mTitle);
            if(data!=null&&data.size()>0){
                if("相机胶卷".equals(mTitle)){
                    mPhotoList.add(new MediaResourseBean(mTitle,MediaResoursesSelectActivity.PICK_PHOTO,null));
                }
                if("全部视频".equals(mTitle)){
                    mPhotoList.add(new MediaResourseBean(mTitle,MediaResoursesSelectActivity.PICK_VIDEO,null));
                }
                mPhotoList.addAll(data);
            }
        }
        mTvTitle.setText(mTitle);
        initAdapter();
        checkFinishButtonState();
    }

    private void initAdapter() {
        mAdapter=new AblumAdapter();
        mGvList.setAdapter(mAdapter);
    }

    private void initView(View view) {
        mGvList= (GridView) view.findViewById(R.id.gv_album);
        mBtnConfirm= (Button) view.findViewById(R.id.btn_finish);
        mTvSelectedCount= (TextView) view.findViewById(R.id.tv_count);
        mTvTitle=(TextView) view.findViewById(R.id.tv_title);
        mBtnReturn= (Button) view.findViewById(R.id.btn_return);
        mBtnShow= (Button) view.findViewById(R.id.btn_show);
        mBtnCancel=(Button) view.findViewById(R.id.btn_cancle);
        if(!mIsPhoto){
            mBtnReturn.setVisibility(View.GONE);
            mBtnShow.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParentCallback != null) {
                    mParentCallback.toggleList();
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                computeSelectPhoto();
                showAlbumPreview(mSelectPhoto, 0);
            }
        });
    }

    @Override
    public boolean isPhotoSelected(MediaResourseBean bean) {
        boolean reslut=mGvList.isItemChecked(mPhotoList.indexOf(bean));
        return reslut;
    }

    @Override
    public AlbumSelectedResult clickSelected(MediaResourseBean bean) {
        if(mCallBack!=null){
            return mCallBack.doOnToggle(mPhotoList.indexOf(bean));
        }
        return null;
    }

    @Override
    public void commit() {
        if (mParentCallback != null) {
            computeSelectPhoto();
            mParentCallback.commitResult(mSelectPhoto);
        }
    }

    class AblumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPhotoList==null?0:mPhotoList.size();
        }

        @Override
        public Object getItem(int position) {
            return (mPhotoList==null||position>=mPhotoList.size())?null:mPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumItem item=null;
            if(convertView==null){
                item=new AlbumItem(getActivity());
            }else {
                item= (AlbumItem) convertView;
            }
            MediaResourseBean bean=(MediaResourseBean)getItem(position);
            if(bean!=null){
                item.setPhoto(position,bean);
                item.setCallBack(mCallBack);
            }
            return item;
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity!=null&&activity instanceof AblumFragmentCallBack){
            mParentCallback= (AblumFragmentCallBack) activity;
        }
    }

    public interface AblumFragmentCallBack{

        void commitResult(List<MediaResourseBean> result);

        void toggleList();

        List<MediaResourseBean> getData(String fatherName);

        void pick();

        void play(MediaResourseBean resourse);
    }

    private void checkFinishButtonState() {
        if(mGvList.getCheckedItemCount()<=0){
            mBtnConfirm.setEnabled(false);
            mBtnShow.setEnabled(false);
            mTvSelectedCount.setVisibility(View.INVISIBLE);
        }else {
            mBtnConfirm.setEnabled(true);
            mBtnShow.setEnabled(true);
            mTvSelectedCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSelectPhoto=null;
    }
}