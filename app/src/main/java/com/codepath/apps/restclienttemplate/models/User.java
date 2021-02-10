package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String profileImageURL;

    @ColumnInfo
    @PrimaryKey
    public long id;

    //empty constuctor needed by parceler library
    public User() {
    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageURL = jsonObject.getString("profile_image_url_https");
        return user;
    }

    public static List<User> fromTweetsArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();

        for(int i = 0; i < tweetsFromNetwork.size();i++){
            users.add(tweetsFromNetwork.get(i).user);
        }

        return users;
    }
}
