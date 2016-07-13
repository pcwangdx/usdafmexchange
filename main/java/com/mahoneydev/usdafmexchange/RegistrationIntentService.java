package com.mahoneydev.usdafmexchange;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by mahoneydev on 5/12/2016.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private String token;
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            //subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Intent nologin = new Intent();
            nologin.setAction(QuickstartPreferences.SWITCH_CONTENT);
            nologin.putExtra("content", R.array.page_009_noconnection);
            sendBroadcast(nologin);
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.

    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param t The new token.
     */
    private void sendRegistrationToServer(String t) {
        // Add custom implementation, as needed.
        token=t;
        String username_info=UserFileUtility.get_username();
        String password_info=UserFileUtility.get_password();
        UserFileUtility.set_token(t);
        if (username_info.equals("")||password_info.equals(""))
        {
            Intent nologin = new Intent();
            nologin.setAction(QuickstartPreferences.SWITCH_CONTENT);
            nologin.putExtra("content", R.array.page_102_login);
            sendBroadcast(nologin);
        }
        Hashtable<String,String> ht=new Hashtable<String, String>();
        ht.put("username",username_info);
        ht.put("password", password_info);
        ht.put("os","Android");
        ht.put("token",t);
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
                        Intent usernameset = new Intent();
                        usernameset.setAction(QuickstartPreferences.SET_USERNAME);
                        usernameset.putExtra("username", UserFileUtility.get_username());
                        sendBroadcast(usernameset);
                        Intent menuchange = new Intent();
                        menuchange.setAction(QuickstartPreferences.SWITCH_MENU);
                        menuchange.putExtra("menu", R.id.login_vendor);
                        sendBroadcast(menuchange);
                        Intent registrationComplete = new Intent();
                        registrationComplete.setAction(QuickstartPreferences.SWITCH_CONTENT);
                        registrationComplete.putExtra("content", R.array.page_001_front);
                        sendBroadcast(registrationComplete);
                    }
                    else if (error.equals("-10"))
                    {
                        Intent nologin = new Intent();
                        nologin.setAction(QuickstartPreferences.SWITCH_CONTENT);
                        nologin.putExtra("content", R.array.page_009_noconnection);
                        sendBroadcast(nologin);
                    }
                    else {
                        Intent nologin = new Intent();
                        nologin.setAction(QuickstartPreferences.SWITCH_CONTENT);
                        nologin.putExtra("content", R.array.page_102_login);
                        sendBroadcast(nologin);
                    }
                }
                catch (JSONException e)
                {
                    Intent nologin = new Intent();
                    nologin.setAction(QuickstartPreferences.SWITCH_CONTENT);
                    nologin.putExtra("content", R.array.page_102_login);
                    sendBroadcast(nologin);
                }
            }

        }.execute(AppCodeResources.postUrl("usdamobile", "mobile_login", ht));
    }
//    private void sendtoken(){
//        Hashtable<String,String> ht=new Hashtable<String, String>();
//        ht.put("token",token);
//        new FetchTask(){
//            @Override
//            protected void onPostExecute(JSONObject result)
//            {
//                try {
//                    Log.d("USER", result.getString("user"));
//                }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//
//        }.execute(AppCodeResources.postUrl("usdamobile", "save_token", ht));
//    }
    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
