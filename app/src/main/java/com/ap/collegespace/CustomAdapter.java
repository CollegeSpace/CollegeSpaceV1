package com.ap.collegespace;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter
{
    private ArrayList<FeedItem> listData;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public CustomAdapter(Context context, ArrayList<FeedItem> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
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
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_post, null);
            holder = new ViewHolder();
            holder.Date = (TextView) convertView.findViewById(R.id.post_date);
            holder.Title = (TextView) convertView.findViewById(R.id.post_title);
            holder.Star = (ImageView) convertView.findViewById(R.id.fav);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FeedItem newsItem = (FeedItem) listData.get(position);
        holder.Date.setText(newsItem.Date());
        holder.Title.setText(newsItem.Title());
        if (newsItem.Starred())
            holder.Star.setVisibility(View.VISIBLE);
        else
            holder.Star.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {
        TextView Date;
        TextView Title;
        ImageView Star;
    }
}
