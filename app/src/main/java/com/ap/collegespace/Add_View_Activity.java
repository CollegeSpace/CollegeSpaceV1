package com.ap.collegespace;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class Add_View_Activity extends Activity
{
    SQLite mDatabase;
    String name;
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_view);

        name = (String) this.getIntent().getSerializableExtra("name");
        mDatabase = new SQLite(this);
        mListView = (ListView)findViewById(R.id.dates);

        int additionalPadding = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                additionalPadding += resources.getDimensionPixelSize(resourceId);
            }
        }

        FrameLayout.LayoutParams Params = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Params.setMargins(0, 0, 0, additionalPadding);
        ((LinearLayout)findViewById(R.id.layout_main)).setLayoutParams(Params);

        ((Button)findViewById(R.id.btn_add_date)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new AddDateDialog(Add_View_Activity.this, mDatabase, name, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Refresh();
                    }
                }).show();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id)
            {
                final String item = (String)a.getItemAtPosition(position);
                new CustomDialog(
                        Add_View_Activity.this,
                        "Delete Record: " + item,
                        "Really?",
                        "Yes",
                        "No",
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mDatabase.DeleteRecordDetail(name, item);
                                Refresh();
                            }
                        },
                        null).show();
            }
        });

        Refresh();
    }

    private void Refresh()
    {
        Cursor mResult_w = mDatabase.GetKey(name + "$w");
        Cursor mResult_m = mDatabase.GetKey(name + "$m");

        Hashtable<String, Integer> Working = new Hashtable<String, Integer>();
        Hashtable<String, Integer> Present = new Hashtable<String, Integer>();
        if (mResult_w.moveToFirst())
        {
            do
            {
                String val = mResult_w.getString(mResult_w.getColumnIndex("value"));
                if (Working.containsKey(val))
                    Working.put(val, Working.get(val) + 1);
                else
                    Working.put(val, 1);
            }
            while (mResult_w.moveToNext());
        }
        mResult_w.close();
        if (mResult_m.moveToFirst())
        {
            do
            {
                String val = mResult_m.getString(mResult_m.getColumnIndex("value"));
                if (Present.containsKey(val))
                    Present.put(val, Present.get(val) + 1);
                else
                    Present.put(val, 1);
            }
            while (mResult_m.moveToNext());
        }
        mResult_m.close();

        ArrayList<String> itemA, itemB;
        itemA = new ArrayList<String>();
        itemB = new ArrayList<String>();
        Set<String> keys = Working.keySet();
        for(String key: keys)
        {
            itemA.add(key);
            if (Present.containsKey(key))
                itemB.add(Present.get(key) + " out of " + Working.get(key));
            else
                itemB.add("0 out of " + Working.get(key));
        }
        mListView.setAdapter(new AddView_Adapter(this, itemA, itemB));
    }
}