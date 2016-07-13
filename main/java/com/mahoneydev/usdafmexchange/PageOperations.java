package com.mahoneydev.usdafmexchange;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by mahoneydev on 5/23/2016.
 */
public class PageOperations {
    public static Frontpage context = null;
    public static Resources res = null;
    private static View playout=null;
    private static Hashtable<String, View> hashelements;
    private static List<PageNode> pageHistory=new ArrayList<PageNode>();
    public static int height=0;
    public static int width=0;
    public static void setPage(int page_id, Hashtable<String,String> ht){
        if (context!=null)
            context.switchContent(page_id, ht);
    }
    public static void setMenu(int menu_id){
        if (context!=null)
            context.switchMenu(menu_id);
    }
    public static void generateTitle(int code, LinearLayout toolbar){
        toolbar.removeAllViewsInLayout();
        switch (code){
            case (R.array.page_001_front):
            {
                ImageView iv=new ImageView(context);
                iv.setImageResource(R.drawable.fme_header);
                toolbar.addView(iv);
                break;
            }
            case (R.array.page_102_login):
            {
                TextView tv = new TextView(context);
                tv.setText("Login");
                toolbar.addView(tv);
                break;
            }
        }
    }
    public static void generateLayout(int code, LinearLayout layout, Hashtable<String,String> params) {
        if ((res == null) || (context == null))
            return;
        TypedArray pageArray = res.obtainTypedArray(code);
        layout.removeAllViewsInLayout();
        playout=layout;
        try {
            hashelements = new Hashtable<>();
            for (int i = 0; i < pageArray.length(); i++) {
                String elements = pageArray.getString(i);
                JSONObject jsonelements = new JSONObject(elements);
                if (jsonelements.has("element")) {
                    String element = jsonelements.getString("element");
                    if (element.equals("TextView")) {
                        TextView tv = new TextView(context);
                        tv.setText(jsonelements.getString("value"));
                        if (jsonelements.has("inputtype")) {
                        }
                        hashelements.put(jsonelements.getString("id"), tv);
                        tv.setVisibility(View.INVISIBLE);
                        layout.addView(tv);

                    } else if (element.equals("EditText")) {
                        EditText et = new EditText(context);
                        et.setHint(jsonelements.getString("value"));
                        if (jsonelements.has("inputtype")) {
                            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        hashelements.put(jsonelements.getString("id"), et);
                        et.setVisibility(View.INVISIBLE);
                        et.setBackgroundResource(R.drawable.rounded_text);
                        layout.addView(et);
                    } else if (element.equals("Button"))
                    {
                        Button bt = new Button(context);
                        bt.setText(jsonelements.getString("value"));

                        hashelements.put(jsonelements.getString("id"), bt);
                        setButtonAction(jsonelements.getString("clickaction"), bt);
                        bt.setBackgroundResource(R.drawable.button_600x50);
                        bt.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                        bt.setPadding(15,0,0,0);
                        bt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        bt.setTextSize(width/50);
                        bt.setTextAppearance(context,R.style.QText);
                        bt.setTransformationMethod(null);
                        bt.setVisibility(View.INVISIBLE);
                        layout.addView(bt);
                    }
                    else if (element.equals("ImageView"))
                    {
                        ImageView iv=new ImageView(context);
                        iv.setVisibility(View.INVISIBLE);
                        hashelements.put(jsonelements.getString("id"),iv);
                        layout.addView(iv);
                    }
                    else if (element.equals("SearchView"))
                    {
                        LinearLayout ll=new LinearLayout(context);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        EditText et = new EditText(context);
                        et.setHint(jsonelements.getString("value"));
                        hashelements.put(jsonelements.getString("id") + "Input", et);
                        et.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,0.8f));

                        ll.addView(et);
                        ImageButton bt = new ImageButton(context);
                        bt.setImageResource(R.drawable.ic_menu_manage);
                        hashelements.put(jsonelements.getString("id") + "Button", bt);
                        setButtonAction(jsonelements.getString("clickaction"), bt);
                        bt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,0.2f));
                        ll.addView(bt);
                        ll.setVisibility(View.INVISIBLE);
                        layout.addView(ll);
                    }else if (element.equals("ScrollView"))
                    {
                        ScrollView sv=new ScrollView(context);
                        hashelements.put(jsonelements.getString("id"),sv);
                        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        sv.setVisibility(View.INVISIBLE);
                        TableLayout tl=new TableLayout(context);
                        hashelements.put(jsonelements.getString("id")+"Table",tl);
                        tl.setLayoutParams(new ScrollView.LayoutParams( ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
                        tl.setVisibility(View.INVISIBLE);
                        sv.addView(tl);
                        layout.addView(sv);
                    }else if (element.equals("Spinner"))
                    {
                        Spinner sp=new Spinner(context);
                        sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        sp.setVisibility(View.INVISIBLE);
                        hashelements.put(jsonelements.getString("id"),sp);
                        layout.addView(sp);
                    }else if (element.equals("CheckBox"))
                    {
                        CheckBox cb=new CheckBox(context);
                        cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        cb.setText(jsonelements.getString("value"));
                        cb.setVisibility(View.INVISIBLE);
                        hashelements.put(jsonelements.getString("id"), cb);
                        layout.addView(cb);
                    }
                }
            }
            renderUI(code,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setButtonAction(String action, ImageButton bt) {
        if (action.equals("searchPublicPost"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showpublicposts((((EditText) hashelements.get("searchpublicpostsInput")).getText()).toString());
                }
            });
        }else if (action.equals("testSearch"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String k=(((EditText) hashelements.get("testSearchInput")).getText()).toString();
                    ((TextView)hashelements.get("testView")).setText(k);
                }
            });
        }
    }
    private static void setButtonAction(String action, Button bt){
        if (action.equals("loginSubmit")) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username_s=(((EditText)hashelements.get("usernameInput")).getText()).toString();
                    Log.d("username", username_s);
                    final String password_s=(((EditText)hashelements.get("passwordInput")).getText()).toString();
                    Log.d("password",password_s);
                    String token_s=UserFileUtility.get_token();
                    if (token_s.equals(""))
                    {
                        ((TextView)hashelements.get("loginErrorView")).setText("Network Error!");
                        return;
                    }
                    UserFileUtility.set_userlogininfo(username_s,password_s);
                    Hashtable<String,String> ht=new Hashtable<String, String>();
                    ht.put("username",username_s);
                    ht.put("password", password_s);
                    ht.put("os", "Android");
                    ht.put("token",token_s);
                    new FetchTask(){
                        @Override
                        protected void onPostExecute(JSONObject result)
                        {
                            try {
                                Log.d("Error", result.getString("error"));
                                String error=result.getString("error");
                                if (error.equals("-9"))
                                {
                                    UserFileUtility.save_userinfo();
                                    setPage(R.array.page_001_front,null);
                                    ((TextView)context.findViewById(R.id.username_menu_display)).setText(UserFileUtility.get_username());
                                    setMenu(R.id.login_vendor);

                                }
                                else
                                {
                                    ((TextView)hashelements.get("loginErrorView")).setText("Password Error!");
                                    UserFileUtility.clean_userinfo();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }.execute(AppCodeResources.postUrl("usdamobile", "mobile_login", ht));
                }
            });
        }
        else if (action.equals("selectImage")) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    context.startActivityForResult(intent, AppCodeResources.IMAGE_UPLOAD);
                }
            });
        }

        //post price
        else if(action.equals("newpost"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_322_newpost, null));
                    setPage(R.array.page_322_newpost, null);
                }
            });
        }
        else if(action.equals("posttemplate"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_324_posttemplate, null));
                    setPage(R.array.page_324_posttemplate, null);
                }
            });
        }
        else if(action.equals("postschedule"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_328_postschedule, null));
                    setPage(R.array.page_328_postschedule, null);
                }
            });
        }
        else if(action.equals("postpublish"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_330_postpublish, null));
                    setPage(R.array.page_330_postpublish, null);
                }
            });
        }

        //vendor profile
        else if(action.equals("nameaddress"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_205_nameaddress, null));
                    setPage(R.array.page_205_nameaddress, null);
                }
            });
        }
        else if(action.equals("phoneemail"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_206_phoneemail, null));
                    setPage(R.array.page_206_phoneemail, null);
                }
            });
        }
        else if(action.equals("website"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_207_website, null));
                    setPage(R.array.page_207_website, null);
                }
            });
        }
        else if(action.equals("certified"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_209_certified, null));
                    setPage(R.array.page_209_certified, null);
                }
            });
        }
        else if(action.equals("farmermarket"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_309_farmermarket, null));
                    setPage(R.array.page_309_farmermarket, null);
                }
            });
        }
        else if(action.equals("productsell"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_305_productsell, null));
                    setPage(R.array.page_305_productsell, null);
                }
            });
        }

        //social network
        else if(action.equals("friendship"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_401_friendship, null));
                    setPage(R.array.page_401_friendship, null);
                }
            });
        }
        else if(action.equals("request"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_412_request, null));
                    setPage(R.array.page_412_request, null);
                }
            });
        }
        else if(action.equals("message"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_402_message, null));
                    setPage(R.array.page_402_message, null);
                }
            });
        }
        else if(action.equals("notification"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_404_notification, null));
                    setPage(R.array.page_404_notification, null);
                }
            });
        }

        //account and setting
        else if(action.equals("accountinfo"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_105_accountinfo, null));
                    setPage(R.array.page_105_accountinfo, null);
                }
            });
        }
        else if(action.equals("uploadlogo"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_106_uploadlogo, null));
                    setPage(R.array.page_106_uploadlogo, null);
                }
            });
        }
        else if(action.equals("qrcode"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_109_qrcode, null));
                    setPage(R.array.page_109_qrcode, null);
                }
            });
        }
        else if(action.equals("searchpreference"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_111_searchpreference, null));
                    setPage(R.array.page_111_searchpreference, null);
                }
            });
        }
        else if(action.equals("setnotification"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_107_setnotification, null));
                    setPage(R.array.page_107_setnotification, null);
                }
            });
        }
        else if(action.equals("socialnetwork"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_108_socialnetwork, null));
                    setPage(R.array.page_108_socialnetwork, null);
                }
            });
        }
        else if(action.equals("deleteaccount"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pushNewPage(new PageNode(R.array.page_112_deleteaccount, null));
                    setPage(R.array.page_112_deleteaccount, null);
                }
            });
        }
        else if (action.equals("scanQR"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentIntegrator qrscanner=new IntentIntegrator(context);
                    qrscanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    qrscanner.initiateScan();
                    context.startActivityForResult(qrscanner.createScanIntent(), AppCodeResources.SCAN_QR);
//                    try {
//                        //start the scanning activity from the com.google.zxing.client.android.SCAN intent
//                        Intent intent = new Intent(AppCodeResources.ACTION_SCAN);
//                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                        context.startActivityForResult(intent, AppCodeResources.SCAN_QR);
//                    } catch (ActivityNotFoundException e) {
//                        //on catch, show the download dialog
//                        context.showDialog(context, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
//                    }
                }
            });
        }
        else if (action.equals("TestBegin"))
        {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean check=((CheckBox)hashelements.get("testCheckbox")).isChecked();
                    ((CheckBox)hashelements.get("testCheckbox")).setChecked(!check);
                    String input1=((EditText)hashelements.get("testInput")).getText().toString();
                    ((EditText)hashelements.get("testInput")).setText(input1 + " " + input1);
                    String spinner1=((Spinner)hashelements.get("testSpinner")).getSelectedItem().toString();
                    long spinnerp=((Spinner)hashelements.get("testSpinner")).getSelectedItemId();
                    ((TextView)hashelements.get("testView")).setText("Selected: "+spinner1+", Postion: "+spinnerp);
                }
            });
        }
    }

    private static void renderUI(int code, Hashtable<String,String> params){
        if (code== R.array.page_001_front)
        {
            showpublicposts("");
        }else if (code== R.array.page_020_viewpost)
        {
            ((TextView)hashelements.get("postView")).setText("This is post "+params.get("postid"));
            setupUI(playout);
        }
        else if (code== R.array.page_106_uploadlogo)
        {
            Hashtable<String,String> ht=new Hashtable<String, String>();
            String token_s=UserFileUtility.get_token();
            ht.put("os", "Android");
            ht.put("token",token_s);
            new FetchTask(){
                @Override
                protected void onPostExecute(JSONObject result)
                {
                    try {
                        Log.d("Error", result.getString("error"));
                        String error=result.getString("error");
                        if (error.equals("-9"))
                        {
                            String imageurl=result.getString("avatar_url");
                            imageurl=imageurl.replace("\\","");
                            LoadImage li=new LoadImage();
                            li.img=(ImageView) hashelements.get("logoView");
                            li.execute(imageurl);
                        }
                        else
                        {
                        }
                        setupUI(playout);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

            }.execute(AppCodeResources.postUrl("usdamobile", "get_avatarurl", ht));
        }else if (code== R.array.page_407_profile) {
            ((TextView)hashelements.get("nameView")).setText("Name: "+params.get("friendname"));
            setupUI(playout);
        }else if (code== R.array.page_777_test) {
            Hashtable<String,String> ht=new Hashtable<String, String>();
            String token_s=UserFileUtility.get_token();
            ht.put("os", "Android");
            ht.put("token", token_s);
            new FetchTask(){
                @Override
                protected void onPostExecute(JSONObject result)
                {
                    try {
                        Log.d("Error", result.getString("error"));
                        String error=result.getString("error");



                        if (error.equals("-9"))
                        {
                            JSONArray ja=result.getJSONArray("list");
                            String[] arraySpinner = new String[ja.length()];
                            for (int i=0;i<ja.length();i++)
                            {
                                arraySpinner[i]=ja.getString(i);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_item, arraySpinner);
                            ((Spinner)hashelements.get("testSpinner")).setAdapter(adapter);
                        }
                        else
                        {
                        }
                        setupUI(playout);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

            }.execute(AppCodeResources.postUrl("usdamobile", "testpage", ht));
        }else if (code== R.array.page_401_friendship) {
            showfriends();
        }
        else
            setupUI(playout);
    }
    private static void showfriends(){
        String token_s=UserFileUtility.get_token();
        Hashtable<String,String> ht=new Hashtable<String, String>();
        ht.put("os", "Android");
        ht.put("token",token_s);
        new FetchTask(){
            @Override
            protected void onPostExecute(JSONObject result) {
                try
                {
                    Log.d("Error", result.getString("error"));
                    String error=result.getString("error");
                    if (error.equals("-9")) {
                        TableLayout tl = (TableLayout) hashelements.get("friendshipScrollTable");
                        tl.removeAllViews();
                        JSONArray allfriends=result.getJSONArray("results");
                        for (int i=0;i<allfriends.length();i++)
                        {
                            JSONObject friend=allfriends.getJSONObject(i);
                            TableRow lv=new TableRow(context);
                            lv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, height/5));
                            //Logo
                            ImageView logo=new ImageView(context);
                            String vendorlogohtml=friend.getString("avatar");
                            int urlstart=vendorlogohtml.indexOf("src=\"")+"src=\"".length();
                            int urlend=urlstart;
                            for (int j=urlstart;vendorlogohtml.charAt(j)!='\"';j++)
                            {
                                urlend=j;
                            }
                            String vendorlogourl=vendorlogohtml.substring(urlstart,urlend+1);
                            Log.d("LOGOURL",vendorlogourl);
                            LoadImage li=new LoadImage();
                            li.img=logo;
                            li.execute(vendorlogourl);
                            logo.setLayoutParams(new TableRow.LayoutParams(0, height/5, 0.3f));
                            lv.addView(logo);

                            LinearLayout ll=new LinearLayout(context);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            //Name
                            TextView name=new TextView(context);
                            name.setText(friend.getString("displayname"));
                            name.setTextAppearance(context,R.style.Large);
                            name.setTextSize(width/50);
                            ll.addView(name);
                            //Business Name
                            final TextView bn=new TextView(context);
                            bn.setText(friend.getString("businessname"));
                            ll.addView(bn);

                            ll.setLayoutParams(new TableRow.LayoutParams(0, height/5, 0.7f));
                            lv.addView(ll);
                            tl.addView(lv);

                            TableRow lk=new TableRow(context);
                            lk.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            View ldivider=new LinearLayout(context);
                            ldivider.setBackgroundColor(Color.BLACK);
                            ldivider.setLayoutParams(new TableRow.LayoutParams(0,2,0.3f));
                            View rdivider=new LinearLayout(context);
                            rdivider.setBackgroundColor(Color.BLACK);
                            rdivider.setLayoutParams(new TableRow.LayoutParams(0,2,0.7f));
                            lk.addView(ldivider);
                            lk.addView(rdivider);
                            tl.addView(lk);
                        }
                    }
                    else
                    {
                    }
                    setupUI(playout);
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }.execute(AppCodeResources.postUrl("usdafriendship", "friends_list_all_byuser", ht));
    }

    private static void showpublicposts(String search){
        //SEARCH AND SHOW FUNTION FOR FRONT PAGE
        String token_s=UserFileUtility.get_token();
        Hashtable<String,String> ht=new Hashtable<String, String>();
        ht.put("liststart","0");
        ht.put("perpage","5");
        ht.put("os", "Android");
        ht.put("search",search);
        ht.put("token",token_s);
        new FetchTask(){
            @Override
            protected void onPostExecute(JSONObject result)
            {
                try {
                    Log.d("Error", result.getString("error"));
                    String error=result.getString("error");
                    if (error.equals("-9"))
                    {
                        TableLayout tl=(TableLayout)hashelements.get("postsScrollTable");
                        tl.removeAllViews();
                        JSONArray resultposts=result.getJSONArray("results");
                        Log.d("resultposts", resultposts.toString());
                        for (int i=0;i<resultposts.length();i++)
                        {
                            JSONObject jpost=resultposts.getJSONObject(i);
                            Log.d("jpost", jpost.toString());
                            TableRow lv=new TableRow(context);
                            lv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                            //Logo
                            ImageView logo=new ImageView(context);
                            String vendorlogohtml=jpost.getString("vendorlogo");
                            int urlstart=vendorlogohtml.indexOf("src=\"")+"src=\"".length();
                            int urlend=urlstart;
                            for (int j=urlstart;vendorlogohtml.charAt(j)!='\"';j++)
                            {
                                urlend=j;
                            }
                            String vendorlogourl=vendorlogohtml.substring(urlstart,urlend+1);
                            Log.d("LOGOURL",vendorlogourl);
                            LoadImage li=new LoadImage();
                            li.img=logo;
                            li.execute(vendorlogourl);
                            logo.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
                            lv.addView(logo);

                            LinearLayout ll=new LinearLayout(context);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            //Product Name and Day
                            TextView tv=new TextView(context);
                            tv.setText(jpost.getString("productname") + "\n" + "Day:" + jpost.getString("marketday"));
                            ll.addView(tv);
                            //Price Unit
                            TextView tv4=new TextView(context);
                            tv4.setText(jpost.getString("price") + " / " + jpost.getString("unit"));
                            ll.addView(tv4);
                            //Vendor
                            TextView tv3=new TextView(context);
                            tv3.setText(jpost.getString("vendorbusinessname"));
                            ll.addView(tv3);
                            //Market
                            TextView tv2=new TextView(context);
                            tv2.setText(jpost.getString("farmersmarketname"));
                            ll.addView(tv2);
                            ll.setLayoutParams(new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 0.7f));
                            lv.addView(ll);




                            final String postid=jpost.getString("postid");
                            lv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Hashtable<String, String> pam = new Hashtable<String, String>();
                                    pam.put("postid", postid);
                                    pushNewPage(new PageNode(R.array.page_020_viewpost, pam));
                                    setPage(R.array.page_020_viewpost, pam);
                                }
                            });

                            tl.addView(lv);
                            TableRow lk=new TableRow(context);
                            lk.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            View ldivider=new LinearLayout(context);
                            ldivider.setBackgroundColor(Color.BLACK);
                            ldivider.setLayoutParams(new TableRow.LayoutParams(0, 2,0.3f));
                            View rdivider=new LinearLayout(context);
                            rdivider.setBackgroundColor(Color.BLACK);
                            rdivider.setLayoutParams(new TableRow.LayoutParams(0,2,0.7f));
                            lk.addView(ldivider);
                            lk.addView(rdivider);
                            tl.addView(lk);
                        }

                    }
                    else
                    {
                    }
                    setupUI(playout);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

        }.execute(AppCodeResources.postUrl("usdatestchongguang", "public_search_posts", ht));
    }
    private static void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(context);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);
                innerView.setVisibility(View.VISIBLE);

                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



    public static PageNode getRecentPage(){
        if (pageHistory==null)
            pageHistory=new ArrayList<>();
        if (pageHistory.isEmpty())
            return null;
        return pageHistory.get(0);
    }
    public static void removeRecentPage(){
        if (pageHistory==null)
            pageHistory=new ArrayList<>();
        if (!pageHistory.isEmpty())
            pageHistory.remove(0);
    }
    public static void cleanPageHistory(){
        if (pageHistory==null)
            pageHistory=new ArrayList<>();
        pageHistory.clear();
    }
    public static void pushNewPage(PageNode pn){
        if (pageHistory==null)
            pageHistory=new ArrayList<>();
        pageHistory.add(0,pn);
    }
    public static boolean historyEmpty(){
        if (pageHistory==null)
            pageHistory=new ArrayList<>();
        if (pageHistory.isEmpty())
            return true;
        else
            return false;
    }

}
