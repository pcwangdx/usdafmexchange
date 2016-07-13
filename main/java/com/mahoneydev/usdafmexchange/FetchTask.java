package com.mahoneydev.usdafmexchange;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mahoneydev on 5/11/2016.
 */
public class FetchTask extends AsyncTask<String, Void, JSONObject> {
    private String action;
    @Override
    protected JSONObject doInBackground(String... para) {
        // params comes from the execute() call: params[0] is the url.
        try {
            Log.d("URL",para[0]);
            return downloadUrl(para[0]);

        } catch (Exception e) {

            JSONObject j=new JSONObject();
            try {
                j.put("error", "-10");
            }
            catch (JSONException ej){
                ej.printStackTrace();
            }
            return j;
        }
    }

    private JSONObject downloadUrl(String myurl) throws Exception {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(100000 /* milliseconds */);
            conn.setConnectTimeout(150000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DEBUG", "The response is: " + response);
            if (response==404)
            {
                is=conn.getErrorStream();
            }
            else
                is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            // Convert the InputStream into a string
            String contentAsString = result.toString();
            Log.d("string",contentAsString);
            JSONObject j = new JSONObject(contentAsString);
            return j;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        }
        finally {
            if (is != null) {
                is.close();
            }

        }
    }
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}