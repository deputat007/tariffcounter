package com.thehatefulsix.tariffcounter.utils;

import android.support.annotation.NonNull;

import com.thehatefulsix.tariffcounter.models.Community;
import com.thehatefulsix.tariffcounter.models.WallPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    public static List<WallPost> parseJSONToPosts(@NonNull JSONObject json){
        final List<WallPost> wallPosts = new ArrayList<>();

        try {
            final JSONArray jsonArray = json.getJSONObject("response").getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++){
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final WallPost wallPost = new WallPost();

                wallPost.setId(jsonObject.getInt("id"));
                wallPost.setOwnerId(jsonObject.getInt("owner_id"));
                wallPost.setDate(jsonObject.getLong("date") * 1000);
                wallPost.setText(jsonObject.getString("text"));

                if (jsonObject.has("attachments") &&
                        jsonObject.getJSONArray("attachments").length() != 0){
                    final JSONArray attachments = jsonObject.getJSONArray("attachments");

                    for (int j = 0; j < attachments.length(); j++) {
                        final JSONObject attachment = attachments.getJSONObject(j);

                        if (attachment.get("type").equals("link")){
                            wallPost.setLink(attachment.getJSONObject("link").getString("url"));
                        }

                        if (attachment.get("type").equals("photo")){
                            wallPost.setPhotoUrl(attachment.getJSONObject("photo").getString("photo_604"));
                        }
                    }
                }
                wallPosts.add(wallPost);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return wallPosts;
    }

    public static Community parseJSONToCommunity(@NonNull JSONObject json){
        final Community community = new Community();

        try {
            final JSONObject jsonObject = json.getJSONArray("response").getJSONObject(0);

            community.setId(jsonObject.getInt("id"));
            community.setName(jsonObject.getString("name"));
            community.setIconPath(jsonObject.getString("photo_50"));

        } catch (JSONException e) {
            return null;
        }

        return community;
    }
}
