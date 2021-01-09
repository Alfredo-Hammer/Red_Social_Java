package com.hammer67.ajecimface.retrofit;


import com.hammer67.ajecimface.models.FCMBody;
import com.hammer67.ajecimface.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=\tAAAAWvnleyE:APA91bEu0hpT1F_nK2XN1H5eUWKiaaFQ6R3QN1zrgWIpy0wZhpPpYV6nXs8VX9MGK3GDT93iYINImzPGKnTTNrG1A6V27lKxN1ipGfxQX79EK1YH-ipHXmPhGnGprcpBAE2fuaGnJu8v"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
