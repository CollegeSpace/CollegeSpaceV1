package com.ap.collegespace;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Collegespace_data.db";
    private static final int DATABASE_VERSION = 2;
    private static final String POSTS_TABLE_NAME = "posts";
    private static final String RECORDS_TABLE_NAME = "records";

    public static final  String POSTS_COLUMN_PRIMARY = "_id";
    public static final  String POSTS_COLUMN_ID = "post_id";
    public static final  String POSTS_COLUMN_TITLE = "title";
    public static final  String POSTS_COLUMN_DATE = "date";
    public static final  String POSTS_COLUMN_URL = "url";
    public static final  String POSTS_COLUMN_CONTENT = "content";
    public static final  String POSTS_COLUMN_AUTHOR = "author";
    public static final  String POSTS_COLUMN_ATTACHMENTS = "attach";
    public static final  String POSTS_COLUMN_STARRED = "fav";
    public static final  String POSTS_COLUMN_CATEGORY = "category";

    public static final String RECORDS_COLUMN_PRIMARY = "_id";
    public static final String RECORDS_COLUMN_NAME = "name";
    //name$d - Description
    //name%p - percentage
    //name$w - working days
    //name$m - present
    public static final String RECORDS_COLUMN_VALUE = "value";

    public SQLite(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + POSTS_TABLE_NAME +
                        "(" + POSTS_COLUMN_PRIMARY + " INTEGER PRIMARY KEY, " +
                        POSTS_COLUMN_ID + " INTEGER, " +
                        POSTS_COLUMN_TITLE + " TEXT, " +
                        POSTS_COLUMN_DATE + " TEXT, " +
                        POSTS_COLUMN_URL + " TEXT, " +
                        POSTS_COLUMN_CONTENT + " TEXT, " +
                        POSTS_COLUMN_AUTHOR + " TEXT, " +
                        POSTS_COLUMN_ATTACHMENTS + " TEXT, " +
                        POSTS_COLUMN_CATEGORY + " INTEGER, " +
                        POSTS_COLUMN_STARRED + " INTEGER)"
        );
        db.execSQL("CREATE TABLE " + RECORDS_TABLE_NAME
                + "(" + RECORDS_COLUMN_PRIMARY + " INTEGER PRIMARY KEY, " +
                RECORDS_COLUMN_NAME + " TEXT, " +
                RECORDS_COLUMN_VALUE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECORDS_TABLE_NAME);
        onCreate(db);
    }

    public void DeletePOSTTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE_NAME);
        db.execSQL(
                "CREATE TABLE " + POSTS_TABLE_NAME +
                        "(" + POSTS_COLUMN_PRIMARY + " INTEGER PRIMARY KEY, " +
                        POSTS_COLUMN_ID + " INTEGER, " +
                        POSTS_COLUMN_TITLE + " TEXT, " +
                        POSTS_COLUMN_DATE + " TEXT, " +
                        POSTS_COLUMN_URL + " TEXT, " +
                        POSTS_COLUMN_CONTENT + " TEXT, " +
                        POSTS_COLUMN_AUTHOR + " TEXT, " +
                        POSTS_COLUMN_ATTACHMENTS + " TEXT, " +
                        POSTS_COLUMN_CATEGORY + " INTEGER, " +
                        POSTS_COLUMN_STARRED + " INTEGER)"
        );
    }

    public boolean CreateKey(String name, String Value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(RECORDS_COLUMN_NAME, name);
        contentValues.put(RECORDS_COLUMN_VALUE, Value);
        db.insert(RECORDS_TABLE_NAME, null, contentValues);
        return  true;
    }

    public boolean CreateRecord(String Name, String Description, String PercentageRequired)
    {
        CreateKey(Name + "$d", Description);
        CreateKey(Name + "$p", PercentageRequired);
        return true;
    }

    public boolean DeleteKey(String Name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECORDS_TABLE_NAME, RECORDS_COLUMN_NAME + " = ? ", new String[]{Name});
        return true;
    }

    public boolean DeleteKeyValue(String Name, String Value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECORDS_TABLE_NAME, RECORDS_COLUMN_NAME + " = ? AND " + RECORDS_COLUMN_VALUE + " = ? ", new String[]{Name, Value});
        return true;
    }

    public boolean DeleteRecord(String Name)
    {
        DeleteKey(Name);
        DeleteKey(Name + "$d");
        DeleteKey(Name + "$p");
        return true;
    }

    public boolean DeleteRecordDetail(String Name, String Value)
    {
        DeleteKeyValue(Name + "$w", Value);
        DeleteKeyValue(Name + "$m", Value);
        return true;
    }

    public Cursor GetKey(String Name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + RECORDS_TABLE_NAME
                + " WHERE " + RECORDS_COLUMN_NAME + "='" + Name + "'", null);
        return  res;
    }

    public String GetDescription(String Name)
    {
        Cursor val = GetKey(Name + "$d");
        String result = null;
        if (val.moveToFirst())
        {
             result = val.getString(val.getColumnIndex(RECORDS_COLUMN_VALUE));
        }
        val.close();
        return result;
    }

    public Integer GetPercentageRequired(String Name)
    {
        Cursor val = GetKey(Name + "$p");
        Integer result = 0;
        if (val.moveToFirst())
        {
            result = Integer.parseInt(val.getString(val.getColumnIndex(RECORDS_COLUMN_VALUE)));
        }
        val.close();
        return result;
    }

    public Integer GetWorkingDays(String Name)
    {
        Cursor val = GetKey(Name + "$w");
        Integer result = val.getCount();
        val.close();
        return result;
    }

    public Integer GetPresentDays(String Name)
    {
        Cursor val = GetKey(Name + "$m");
        Integer result = val.getCount();
        val.close();
        return result;
    }

    public Cursor GetAllRecords()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + RECORDS_TABLE_NAME
                + " WHERE " + RECORDS_COLUMN_NAME + " LIKE '%$d'", null);
        return  res;
    }

    public boolean RecordExist(String Name)
    {
        return KeyExist(Name + "$d");
    }

    public boolean KeyExist(String Name)
    {
        Cursor res = GetKey(Name);
        boolean exist = res.moveToFirst();
        res.close();
        return exist;
    }

    public boolean UpdateKey(String Name, String Value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORDS_COLUMN_VALUE, Value);
        db.update(RECORDS_TABLE_NAME, contentValues, RECORDS_COLUMN_NAME + " = ? ", new String[]{Name});
        return true;
    }

    public boolean InsertPost(FeedItem aFeedItem, Integer category)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(POSTS_COLUMN_ID, aFeedItem.ID());
        contentValues.put(POSTS_COLUMN_TITLE, aFeedItem.Title());
        contentValues.put(POSTS_COLUMN_DATE, aFeedItem.Date());
        contentValues.put(POSTS_COLUMN_URL, aFeedItem.Url());
        contentValues.put(POSTS_COLUMN_CONTENT, aFeedItem.Content());
        contentValues.put(POSTS_COLUMN_AUTHOR, aFeedItem.Author());
        contentValues.put(POSTS_COLUMN_ATTACHMENTS, aFeedItem.Attachments());
        contentValues.put(POSTS_COLUMN_STARRED, aFeedItem.Attribute());
        contentValues.put(POSTS_COLUMN_CATEGORY, category);

        db.insert(POSTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean StarPost(int val, int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(POSTS_COLUMN_STARRED, val);
        db.update(POSTS_TABLE_NAME, contentValues, POSTS_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Cursor GetPost(int ID, String cat)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + POSTS_TABLE_NAME
                + " WHERE " + POSTS_COLUMN_ID + "=" + ID
                + (cat == "0" ? "" : " AND " + POSTS_COLUMN_CATEGORY + "=" + cat), null);
        return  res;
    }

    public Cursor GetPosts(int page, String cat) {
        page *= 10;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + POSTS_TABLE_NAME
                + " WHERE " + (cat == "5" ? (POSTS_COLUMN_STARRED + "& 2 != 0") : (POSTS_COLUMN_CATEGORY + "=" + cat))
                + " ORDER BY " + POSTS_COLUMN_ID
                + " DESC LIMIT " + page + ", 10", null);
        return res;
    }

    public  Cursor GetStarred()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + POSTS_TABLE_NAME
                + " WHERE " + POSTS_COLUMN_STARRED + "& 2 != 0"
                + " ORDER BY " + POSTS_COLUMN_ID
                + " DESC", null);
        return res;
    }
}
