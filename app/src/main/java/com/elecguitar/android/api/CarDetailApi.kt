package com.elecguitar.android.api

import com.elecguitar.android.dto.CarDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CarDetailApi {

    @GET("car/{id}")
    fun getCarById(@Path("id") id: Int): Call<CarDetail>

}