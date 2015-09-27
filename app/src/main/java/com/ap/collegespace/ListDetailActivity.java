package com.ap.collegespace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ListDetailActivity extends Activity
{
    private FeedItem feed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        feed = (FeedItem) this.getIntent().getSerializableExtra("feed");

        if (null != feed) {
            final TextView Author = (TextView) findViewById(R.id.user_name);
            final TextView Title = (TextView) findViewById(R.id.post_title);
            final TextView Date = (TextView) findViewById(R.id.post_date);
            final TextView Content = (TextView) findViewById(R.id.post_content);
            final ImageView Star = (ImageView) findViewById(R.id.fav);
            final SQLite mDatabase = new SQLite(this);

            Author.setText(feed.Author());
            Title.setText(feed.Title());
            Date.setText(feed.Date());
            Content.setText(Html.fromHtml(feed.Content()));
            Content.setMovementMethod(LinkMovementMethod.getInstance());
            //For webview -- better tags support
            //Content.loadDataWithBaseURL(null, feed.Content(), "text/html", "utf-8", null);
            //Content.setBackgroundColor(Color.TRANSPARENT);

            if (feed.Starred())
                Star.setBackgroundResource(R.drawable.star);
            else
                Star.setBackgroundResource(R.drawable.star_dis);

            if (feed.Attachments().startsWith("http"))
            {
                String[] Medias = feed.Attachments().split(",");
                Log.i("Attchement", feed.Attachments());
                Log.i("Links_Count", String.valueOf(Medias.length));
                final LinearLayout Attach = (LinearLayout)findViewById(R.id.attach_status);
                int col = getResources().getColor(R.color.Theme_Blue_Swipe_C3);
                LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mParams.setMargins(0, 0, 0, 5);
                for (int i = 0; i < Medias.length; i++)
                {
                    final String URL = Medias[i];
                    TextView lbl = new TextView(this);
                    lbl.setText(URL.substring(URL.lastIndexOf('/') + 1));
                    lbl.setTextColor(col);
                    lbl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            if (!ConnectedToInternet(true))
                                return;
                            Intent intent = new Intent(ListDetailActivity.this, previewActivity.class);
                            intent.putExtra("url", URL);
                            startActivity(intent);
                        }
                    });
                    lbl.setLayoutParams(mParams);
                    Attach.addView(lbl);
                }
            }
            else
                ((LinearLayout)findViewById(R.id.attachement_box)).setVisibility(View.GONE);

            Star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0)
                {
                    if (feed.Starred())
                    {
                        Star.setBackgroundResource(R.drawable.star_dis);
                        feed.mAttrib &= 0xFFFFFFFD;
                        mDatabase.StarPost(feed.mAttrib, feed.ID());
                    }
                    else
                    {
                        Star.setBackgroundResource(R.drawable.star);
                        feed.mAttrib |= 0x2;
                        mDatabase.StarPost(feed.mAttrib, feed.ID());
                    }
                }
            });
        }
    }

    public boolean ConnectedToInternet(boolean Dialog)
    {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }

        if (Dialog)
        {
            CustomDialog cd = new CustomDialog(
                    this,
                    "Failed to Load",
                    "Check your internet connection xD",
                    null,
                    "I know :P",
                    null,
                    null);
            cd.show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}
