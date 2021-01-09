package com.hammer67.ajecimface.provider;

import com.hammer67.ajecimface.models.FCMBody;
import com.hammer67.ajecimface.models.FCMResponse;
import com.hammer67.ajecimface.retrofit.IFCMApi;
import com.hammer67.ajecimface.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
