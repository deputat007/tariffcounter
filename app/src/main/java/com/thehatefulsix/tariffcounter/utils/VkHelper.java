package com.thehatefulsix.tariffcounter.utils;


import android.content.Context;
import android.support.annotation.NonNull;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.models.Community;
import com.thehatefulsix.tariffcounter.models.WallPost;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiGroups;
import com.vk.sdk.api.methods.VKApiWall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VkHelper {
    private static final Object COUNT = 100;

    public static void refreshNews(@NonNull final Context context,
                                   @NonNull final NewsUpdateCallback callback) {
        if (!InternetConnectivity.isOnline(context)){
            callback.onError(context.getString(R.string.no_internet_connection));
            return;
        }

        final RealmHelper realmHelper = RealmHelper.getInstance();

        final int groupId = context.getResources().getInteger(R.integer.group_id);

        final Map<String, Object> parametersGroup = new HashMap<>();
        parametersGroup.put(VKApiConst.GROUP_ID, groupId);

        final Map<String, Object> parametersPosts = new HashMap<>();
        parametersPosts.put(VKApiConst.OWNER_ID, -groupId);
        parametersPosts.put(VKApiConst.COUNT, COUNT);

        final VKRequest vkRequestGroup = new VKApiGroups().getById(
                new VKParameters(parametersGroup));
        vkRequestGroup.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                final Community community = JSONParser.parseJSONToCommunity(response.json);
                if (community != null){
                    final Community communityFromRealm =
                            realmHelper.getObjectById(Community.class, groupId);
                    if (communityFromRealm == null){
                        realmHelper.addOrUpdate(community);
                    }else {
                        realmHelper.beginTransaction();
                        communityFromRealm.setIconPath(community.getIconPath());
                        communityFromRealm.setName(community.getName());
                        realmHelper.commitTransaction();
                    }
                }else {
                    callback.onError("parseJSONToCommunity() returned null");
                }
            }

            @Override
            public void onError(VKError error) {
                callback.onError(error.toString());
            }
        });

        final VKRequest vkRequestPosts = new VKApiWall().get(new VKParameters(parametersPosts));
        vkRequestPosts.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                final List<WallPost> wallPosts = JSONParser.parseJSONToPosts(response.json);

                if (wallPosts != null){
                    final Community community = realmHelper.getObjectById(
                            Community.class, groupId);
                    if (community != null){
                        realmHelper.beginTransaction();
                        realmHelper.getAll(WallPost.class).deleteAllFromRealm();
                        realmHelper.commitTransaction();

                        final List<WallPost> posts = realmHelper.addAll(wallPosts);

                        realmHelper.beginTransaction();

                        for (WallPost wallPost :
                                posts) {
                            community.getWallPosts().add(wallPost);
                        }
                        realmHelper.commitTransaction();

                        callback.onComplete(community);
                    }
                }else {
                    callback.onError("parseJSONToPosts() returned null");
                }

            }

            @Override
            public void onError(VKError error) {
                callback.onError(error.toString());
            }
        });
    }

    public static abstract class NewsUpdateCallback{
        protected abstract void onComplete(Community community);

        protected void onError(String error){}
    }
}
