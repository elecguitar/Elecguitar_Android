package com.elecguitar.android.api

import com.elecguitar.android.dto.Car
import retrofit2.Call
import retrofit2.http.GET

interface CarListApi {

    @GET("carapi/car")
    fun getAllCarList(): Call<List<Car>>

}