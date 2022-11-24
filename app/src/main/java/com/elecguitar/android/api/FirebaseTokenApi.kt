package com.elecguitar.android.api

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseTokenApi {
    // Token정보 서버로 전송
    @POST("fcmapi/token")
    fun uploadToken(@Query("token") token: String): Call<String>
}