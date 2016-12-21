/**
 * Created on 2016/8/16
 */
package com.example.cuzki.mediaselectordemo;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * @author Cuzki
 */
public class AListFragment extends android.support.v4.app.Fragment {

    private ExpandableListView listView;

    private List<String> group= new ArrayList<String>();
    private List<List<String>> child= new ArrayList<List<String>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_list,container,false);
        listView= (ExpandableListView) view.findViewById(R.id.lv_list);
        group.add("第一組");
        group.add("第二組");
        group.add("第三組");

        for(int i=0;i<group.size();i++){
            List<String> s=new ArrayList<String>();
            for(int j=0;i<4;j++){
                s.add(group.get(i)+" 真好"+j);
            }
            child.add(s);
        }
        ExpandableListAdapter adapter=new DefineExpandableAdapter(getActivity());
        listView.setAdapter(adapter);
        return view;
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
            container.addView(textView);
            if(b){
                TextView textView1 =new TextView(mContext);
                textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);
                textView.setPadding(40, 0, 0, 0);
                textView.setText((String) getChild(i,0));
                container.addView(textView1);
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

            textView.setGravity(Gravity.CENTER_VERTICAL |Gravity.LEFT);

            textView.setPadding(40, 0, 0, 0);
            textView.setText((String) getChild(i,i1));
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
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
