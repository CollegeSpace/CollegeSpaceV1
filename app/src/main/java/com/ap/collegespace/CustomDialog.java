package com.ap.collegespace;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener
{
    private Activity c;
    private Dialog d;
    private Button yes, no;
    private String TITLE, CONTENT, YES_BTN, NO_BTN;
    private View.OnClickListener ListnerA, ListnerB;
    private boolean IsEsterEggDialog = false;

    public CustomDialog(Activity a)
    {
        super(a);
        this.c = a;
        this.TITLE = "Congratulation!!";
        this.CONTENT = "";
        this.IsEsterEggDialog = true;
    }

    public CustomDialog(Activity a, String title, String content, String y, String n, View.OnClickListener mListner, View.OnClickListener mListner2)
    {
        super(a);
        this.c = a;
        this.TITLE = title;
        this.CONTENT = content;
        this.YES_BTN = y;
        this.NO_BTN = n;
        this.ListnerA = mListner;
        this.ListnerB = mListner2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exit_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        ((TextView)findViewById(R.id.txt_title)).setText(TITLE);
        ((TextView)findViewById(R.id.txt_content)).setText(CONTENT);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        if (IsEsterEggDialog)
            ((ImageView)findViewById(R.id.img_esteregg)).setVisibility(View.VISIBLE);
        if (YES_BTN != null)
            yes.setText(YES_BTN);
        else
            ((LinearLayout)findViewById(R.id.yes_container)).setVisibility(View.GONE);
        if (NO_BTN != null)
            no.setText(NO_BTN);
        else
            ((LinearLayout)findViewById(R.id.no_container)).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_yes:
                ListnerA.onClick(v);
                break;
            case R.id.btn_no:
                if (ListnerB != null)
                    ListnerB.onClick(v);
                else
                    dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
