/**
 * Created on 2016/5/12
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cuzki.mediaselectordemo.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * <p/>
 *
 * @author Cuzki
 */
public class AlbumPreviewFragment extends android.support.v4.app.Fragment implements View.OnTouchListener {
    public static final String ALBUM_PREVIEW_LIST="ALBUM_PREVIEW_LIST";
    public static final String ALBUM_PREVIEW_SELECTED_COUNT="ALBUM_PREVIEW_SELECTED_COUNT";
    public static final String ALBUM_PREVIEW_INDEX="ALBUM_PREVIEW_INDEX";
    private List<MediaResourseBean> mPhotoList;
    private int mIndex;
    private int mCount;
    private PreviewPagerAdapter mAdapter;
    HackyViewPager mVpContainer;
    private TextView mBtnReturn;
    private Button mBtnConfirm;
    private TextView mTvSelectedCount;
    private CheckableImageView mCheckView;
    AlbumPreviewCallBack mCallBack;



    public static AlbumPreviewFragment getInstance(List<MediaResourseBean> photoList, int index, int count){
        AlbumPreviewFragment fg=new AlbumPreviewFragment();
        fg.mPhotoList=photoList;
        fg.mIndex=index;
        fg.mCount=count;
        return fg;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_album_preview, container, false);
        initView(view);
        initEvent();
        initAdapter();
        mVpContainer.setCurrentItem(mIndex);
        judgeCheckState(mPhotoList.get(mIndex));
        checkFinishButtonState(mCount);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);
    }

    private void initView(View view) {
        mVpContainer = (HackyViewPager) view.findViewById(R.id.vp_content);
        mBtnReturn = (TextView) view.findViewById(R.id.btn_return);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_finish);
        mTvSelectedCount = (TextView) view.findViewById(R.id.tv_count);
        mCheckView = (CheckableImageView) view.findViewById(R.id.iv_select);
    }

    private void initEvent() {
        mVpContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
                judgeCheckState(mPhotoList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.commit();
            }
        });
        android.support.v4.app.Fragment fragment;
        if ((fragment = getParentFragment()) != null && fragment instanceof AlbumPreviewCallBack) {
            mCallBack = (AlbumPreviewCallBack) fragment;
        }
        mCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    AlbumSelectedResult out = mCallBack.clickSelected(mPhotoList.get(mIndex));
                    if (out == null) {
                        return;
                    }
                    if (out.result) {
                        mCheckView.setChecked(true);
                    } else {
                        mCheckView.setChecked(false);
                    }
                    checkFinishButtonState(out.selectedCount);
                }
            }
        });

    }

    private void checkFinishButtonState(int count) {
        if (count <= 0) {
            mBtnConfirm.setEnabled(false);
            mTvSelectedCount.setVisibility(View.INVISIBLE);
        } else {
            mBtnConfirm.setEnabled(true);
            mTvSelectedCount.setVisibility(View.VISIBLE);
            mTvSelectedCount.setText(count + "");
        }
    }

    private void judgeCheckState(MediaResourseBean bean) {
        if (mCallBack != null) {
            if (mCallBack.isPhotoSelected(bean)) {
                mCheckView.setChecked(true);
            } else {
                mCheckView.setChecked(false);
            }
        }
    }

    private void initAdapter() {
        mAdapter = new PreviewPagerAdapter();
        mVpContainer.setAdapter(mAdapter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
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
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(photoView, params);
            Glide.with(getActivity())
                    .load(AlbumItem.LOCAL_FILE_PREFIX + mPhotoList.get(position).getRealPath())
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

    public interface AlbumPreviewCallBack {
        boolean isPhotoSelected(MediaResourseBean path);

        AlbumSelectedResult clickSelected(MediaResourseBean path);

        void commit();
    }
}
