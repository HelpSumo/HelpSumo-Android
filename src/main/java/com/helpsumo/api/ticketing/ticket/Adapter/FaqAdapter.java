package com.helpsumo.api.ticketing.ticket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.helpsumo.api.ticketing.R;
import com.helpsumo.api.ticketing.ticket.ClassObjects.Faqdetails;

import java.util.ArrayList;


public class FaqAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Faqdetails> groups;

    public FaqAdapter(Context context, ArrayList<Faqdetails> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.groups.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.faq_listrow, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.faques);
        final String status = groups.get(groupPosition).getStatus();
        if (status.equals("1")) {
            tv.setText(groups.get(groupPosition).getFaq());
        } else {
            tv.setVisibility(View.GONE);
        }
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_child, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.faqanswer);

        tv.setText(groups.get(groupPosition).getFaqanswer());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

