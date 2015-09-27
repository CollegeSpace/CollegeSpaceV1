package com.ap.collegespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class main_splash extends Activity
{
    static int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        final GifImageView loder = (GifImageView)findViewById(R.id.splash_loader);

        ((ImageView)findViewById(R.id.logo_splash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
            }
        });

        loder.setAnimatedGif(R.raw.main_ld, GifImageView.TYPE.FIT_CENTER);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(main_splash.this, main.class);
                i.putExtra("count", counter);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
