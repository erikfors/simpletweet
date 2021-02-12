package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGHT = 140;

    EditText etCompose;
    Button btnTweet;
    TextView tvCounter;
    Boolean isSaved = false;
    String tweetBody;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

      tweetBody = "";

        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
       isSaved = pref.getBoolean("isSaved",false );

       if(isSaved)
           tweetBody = pref.getString("text", "");


        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCounter = findViewById(R.id.tvCounter);

        etCompose.setText(tweetBody);
        etCompose.requestFocus();

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetBody = etCompose.getText().toString();
                if(tweetBody.isEmpty()){
                    Toast.makeText(ComposeActivity.this,"Sorry, your text can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetBody.length() > MAX_TWEET_LENGHT){
                    Toast.makeText(ComposeActivity.this,"Sorry, your text can not be longer than 140 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                client.publishTweet(tweetBody, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG,"onSuccess publishing");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"Publish tweet is ");
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet) );
                            setResult(RESULT_OK, intent);
                            //set save preference save to false
                            SharedPreferences pref =
                                    PreferenceManager.getDefaultSharedPreferences(ComposeActivity.this);
                            SharedPreferences.Editor edit = pref.edit();
                            edit.putBoolean("isSaved", false);
                            edit.apply();
                            finish();
                        }catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG,"JSON exception");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure", throwable);
                    }
                });
            }
        });

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int counter = etCompose.length();
                final int TEXT_COUNT = 140;

                if(counter <= TEXT_COUNT) {
                    tvCounter.setText(Integer.toString(counter));
                    tvCounter.setTextColor(getResources().getColor(R.color.is_black));
                    btnTweet.setEnabled(true);
                    btnTweet.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
                }
                else{
                    tvCounter.setText("-" + (counter - TEXT_COUNT));
                    tvCounter.setTextColor(getResources().getColor(R.color.medium_red));
                    btnTweet.setEnabled(false);
                    btnTweet.setBackgroundColor(getResources().getColor(R.color.twitter_blue_30));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(ComposeActivity.this);
        // Set the message show for the Alert time
        builder.setMessage("Do you want to save your tweet as a draft ?");
        // Set Alert Title
        builder.setTitle("Alert !");
        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.
        builder
                .setPositiveButton(
                        "Yes",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {

                                //save the user tweet into share preference
                                SharedPreferences pref =
                                        PreferenceManager.getDefaultSharedPreferences(ComposeActivity.this);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putString("text", etCompose.getText().toString());
                                //set change preference is saved to true
                                edit.putBoolean("isSaved", true);
                                edit.apply();
                                //leave activity
                                finish();
                            }
                        });
        // Set the Negative button with No name
        // OnClickListener method is use
        // of DialogInterface interface.
        builder
                .setNegativeButton(
                        "No",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                               //set save preference save to false
                                SharedPreferences pref =
                                        PreferenceManager.getDefaultSharedPreferences(ComposeActivity.this);
                                SharedPreferences.Editor edit = pref.edit();
                                edit.putBoolean("isSaved", false);
                                edit.apply();
                                //leave activity
                                finish();
                            }
                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();

    }
}