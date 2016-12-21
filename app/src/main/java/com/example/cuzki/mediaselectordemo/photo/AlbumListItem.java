/**
 * Created on 2016/5/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cuzki.mediaselectordemo.R;


/**
 * <p>
 *
 * @author Cuzki
 */
public class AlbumListItem extends LinearLayout {
    private ImageView mIvAlbum;
    private TextView mTvAlbum;
    private Context mContxt;
    public AlbumListItem(Context context) {
        super(context);
        init(context);
    }

    public AlbumListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlbumListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AlbumListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context){
        mContxt=context;
        LayoutInflater.from(context).inflate(R.layout.item_album_list,this);
        mIvAlbum= (ImageView) findViewById(R.id.iv_album);
        mTvAlbum= (TextView) findViewById(R.id.tv_alnum);
    }

    public void setAlbum(AlbumListFragment.AlbumData data){
        if(data!=null){
            Glide.with(mContxt)
                    .load(AlbumItem.LOCAL_FILE_PREFIX +data.albumImg)
                    .into(mIvAlbum);
            mTvAlbum.setText(data.fatherName+"  ("+data.albumCount+")");
        }
    }
}
