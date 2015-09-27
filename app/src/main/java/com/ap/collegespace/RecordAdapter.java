package com.ap.collegespace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<RecordList> listData;

    private static int COLOR_NO_STATUS = 0xFFEEB269;
    private static int COLOR_SAFE_STATUS = 0xFF0074c1;
    private static int COLOR_UNSAFE_STATUS = 0xFFDC5753;

    public RecordAdapter(Context context, ArrayList<RecordList> Record)
    {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        listData = Record;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_record, null);
            holder = new ViewHolder();
            holder.Name = (TextView) convertView.findViewById(R.id.record_name);
            holder.Description = (TextView) convertView.findViewById(R.id.record_description);
            holder.Status = (ImageView)convertView.findViewById(R.id.record_status);
            holder.Percentage = (TextView)convertView.findViewById(R.id.record_percentage);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordList newsItem = (RecordList) listData.get(position);
        holder.Name.setText(newsItem.GetName());
        holder.Description.setText(newsItem.GetDescription());
        float percentage = newsItem.GetPercentage();
        int required = newsItem.GetPercentageRequired();
        if (percentage < 0) {
            holder.Status.setBackgroundColor(COLOR_NO_STATUS);
            holder.Percentage.setTextColor(COLOR_NO_STATUS);
            holder.Percentage.setText("?");
        }
        else if (percentage >= required) {
            holder.Status.setBackgroundColor(COLOR_SAFE_STATUS);
            holder.Percentage.setTextColor(COLOR_SAFE_STATUS);
            holder.Percentage.setText(String.valueOf(percentage));
        }
        else {
            holder.Status.setBackgroundColor(COLOR_UNSAFE_STATUS);
            holder.Percentage.setTextColor(COLOR_UNSAFE_STATUS);
            holder.Percentage.setText(String.valueOf(percentage));
        }
        return convertView;
    }

    static class ViewHolder
    {
        TextView Name;
        TextView Description;
        TextView Percentage;
        ImageView Status;
    }
}
