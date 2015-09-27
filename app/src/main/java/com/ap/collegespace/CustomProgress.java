package com.ap.collegespace;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class CustomProgress extends Dialog
{
    private GifImageView iv;

    public CustomProgress(Context context) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        iv = new GifImageView(context);
        layout.addView(iv, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        iv.setAnimatedGif(R.raw.progress_custom, GifImageView.TYPE.AS_IS);
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }
}
