/**
 * Created on 2016/5/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.cuzki.mediaselectordemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <p>
 *
 * @author Cuzki
 */
public class AlbumListFragment extends android.support.v4.app.Fragment implements View.OnTouchListener{
    private ListView mLvList;
    private Button mBtnCancel;
    private AlbumListCallBack mParentCallback;
    private List<AlbumData> mAlbumList;
    private AlbumListAdapter mAdapter;

    private HashMap<String, List<MediaResourseBean>> mAlbumMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fg_album_list, container, false);
        initView(view);
        initEvent();
        computeAlbum();
        initAdapter();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);
    }

    private void initView(View view) {
        mLvList= (ListView) view.findViewById(R.id.lv_album);
        mBtnCancel= (Button) view.findViewById(R.id.btn_cancle);
    }

    private void initEvent() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mParentCallback!=null){
                    mParentCallback.toggleDetail(mAlbumList.get(position).fatherName);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    class AlbumListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mAlbumList==null?0:mAlbumList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAlbumList==null?null:mAlbumList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumListItem item=null;
            if(convertView==null){
                item=new AlbumListItem(getActivity());
            }else {
                item= (AlbumListItem) convertView;
            }
            item.setAlbum((AlbumData) getItem(position));
            return item;
        }
    }

    public static AlbumListFragment getInstance(HashMap<String, List<MediaResourseBean>> albumMap){
        AlbumListFragment fg=new AlbumListFragment();
        fg.mAlbumMap=albumMap;
        return fg;
    }

    public void refresh( HashMap<String, List<MediaResourseBean>> albumMap){
        mAlbumMap=albumMap;
        computeAlbum();
        initAdapter();
    }

    private void computeAlbum(){
        mAlbumList=new ArrayList<AlbumData>();
        if(mAlbumMap!=null){
            for(String key:mAlbumMap.keySet()){
                AlbumData tem=new AlbumData();
                tem.fatherName=key;
                List<MediaResourseBean> album=mAlbumMap.get(key);
                if(album==null||album.size()==0){
                    tem.albumImg="";
                }else {
                    tem.albumImg=album.get(0).getThumbnails();
                    tem.albumCount=album.size();
                }
                mAlbumList.add(tem);
            }
        }
    }

    private void initAdapter() {
        mAdapter=new AlbumListAdapter();
        mLvList.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity!=null&&activity instanceof AlbumListCallBack){
            mParentCallback= (AlbumListCallBack) activity;
        }
    }

    public interface AlbumListCallBack{
        void toggleDetail(String fatherName);
    }

    public class AlbumData{
        String fatherName;
        String albumImg;
        int albumCount;
    }
}
