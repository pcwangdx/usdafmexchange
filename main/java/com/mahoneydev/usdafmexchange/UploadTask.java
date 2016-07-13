package com.mahoneydev.usdafmexchange;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
public class UploadTask extends AsyncTask<String, Void, JSONObject> {
    public Uri fileuri=null;
    public Frontpage context=null;
    @Override
    protected JSONObject doInBackground(String... para) {
        // params comes from the execute() call: params[0] is the url.
        if ((fileuri==null)||(context==null))
        {
            JSONObject j=new JSONObject();
            try {
                j.put("error", "-10");
            }
            catch (JSONException ej){
                ej.printStackTrace();
            }
        }
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
        String exsistingFileName="file.bmp";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="3rd";

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

// Allow Inputs
            conn.setDoInput(true);

// Allow Outputs
            conn.setDoOutput(true);

// Don't use a cached copy.
            conn.setUseCaches(false);

// Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            DataOutputStream dos = new DataOutputStream( conn.getOutputStream() );

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + exsistingFileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);




            Log.e(Tag,"Headers are written");

// create a buffer of maximum size
            InputStream fileInputStream=context.getContentResolver().openInputStream(fileuri);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

// read file and write it into form...

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

// send multipart form data necesssary after file data...

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

// close streams
            Log.e(Tag,"File is written");
            fileInputStream.close();
            dos.flush();
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