package com.ap.collegespace;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class previewActivity extends Activity
{
    DownloadImageTask BackTask;
    ImageView Download;
    Bitmap MainBitamp = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_media);

        String URL = (String)this.getIntent().getSerializableExtra("url");
        Log.i("Incoming URL", URL);

        final TouchImageView pan =(TouchImageView) findViewById(R.id.image_panel);
        Download = (ImageView)findViewById(R.id.download);
        Download.setVisibility(View.GONE);

        BackTask = new DownloadImageTask(pan);
        BackTask.execute(URL);

        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (MainBitamp == null)
                    return;
                OutputStream fOut = null;
                String FileName = "NaN";
                try {
                    File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Collegespace/");
                    root.mkdirs();
                    FileName = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss")
                            .format(Calendar.getInstance().getTime()) + ".png";
                    File sdImageMainDirectory = new File(root, FileName);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                }
                catch (Exception e)
                {
                    Toast.makeText(previewActivity.this, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
                }

                try
                {
                    MainBitamp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {  }
                Toast.makeText(previewActivity.this, "Saved Successfully \"" + FileName + "\"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onStop ()
    {
        BackTask.cancel(true);
        super.onStop();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;
        CustomProgress pDialog;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            pDialog = new CustomProgress(previewActivity.this);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            MainBitamp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                MainBitamp = BitmapFactory.decodeStream(in,null, options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return MainBitamp;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
            Download.setVisibility(View.VISIBLE);
            pDialog.dismiss();
        }
    }
}
