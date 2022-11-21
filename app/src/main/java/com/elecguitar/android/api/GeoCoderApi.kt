package com.elecguitar.android.api

import com.elecguitar.android.response.GeoCoderResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCoderApi {

    @GET("v2/gc?output=json&orders=legalcode,addr,admcode,roadaddr")
    fun getAddressByLatLng(@Query("coords") latLng:String): Call<GeoCoderResponse>

}