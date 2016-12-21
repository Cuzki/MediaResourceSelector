/**
 * Created on 2016/5/9
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.cuzki.mediaselectordemo.R;


/**
 * <p>
 *
 * @author Cuzki
 */
public class AlbumItem extends CheckableRelativeLayout {
    public static final String LOCAL_FILE_PREFIX = "file://";
    private ImageView mIvPhoto;
    private CheckableImageView mIvChecked;
    private ItemCallBack mCallBack;
    private Context mContext;
    private int mIndex;
    private MediaResourseBean mData;
    private boolean mIsPick=false;
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    public void setCallBack(ItemCallBack callBack) {
        this.mCallBack = callBack;
    }
    public AlbumItem(Context context) {
        super(context);
        init(context);
    }

    public AlbumItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AlbumItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mContext=context;
        LayoutInflater.from(context).inflate(R.layout.item_ablum,this);
        mIvPhoto= (SquareImageView) findViewById(R.id.iv_img);
        mIvChecked= (CheckableImageView) findViewById(R.id.iv_select);
        mIvChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallBack!=null){
                    mCallBack.doOnToggle(mIndex);
                }
            }
        });
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallBack!=null){
                    mCallBack.showPhoto(mIndex,mData);
                }
            }
        });
    }

    public void setPhoto(int position,MediaResourseBean data){
        if(data==null||position<0){
            return;
        }
        mData=data;
        mIndex=position;
        if (mIvPhoto != null) {
            if(MediaResoursesSelectActivity.PICK_VIDEO.equals(data.getThumbnails())){
                mIvChecked.setVisibility(View.GONE);
                mIvPhoto.setImageDrawable( ContextCompat.getDrawable(mContext,
                        R.drawable.ic_add_video));
                return;
            }
            if(MediaResoursesSelectActivity.PICK_PHOTO.equals(data.getThumbnails())){
                mIvChecked.setVisibility(View.GONE);
                mIvPhoto.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_add_photo));

                return;
            }

            Glide.with(mContext)
                    .load(LOCAL_FILE_PREFIX +data.getThumbnails())
                    .into(mIvPhoto);

        }
    }

}
