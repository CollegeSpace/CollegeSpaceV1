package com.ap.collegespace;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

public class AddDateDialog extends Dialog
{
    private Activity mActivity;
    private SQLite mDatabase;
    private String mName;
    private View.OnClickListener mListner;

    public AddDateDialog(Activity parent, SQLite sql, String name, View.OnClickListener Listner)
    {
        super(parent);
        this.mActivity = parent;
        this.mDatabase = sql;
        this.mName = name;
        this.mListner = Listner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_date);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final DatePicker _dp = (DatePicker)findViewById(R.id.selector_date);
        final CheckBox _attend = (CheckBox)findViewById(R.id.attend);

        ((Button) findViewById(R.id.btn_no)).setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                    dismiss();
                }
        });

        ((Button)findViewById(R.id.btn_yes)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int dd = _dp.getDayOfMonth();
                int mm = _dp.getMonth();
                int yy = _dp.getYear();

                String da = ((dd < 9) ? "0" : "") + dd + GetMonth(mm) + yy;
                mDatabase.CreateKey(mName + "$w", da);
                if (_attend.isChecked())
                    mDatabase.CreateKey(mName + "$m", da);
                mListner.onClick(v);
                dismiss();
            }
        });
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
}
