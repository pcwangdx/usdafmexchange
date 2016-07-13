package com.mahoneydev.usdafmexchange;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by mahoneydev on 5/25/2016.
 */
public class LoadImage extends AsyncTask<String, String, Bitmap> {
    public ImageView img;
    private Bitmap bitmap;

    @Override
    protected Bitmap doInBackground(String... args) {
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap image) {

        if (image != null) {
            img.setImageBitmap(image);
        }
    }
}