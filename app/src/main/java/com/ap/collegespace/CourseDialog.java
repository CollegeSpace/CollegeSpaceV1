package com.ap.collegespace;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CourseDialog extends Dialog
{
    private SQLite mDatabase;
    private Activity mActivity;
    private EditText name, desc, percentage;
    private View.OnClickListener mListner;

    public CourseDialog(Activity parent, SQLite sql, View.OnClickListener Listner)
    {
        super(parent);
        this.mActivity = parent;
        this.mDatabase = sql;
        this.mListner = Listner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_course);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        name = (EditText)findViewById(R.id.course_name);
        desc = (EditText)findViewById(R.id.course_description);
        percentage = (EditText)findViewById(R.id.course_percentage);

        ((Button)findViewById(R.id.btn_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button)findViewById(R.id.btn_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String NameText = name.getText().toString();
                String DescText = desc.getText().toString();
                String Percentage = percentage.getText().toString();
                if (NameText.equals("Code") && DescText.equals("Name"))
                {
                    if (!mDatabase.KeyExist("$egg_3")) {
                        new CustomDialog(mActivity).show();
                        mDatabase.CreateKey("$egg_3", "wow");
                    }
                }
                int pee = 0;
                try
                {
                    pee = Integer.parseInt(Percentage);
                    if (NameText.length() == 0)
                        throw new Exception();
                }
                catch (Exception e)
                {
                    new CustomDialog(
                            mActivity,
                            "Failed",
                            "Illegal text",
                            null,
                            "Okay",
                            null,
                            null).show();
                    return;
                }
                if (pee > 100)
                {
                    new CustomDialog(
                            mActivity,
                            "LOL!",
                            "Percentage is always <= 100 by definition :P",
                            null,
                            "Okay",
                            null,
                            null).show();
                    return;
                }
                if (pee < 20)
                {
                    new CustomDialog(
                            mActivity,
                            "Sorry :(",
                            "We don't allow percentage < 20 xD",
                            null,
                            "Okay",
                            null,
                            null).show();
                    return;
                }
                if (NameText.length() > 25 || DescText.length() > 25)
                {
                    new CustomDialog(
                            mActivity,
                            "I'm Sorry :(",
                            "We don't allow more than 25 characters, because we love our app's UI :P",
                            null,
                            "Okay",
                            null,
                            null).show();
                    return;
                }
                if (NameText.length() == 0)
                {
                    new CustomDialog(
                            mActivity,
                            "Failed",
                            "Illegal text",
                            null,
                            "Okay",
                            null,
                            null).show();
                    return;
                }
                if (NameText.contains("'")
                    || DescText.contains("'")
                        || NameText.contains("$"))
                {
                    new CustomDialog(
                            mActivity,
                            "Failed",
                            "Don't try sql injection xD",
                            null,
                            "Okay :P",
                            null,
                            null).show();
                    return;
                }
                if (mDatabase.RecordExist(NameText))
                {
                    //Great we can't add this
                    new CustomDialog(
                            mActivity,
                            "Failed",
                            "Course name already exists",
                            null,
                            "Okay",
                            null,
                            null).show();
                }
                else
                {
                    mDatabase.CreateRecord(NameText, DescText, Percentage);
                    mListner.onClick(v);
                    dismiss();
                }
            }
        });
    }
}
