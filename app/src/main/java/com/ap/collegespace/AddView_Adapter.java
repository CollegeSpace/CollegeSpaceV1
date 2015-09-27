package com.ap.collegespace;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AddView_Adapter extends BaseAdapter
{
    private ArrayList<String> listData;
    private ArrayList<String> listData2;
    private LayoutInflater layoutInflater;
    private Context mContext;

    @Override
    public int getCount() { return listData.size(); }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public AddView_Adapter(Context context, ArrayList<String> listData, ArrayList<String> listdata2)
    {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.listData = listData;
        this.listData2 = listdata2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_post, null);
            holder = new ViewHolder();
            holder.Date = (TextView) convertView.findViewById(R.id.post_date);
            holder.Title = (TextView) convertView.findViewById(R.id.post_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Date.setText(listData.get(position));
        holder.Title.setText(listData2.get(position));
        return convertView;
    }

    static class ViewHolder
    {
        TextView Date;
        TextView Title;
    }
}

