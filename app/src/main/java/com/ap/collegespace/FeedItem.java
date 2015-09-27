package com.ap.collegespace;

import java.io.Serializable;

public class FeedItem  implements Serializable
{
    private String mTitle;
    private String mDate;
    private String mCustomDate;
    private String mUrl;
    private String  mContent;
    private String mAuthor;
    private Integer mID;
    private String mAttachments;
    public int mAttrib;

    public FeedItem(Integer id, String Title, String Date, String Url, String Content, String Author, String Attachments, int Attrib)
    {
        this.mID = id;
        this.mTitle = Title;
        this.mDate = Date;
        this.mUrl = Url;
        this.mContent = Content;
        this.mAuthor = Author;
        this.mAttachments = Attachments;
        this.mAttrib = Attrib;
    }

    public Integer ID()
    { return mID; }

    public String Title()
    { return mTitle; }

    public String Date()
    { return mDate; }

    public  String Url()
    { return  mUrl; }

    public  String  Content()
    { return  mContent; }

    public  String Author()
    { return  mAuthor; }

    public  String Attachments()
    { return mAttachments; }

    public Integer Attribute()
    { return mAttrib; }

    public boolean Starred()
    {
        return (mAttrib & 0x2) != 0;
    }

    public String CustomDate()
    {
        return  mCustomDate;
    }

    @Override
    public String toString() {
        return mAuthor + "=" + mUrl;
    }
}
