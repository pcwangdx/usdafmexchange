package com.mahoneydev.usdafmexchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class Frontpage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View buttonview;
    private int k;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if (action.equals(QuickstartPreferences.REGISTRATION_COMPLETE))
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
                                ImageView logo=(ImageView) findViewById(R.id.uavatar);
                                LoadImage li=new LoadImage();
                                li.img=logo;
                                li.execute(imageurl);
                            }
                            else
                            {
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                }.execute(AppCodeResources.postUrl("usdamobile", "get_avatarurl", ht));

                //switchContent(R.id.content);
            }
            else if (action.equals(QuickstartPreferences.SET_USERNAME))
            {
                String username_get=intent.getStringExtra("username");
                ((TextView)findViewById(R.id.username_menu_display)).setText(username_get);
            }
            else if (action.equals(QuickstartPreferences.SWITCH_CONTENT))
            {
                int content_id=intent.getIntExtra("content", R.array.page_001_front);
                if (content_id== R.array.page_009_noconnection)
                {
                    PageOperations.cleanPageHistory();
                    switchContent(content_id, null);
                    PageOperations.pushNewPage(new PageNode(content_id, null));
                }
                else if (PageOperations.historyEmpty()) {
                    switchContent(content_id, null);
                    PageOperations.pushNewPage(new PageNode(content_id,null));
                }
                else
                {
                    PageNode k= PageOperations.getRecentPage();
                    switchContent(k.pageId, k.params);
                }
            }
            else if (action.equals(QuickstartPreferences.SWITCH_MENU))
            {
                int menu_id=intent.getIntExtra("menu", R.id.nologin);
                switchMenu(menu_id);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Resources res = getResources();
        ((TextView)findViewById(R.id.helloworld)).setText(res.obtainTypedArray(R.array.page_102_login).getString(0));
        PageOperations.context=this;
        switchContent(R.array.page_000_welcome,null);
        PageOperations.res=getResources();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        PageOperations.height=height;
        PageOperations.width=width;
    }

    @Override
    protected void onResume(){
        super.onResume();
        //Here we go!
        //switchContent(R.id.login);
        setupReceiver();
        k=1;
        UserFileUtility.set_context(this);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (PageOperations.getRecentPage().pageId== R.array.page_001_front)
                super.onBackPressed();
            else
            {
                PageOperations.removeRecentPage();
                if (PageOperations.historyEmpty())
                {
                    PageOperations.pushNewPage(new PageNode(R.array.page_001_front,null));
                    switchContent(R.array.page_001_front,null);
                }
                else
                {
                    PageNode k= PageOperations.getRecentPage();
                    switchContent(k.pageId,k.params);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.frontpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Login) {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_102_login, null));
            switchContent(R.array.page_102_login, null);
        } else if (id == R.id.home) {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_001_front,null));
            switchContent(R.array.page_001_front,null);
        }
        else if (id == R.id.sign_out_vendor)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_001_front,null));
            UserFileUtility.clean_userinfo();
            UserFileUtility.save_userinfo();
            ((TextView)findViewById(R.id.username_menu_display)).setText("");
            switchMenu(R.id.nologin);
            switchContent(R.array.page_001_front,null);
        }
        else if (id== R.id.about_us)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_003_aboutus,null));
            switchContent(R.array.page_003_aboutus,null);
        }
        else if (id== R.id.contact_us)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_005_contactus,null));
            switchContent(R.array.page_005_contactus,null);
        }
        else if (id == R.id.post_price)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_320_postprice,null));
            switchContent(R.array.page_320_postprice,null);
        }
        else if (id == R.id.home_vendor)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_001_front,null));
            switchContent(R.array.page_001_front,null);
        }
        else if (id== R.id.vendor_profile)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_203_profilevendor,null));
            switchContent(R.array.page_203_profilevendor,null);
        }
        else if (id== R.id.social_network_vendor)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_400_socialnetwork,null));
            switchContent(R.array.page_400_socialnetwork,null);
        }
        else if (id== R.id.account_vendor)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_110_accountsettings,null));
            switchContent(R.array.page_110_accountsettings,null);
        }
        else if (id== R.id.logo)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_106_uploadlogo,null));
            switchContent(R.array.page_106_uploadlogo,null);
        }
        else if (id== R.id.scan_qr)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_199_scanqr,null));
            switchContent(R.array.page_199_scanqr,null);
        }else if (id== R.id.testpage)
        {
            PageOperations.cleanPageHistory();
            PageOperations.pushNewPage(new PageNode(R.array.page_777_test,null));
            switchContent(R.array.page_777_test,null);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void setupReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(QuickstartPreferences.REGISTRATION_COMPLETE);
        intentFilter.addAction(QuickstartPreferences.SET_USERNAME);
        intentFilter.addAction(QuickstartPreferences.SWITCH_CONTENT);
        intentFilter.addAction(QuickstartPreferences.SWITCH_MENU);
        registerReceiver(receiver, intentFilter);
    }
    public void switchContent(int content_id, Hashtable<String,String> ht)
    {
        PageOperations.generateLayout(content_id, (LinearLayout) findViewById(R.id.content), ht);
        PageOperations.generateTitle(content_id, (LinearLayout) findViewById(R.id.toolbarLayout));
    }
    public void switchMenu(int menu_id)
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().setGroupVisible(R.id.nologin, false);
        navigationView.getMenu().setGroupVisible(R.id.login_vendor, false);
        navigationView.getMenu().setGroupVisible(menu_id, true);
    }
    public void login_action(View view)
    {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == AppCodeResources.IMAGE_UPLOAD) {
            if (resultCode == RESULT_OK) {
                Log.d("Image"," Selected");
                Uri targetUri = data.getData();
//            textTargetUri.setText(targetUri.getPath());
//            String realPath;
//            if (Build.VERSION.SDK_INT < 11)
//                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
//a
//                // SDK >= 11 && SDK < 19
//            else if (Build.VERSION.SDK_INT < 19)
//                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
//
//                // SDK > 19 (Android 4.4)
//            else
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
//            textTargetUri.setText(realPath);
                Hashtable<String,String> ht=new Hashtable<>();
                String token=UserFileUtility.get_token();
                ht.put("token",token);
                ht.put("os","Android");
                UploadTask v=new UploadTask(){
                    @Override
                    protected void onPostExecute(JSONObject result)
                    {
                        try {
                            Log.d("Error", result.getString("error"));
                            String error=result.getString("error");
                            if (error.equals("-9"))
                            {

                            }
                            else
                            {

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                };
                v.fileuri=targetUri;
                v.context=this;
                v.execute(AppCodeResources.postUrl("usdamobile", "upload_logo", ht));

            }
        }
        else if (requestCode == AppCodeResources.SCAN_QR){
            if (resultCode == RESULT_OK)
            {
                //get the extras that are returned from the intent
                String contents = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                String token_s=UserFileUtility.get_token();
                Hashtable<String,String> ht=new Hashtable<String, String>();
                String friendname=contents.replace(AppCodeResources.FRIEND_URL_PRE,"");
                ht.put("friendname", friendname);
                PageOperations.pushNewPage(new PageNode(R.array.page_407_profile,ht));
                switchContent(R.array.page_407_profile, ht);
//                new FetchTask(){
//                    @Override
//                    protected void onPostExecute(JSONObject result)
//                    {
//                        try {
//                            Log.d("Error", result.getString("error"));
//                            String error=result.getString("error");
//                            if (error.equals("-9"))
//                            {
//
//                            }
//                            else
//                            {
//                            }
//                        }
//                        catch (JSONException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }.execute(AppCodeResources.postUrl("usdatestchongguang", "public_search_posts", ht));

            }
        }
    }
    public static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

}
