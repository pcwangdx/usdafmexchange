package com.mahoneydev.usdafmexchange;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Hashtable;

/**
 * Created by mahoneydev on 5/17/2016.
 */
public class UserFileUtility {
    private static String username="";
    private static String password="";
    private static String token="";
    private static Context context=null;
    public static void set_context(Context app_context){
        context=app_context;
    }
    public static String get_username(){
        if (username.equals(""))
        {
            get_userinfo();
        }
        return username;
    }
    public static String get_password(){
        if (password.equals(""))
        {
            get_userinfo();
        }
        return password;
    }
    public static String get_token(){
        if (token.equals(""))
        {
            get_userinfo();
        }
        return token;
    }
    public static void clean_userinfo(){
        username="";
        password="";
    }
    public static void set_userlogininfo(String username_set, String password_set){
        username=username_set;
        password=password_set;
    }
    public static void set_token(String token_set)
    {
        token=token_set;
    }
    private static void get_userinfo(){
        if (context==null)
            return;
        try {
            File file = new File(context.getFilesDir(), AppCodeResources.userfile);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            // Convert the InputStream into a string
            String contentAsString = result.toString();
            Log.d("string", contentAsString);
            JSONObject j = new JSONObject(contentAsString);
            username=j.getString("username");
            password=j.getString("password");
            token=j.getString("token");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void save_userinfo(){
        if (context==null)
            return;
        try {
            File file = new File(context.getFilesDir(), AppCodeResources.userfile);
            FileOutputStream outputStream=new FileOutputStream(file);
            Hashtable<String,String> ht=new Hashtable<String, String>();
            ht.put("username",username);
            ht.put("password", password);
            ht.put("token", token);
            JSONObject j = new JSONObject(ht);
            outputStream.write(j.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
