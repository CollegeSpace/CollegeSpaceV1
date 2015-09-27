package com.ap.collegespace;

import java.io.Serializable;

public class RecordList implements Serializable
{
    private String mName;
    private String mDescription;
    private Integer mPercentageRequired;
    private main mParent;

    public RecordList(main parent, String name, String Description)
    {
        this.mParent = parent;
        this.mName = name;
        this.mDescription = Description;
        this.mPercentageRequired = mParent.mDatabase.GetPercentageRequired(mName);
    }

    public Integer GetPercentageRequired()
    {
        return  mPercentageRequired;
    }

    public Integer GetWorkingDays()
    {
        return  mParent.mDatabase.GetWorkingDays(mName);
    }

    public Integer GetPresentDays()
    {
        return  mParent.mDatabase.GetPresentDays(mName);
    }

    public float GetPercentage()
    {
        int working = GetWorkingDays();
        if (working == 0)
            return  -1.0f;
        return  (GetPresentDays() * 100) / working;
    }

    public String GetName()
    {
        return mName;
    }

    public String GetDescription()
    {
        return mDescription;
    }
}
