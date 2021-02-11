package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCounter = findViewById(R.id.tvCounter);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tweetBody = etCompose.getText().toString();
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
}