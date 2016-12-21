package com.example.cuzki.mediaselectordemo;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cuzki.mediaselectordemo.photo.MediaResoursesSelectActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ExpandableListView listView;

    private List<String> group= new ArrayList<String>();
    private List<List<String>> child= new ArrayList<List<String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView view= (TextView) findViewById(R.id.tv_click);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaResoursesSelectActivity.pickAlbumPhoto(MainActivity.this,9,true,true,true,null);
            }
        });

        TextView view1= (TextView) findViewById(R.id.tv_click1);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaResoursesSelectActivity.pickAlbumPhoto(MainActivity.this,9,true,false,true,null);
            }
        });

        listView= (ExpandableListView) findViewById(R.id.lv_list);
        group.add("第一組");
        group.add("第二組");
        group.add("第三組");
        for(int i=0;i<group.size();i++){
            List<String> s=new ArrayList<String>();
            for(int j=0;j<5;j++){
                s.add("相约"+group.get(i)+" 真好"+j);
            }
            child.add(s);
        }
        listView.setGroupIndicator(null);
        ExpandableListAdapter adapter=new DefineExpandableAdapter(this);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e("ex","txClick");
                return false;
            }
        });
        listView.setAdapter(adapter);
    }

    class DefineExpandableAdapter implements ExpandableListAdapter {
        private Context mContext;

        public DefineExpandableAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getGroupCount() {
            return group.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return child.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return group.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return child.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            Log.e("ex","group:"+i+"重绘");
            AbsListView.LayoutParams  layoutParams =new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout container =new LinearLayout(mContext);
            container.setLayoutParams(layoutParams);
            container.setOrientation(LinearLayout.VERTICAL);

            TextView textView =new TextView(mContext);
            textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
            textView.setPadding(40, 0, 0, 0);
            textView.setText((String) getGroup(i));
            textView.setTextColor(Color.BLACK);
            container.addView(textView);

            if(!b){
//                TextView textView1 =new TextView(mContext);
//                textView1.setBackgroundColor(Color.WHITE);
//                textView1.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
//                textView1.setPadding(40, 0, 0, 0);
//                textView1.setText((String) getChild(i,0));
//                textView1.setTextColor(Color.BLACK);
                View view1=getChildView(i,0,b,null,null);
                container.addView(view1);
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("ex","txClick");
                    }
                });
//                textView1.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return true;
//                    }
//                });
            }
            return container;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            AbsListView.LayoutParams  layoutParams =new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView =new TextView(mContext);
            textView.setLayoutParams(layoutParams);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
            textView.setPadding(40, 0, 0, 0);
            textView.setText((String) getChild(i,i1));
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int i) {

        }

        @Override
        public void onGroupCollapsed(int i) {

        }

        @Override
        public long getCombinedChildId(long l, long l1) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long l) {
            return 0;
        }
    }
}
