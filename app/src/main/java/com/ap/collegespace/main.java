package com.ap.collegespace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

public class main extends SlidingActivity {

    private int CurrentTab = 0;
    private TextView title_label;
    private ScrollView screen_aboutUs;
    private ListView MainListView;
    private static String HomeFeed = "http://updates.collegespace.in/wp-json/posts?filter[posts_per_page]=10&calendar=true&page=";
    private static String NoticesFeed = "http://updates.collegespace.in/wp-json/posts?filter[posts_per_page]=10&filter[category_name]=notices&calendar=true&page=";
    private static String ResultsFeed = "http://updates.collegespace.in/wp-json/posts?filter[posts_per_page]=10&filter[category_name]=results&calendar=true&page=";
    private static ArrayList<FeedItem> feedList;
    private static ArrayList<RecordList> RcList;
    CustomProgress pDialog;
    SlidingMenu DragMenu;
    public SQLite mDatabase;
    Button btnLoad;
    SwipeRefreshLayout mSwipeRefreshLayout;

    static int FlipCounter = 0;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setBehindContentView(R.layout.menu);

        int additionalPadding = 20;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                additionalPadding += resources.getDimensionPixelSize(resourceId);
            }
        }
        DragMenu = getSlidingMenu();
        DragMenu.setMode(SlidingMenu.LEFT);
        DragMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        DragMenu.setShadowWidthRes(R.dimen.shadow_width);
        DragMenu.setShadowDrawable(R.drawable.shadow);
        DragMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        DragMenu.setFadeDegree(0.35f);

        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collegespace/.temp/").mkdirs();

        //Menu Handler
        final LinearLayout mHome = (LinearLayout)findViewById(R.id.btn_home);
        final LinearLayout mNotices = (LinearLayout)findViewById(R.id.btn_notices);
        final LinearLayout mResults = (LinearLayout)findViewById(R.id.btn_results);
        final LinearLayout mAboutUs = (LinearLayout)findViewById(R.id.btn_aboutus);
        final LinearLayout mStarred = (LinearLayout)findViewById(R.id.btn_starred);
        final LinearLayout mRecords = (LinearLayout)findViewById(R.id.btn_records);

        final ImageView mHome_Selected = (ImageView)findViewById(R.id.Home_Selected);
        final ImageView mNotices_Selected = (ImageView)findViewById(R.id.Notices_Selected);
        final ImageView mResults_Selected = (ImageView)findViewById(R.id.Results_Selected);
        final ImageView mAboutUs_Selected = (ImageView)findViewById(R.id.AboutUs_Selected);
        final ImageView mStarred_Selected = (ImageView)findViewById(R.id.Starred_Selected);
        final ImageView mRecords_Selected = (ImageView)findViewById(R.id.Records_Selected);

        LinearLayout.LayoutParams mAbout_Params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mAbout_Params.setMargins(0, 0, 0, additionalPadding);
        mAboutUs.setLayoutParams(mAbout_Params);

        title_label = (TextView)findViewById(R.id.screen_title);
        screen_aboutUs = (ScrollView)findViewById(R.id.scr_about_us);

        mDatabase = new SQLite(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Params.setMargins(0, 0, 0, additionalPadding - 20);
        mSwipeRefreshLayout.setLayoutParams(Params);

        ((Button)findViewById(R.id.btn_feedback)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                String aEmailList[] = {"collegespacensit@gmail.com", "aman.eureka@gmail.com"};
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
                emailIntent.setType("plain/text");
                startActivity(emailIntent);
            }
        });

        ((ImageView)findViewById(R.id.btn_menu_title)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DragMenu.toggle();
            }
        });


        View.OnClickListener event_click = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                int id = v.getId();
                int old = CurrentTab;
                if (mSwipeRefreshLayout.isRefreshing()) {
                    DragMenu.toggle();
                    return;
                }
                if (id == R.id.btn_home)
                {
                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 0;
                }
                else if (id == R.id.btn_notices)
                {
                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 1;
                }
                else if (id == R.id.btn_results)
                {
                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 2;
                }
                else if (id == R.id.btn_aboutus)
                {
                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 3;
                }
                else if (id == R.id.btn_starred)
                {
                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 4;
                }
                else if (id == R.id.btn_records)
                {
                    mAboutUs.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mNotices.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mNotices_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mResults_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mHome_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mAboutUs_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));
                    mStarred_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Light));

                    mRecords.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Dark));
                    mRecords_Selected.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Highlight));
                    CurrentTab = 5;
                }
                DragMenu.toggle();
                if (old != CurrentTab) {
                    Toggle_Content();
                    FlipCounter = 0;
                }
                else
                {
                    FlipCounter++;
                    if (FlipCounter > 5)
                    {
                        if (!mDatabase.KeyExist("$egg_2")) {
                            new CustomDialog(main.this).show();
                            mDatabase.CreateKey("$egg_2", "wow");
                        }
                        FlipCounter = 0;
                    }
                }
            }
        };

        mHome.setOnClickListener(event_click);
        mNotices.setOnClickListener(event_click);
        mResults.setOnClickListener(event_click);
        mAboutUs.setOnClickListener(event_click);
        mStarred.setOnClickListener(event_click);
        mRecords.setOnClickListener(event_click);

        MainListView = (ListView)findViewById(R.id.home_posts);
        btnLoad = new Button(this);
        btnLoad.setText("Load More");
        btnLoad.setTextColor(0xFFFFFFFF);
        btnLoad.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Swipe_C2));

        LinearLayout Footer = new LinearLayout(this);
        Footer.setBackgroundResource(R.drawable.shadow_1);
        Footer.setOrientation(LinearLayout.VERTICAL);
        Footer.addView(btnLoad, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        MainListView.addFooterView(Footer);

        LoadData("1", 0, false);

        // Event listener for Load More
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if (CurrentTab == 5)
                {
                    new CourseDialog(main.this, mDatabase, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoadData("6", 0, false);
                        }
                    }).show();
                    return;
                }
                if (feedList.isEmpty())
                    return;
                if (!ConnectedToInternet(false) || CurrentTab == 4)
                {
                    LoadData(String.valueOf(CurrentTab + 1), CurrentPage, true);
                    ++CurrentPage;
                    return;
                }
                String URL;
                switch (CurrentTab)
                {
                    case 0:
                        URL = HomeFeed;
                        break;
                    case 1:
                        URL = NoticesFeed;
                        break;
                    case 2:
                        URL = ResultsFeed;
                        break;
                    default:
                        return;
                }
                new DownloadFilesTask().execute(URL + (++CurrentPage), String.valueOf(CurrentTab + 1));
            }
        });

        MainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (mSwipeRefreshLayout.isRefreshing())
                    return;
                if (CurrentTab == 5)
                {
                    final RecordList item = (RecordList)MainListView.getItemAtPosition(position);
                    float percentage = item.GetPercentage();
                    new CustomDialog(main.this,
                            "Record: " + item.GetName(),
                            "Required: " + item.GetPercentageRequired() + "%\nCurrent: " + ((percentage < 0) ? "?" : String.valueOf(percentage)) + "%\nAttendance: " + item.GetPresentDays() + " out of " + item.GetWorkingDays(),
                            "Add/View",
                            "Delete",
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(main.this, Add_View_Activity.class);
                                    intent.putExtra("name", item.GetName());
                                    NotifyUpdateElement = 1;
                                    startActivity(intent);
                                }
                            },
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    new CustomDialog(
                                            main.this,
                                            "Delete Record: " + item.GetName(),
                                            "Really?",
                                            "Yes",
                                            "No",
                                            new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    mDatabase.DeleteRecord(item.GetName());
                                                    LoadData("6", 0, false);
                                                }
                                            },
                                            null).show();
                                }
                            }).show();
                }
                else
                {
                    Intent intent = new Intent(main.this, ListDetailActivity.class);
                    intent.putExtra("feed", (FeedItem) MainListView.getItemAtPosition(position));
                    startActivity(intent);
                    NotifyUpdateElement = position;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.Theme_Blue_Swipe_C1),
                getResources().getColor(R.color.Theme_Blue_Swipe_C2),
                getResources().getColor(R.color.Theme_Blue_Swipe_C3),
                getResources().getColor(R.color.Theme_Blue_Swipe_C4)
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                Sync_All();
            }
        });

        if (!mDatabase.KeyExist("$app"))
        {
            new CustomDialog(
                    main.this,
                    "Hi, there",
                    "Welcome to CollegeSpace App",
                    null,
                    "Hi!! ^_^",
                    null,
                    null).show();
            mDatabase.CreateKey("$app", "wow");
        }

        //Easter Egg
        if (((int)this.getIntent().getSerializableExtra("count")) > 10)
        {
            if (!mDatabase.KeyExist("$egg_4")) {
                new CustomDialog(main.this).show();
                mDatabase.CreateKey("$egg_4", "wow");
            }
        }
        ((TextView)findViewById(R.id.txt_amanp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCounter++;
                if (ClickCounter > 3)
                {
                    ClickCounter = 0;
                    new CustomDialog(
                            main.this,
                            "Hey hi",
                            "Do I know you?",
                            "Yes :P",
                            "No :/",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new CustomDialog(
                                            main.this,
                                            "oh! wow",
                                            "Then message me a good feedback on app :P",
                                            null,
                                            "Okay!",
                                            null,
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (!mDatabase.KeyExist("$egg_1")) {
                                                        new CustomDialog(main.this).show();
                                                        mDatabase.CreateKey("$egg_1", "wow");
                                                    }
                                                }
                                            }).show();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new CustomDialog(
                                            main.this,
                                            "No Problem :(",
                                            "I forgive you for this xD",
                                            null,
                                            "Thanks!",
                                            null,
                                            null).show();
                                }
                            }).show();
                }
            }
        });
    }
    private int ClickCounter = 0;

    private void Sync_All()
    {
        if (!ConnectedToInternet(true))
        {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (CurrentTab <= 2)
            new SyncFilesTask().execute(HomeFeed);
        else if (CurrentTab == 4)
        {
            LoadData("5", 0, false);
            mSwipeRefreshLayout.setRefreshing(false);
        }
        else if (CurrentTab == 5)
        {
            LoadData("6", 0, false);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (NotifyUpdateElement != -1)
        {
            if (CurrentTab == 5)
            {
                LoadData("6", 0, false);
            }
            else
            {
                View v = MainListView.getChildAt(NotifyUpdateElement -
                        MainListView.getFirstVisiblePosition());
                FeedItem current = feedList.get(NotifyUpdateElement);
                Cursor result = mDatabase.GetPost(current.ID(), "0");
                if (result.moveToFirst())
                    current.mAttrib = result.getInt(result.getColumnIndex("fav"));
                if (current.Starred())
                    v.findViewById(R.id.fav).setVisibility(View.VISIBLE);
                else
                    v.findViewById(R.id.fav).setVisibility(View.GONE);
            }
            NotifyUpdateElement = -1;
        }
    }

    private int CurrentPage = 0;
    private void Toggle_Content()
    {
        String Title;
        CurrentPage = 0;
        switch (CurrentTab)
        {
            case 0:
                Title = "Home";
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                screen_aboutUs.setVisibility(View.GONE);
                LoadData("1", 0, false);
                break;
            case 1:
                Title = "Notices";
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                screen_aboutUs.setVisibility(View.GONE);
                LoadData("2", 0, false);
                break;
            case 2:
                Title = "Results";
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                screen_aboutUs.setVisibility(View.GONE);
                LoadData("3", 0, false);
                break;
            case 3:
                Title = "About";
                mSwipeRefreshLayout.setVisibility(View.GONE);
                screen_aboutUs.setVisibility(View.VISIBLE);
                break;
            case 4:
                Title = "Starred";
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                screen_aboutUs.setVisibility(View.GONE);
                LoadData("5", 0, false);
                break;
            case 5:
                Title = "Records";
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                screen_aboutUs.setVisibility(View.GONE);
                LoadData("6", 0, false);
                break;
            default:
                Title="ERROR";
                break;
        }
        title_label.setText(Title);
    }

    int NotifyUpdateElement = -1;
    public void updateList(int currentPos, String category)
    {
        if (category == "6")
        {
            btnLoad.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Swipe_C3));
            btnLoad.setText("Add Course");
        }
        else if (feedList.isEmpty())
        {
            btnLoad.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Swipe_C3));
            btnLoad.setText("Swipe down to Refresh!");
        }
        else
        {
            btnLoad.setBackgroundColor(getResources().getColor(R.color.Theme_Blue_Swipe_C2));
            btnLoad.setText("Load More");
        }

        if (category == "6")
            MainListView.setAdapter(new RecordAdapter(this, RcList));
        else
            MainListView.setAdapter(new CustomAdapter(this, feedList));
        MainListView.setSelection(currentPos);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && !DragMenu.isMenuShowing())
        {
            new CustomDialog(
                    this,
                    "Exit the app",
                    "Really? We <3 you!",
                    "Yes ;-)",
                    "Nope :P",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    },
                    null).show();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void  LoadData(String Category, int page, boolean Refresh)
    {
        int current = MainListView.getFirstVisiblePosition();
        try
        {
            if (Category.charAt(0) <= '5')
            {
                if (!Refresh)
                {
                    current = 0;
                    feedList = new ArrayList<FeedItem>();
                }
                Cursor result = mDatabase.GetPosts(page, Category);
                Hashtable<Integer, Integer> hash = null;
                if (Category == "5")
                    hash = new Hashtable<Integer, Integer>();
                if (result.moveToFirst())
                {
                    do
                    {
                        //For Category == 5, SQL query will fetch all those posts whose
                        //attribute entry has 2nd bit on :D ! --> in short Starred post :P
                        int id = result.getInt(result.getColumnIndex("post_id"));
                        if (hash != null)
                        {
                            if (hash.containsKey(id))
                                continue;
                            else
                                hash.put(id, 0);
                        }
                        boolean Contains = false;
                        if (Refresh) {
                            for (int i = 0; i < feedList.size(); i++)
                                if (feedList.get(i).ID() == id) {
                                    Contains = true;
                                    break;
                                }
                        }
                        if (!Contains) {
                            FeedItem f = new FeedItem(
                                    id,
                                    result.getString(result.getColumnIndex("title")),
                                    result.getString(result.getColumnIndex("date")),
                                    result.getString(result.getColumnIndex("url")),
                                    result.getString(result.getColumnIndex("content")),
                                    result.getString(result.getColumnIndex("author")),
                                    result.getString(result.getColumnIndex("attach")),
                                    result.getInt(result.getColumnIndex("fav")));
                            feedList.add(f);
                        }
                    }
                    while (result.moveToNext());
                }
                result.close();
            }
            else
            {
                if (!Refresh)
                {
                    current = 0;
                    RcList = new ArrayList<RecordList>();
                }

                Cursor result = mDatabase.GetAllRecords();
                if (result.moveToFirst())
                {
                    do
                    {
                        String Name = result.getString(result.getColumnIndex("name"));
                        RecordList f = new RecordList(
                                main.this,
                                Name.substring(0, Name.length() - 2),
                                result.getString(result.getColumnIndex("value")));
                        RcList.add(f);
                    }
                    while (result.moveToNext());
                }
                result.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        updateList(current, Category);
    }

    public String CustomDate(String dateString)
    {
        int year = 2000 + (dateString.charAt(2) - '0') * 10 + (dateString.charAt(3) - '0');
        int month = (dateString.charAt(5) - '0') * 10 + (dateString.charAt(6) - '0');
        int date = (dateString.charAt(8) - '0') * 10 + (dateString.charAt(9) - '0');
        int hour = (dateString.charAt(11) - '0') * 10 + (dateString.charAt(12) - '0');
        int minute = (dateString.charAt(14) - '0') * 10 + (dateString.charAt(15) - '0');

        return  date + GetMonth(month) + year + " at " + (hour <= 9 ? "0" : "") + hour + ":" + (minute <= 9 ? "0" : "") + minute;
    }
    private  String GetMonth(int a)
    {
        switch (a)
        {
            case 1: return  " Jan ";
            case 2: return  " Feb ";
            case 3: return  " Mar ";
            case 4: return  " Apr ";
            case 5: return  " May ";
            case 6: return  " June ";
            case 7: return  " July ";
            case 8: return  " Aug ";
            case 9: return  " Sep ";
            case 10: return  " Oct ";
            case 11: return  " Nov ";
            case 12: return  " Dec ";
            default:
                return  "NaN";
        }
    }

    private String ExtractAttachmentsAsString(String Content)
    {
        final Pattern p = Pattern.compile("(?i)<a\\s+[^>]*?href=(\"|')([^\"']+)\\1");
        final Matcher m = p.matcher(Content);

        String media = "";
        while (m.find())
        {
            String temp = m.group(2);
            Log.i("Attachments", temp);
            if (temp.endsWith(".png") || temp.endsWith(".jpg") || temp.endsWith(".jpeg"))
                media += m.group(2) + ",";
        }
        return media;
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
            new CustomDialog(
                    this,
                    "Sync Failed",
                    "Check your internet connection xD",
                    null,
                    "I know :P",
                    null,
                    null).show();
        }
        return false;
    }

    private class SyncFilesTask extends AsyncTask<String, Integer, Integer>
    {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Integer doInBackground(String... params)
        {
            String rurl = params[0];
            Integer NewPostCount = 0;
            try
            {
                JSONParser jParser = new JSONParser();
                JSONArray json = jParser.getJSONFromUrl(rurl);

                Cursor Starred = mDatabase.GetStarred();
                Hashtable<Integer, Integer> hash = new Hashtable<Integer, Integer>();
                if (Starred.moveToFirst())
                {
                    do
                    {
                        int current = Starred.getInt(Starred.getColumnIndex("post_id"));
                        if (!hash.containsKey(current)) {
                            hash.put(current, 1);
                        }
                    }
                    while (Starred.moveToNext());
                }
                Starred.close();
                mDatabase.DeletePOSTTable();
                for (int i = 0; i < json.length(); i++)
                {
                    JSONObject obj = json.getJSONObject(i);
                    int ID = obj.getInt("ID");
                    String content = obj.getString("content");
                    String aut = obj.getJSONObject("author").getString("name");
                    FeedItem f = new FeedItem(
                            ID,
                            obj.getString("title"),
                            CustomDate(obj.getString("date")),
                            obj.getString("link"),
                            content.replaceAll("<img .*? />", ""),
                            aut,
                            ExtractAttachmentsAsString(content),
                            (hash.containsKey(ID) ? 2 : 0)
                    );
                    mDatabase.InsertPost(f, 1);
                    int category = obj.getJSONObject("terms").getJSONArray("category").getJSONObject(0).getInt("ID");
                    if (category == 3)//Results
                        mDatabase.InsertPost(f, 3);
                    else if (category == 5)//Notices
                        mDatabase.InsertPost(f, 2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  NewPostCount;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            mSwipeRefreshLayout.setRefreshing(false);
            LoadData(String.valueOf(CurrentTab + 1), 0, false);
            if (result != 0)
            {
                //TODO: Notify number of updated posts
            }
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void>
    {
        @Override
        protected void onPreExecute()
        {
            pDialog = new CustomProgress(main.this);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String rurl = params[0];
            try {
                JSONParser jParser = new JSONParser();
                JSONArray json = jParser.getJSONFromUrl(rurl);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject obj = json.getJSONObject(i);
                    int ID = obj.getInt("ID");
                    Cursor result = mDatabase.GetPost(ID, params[1]);
                    if (!result.moveToFirst()) {
                        String content = obj.getString("content");
                        FeedItem f = new FeedItem(
                                obj.getInt("ID"),
                                obj.getString("title"),
                                CustomDate(obj.getString("date")),
                                obj.getString("link"),
                                content.replaceAll("<img .*? />", ""),
                                obj.getJSONObject("author").getString("name"),
                                ExtractAttachmentsAsString(content),
                                0
                        );
                        mDatabase.InsertPost(f, params[1].charAt(0) - '0');
                    }
                    result.close();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            LoadData(String.valueOf(CurrentTab + 1), CurrentPage - 1, true);
            pDialog.dismiss();
        }
    }
}
