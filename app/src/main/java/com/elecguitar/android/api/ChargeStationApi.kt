package com.elecguitar.android.api

import com.elecguitar.android.ApplicationClass
import com.elecguitar.android.response.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChargeStationApi {

    @GET("getEvSearchList?serviceKey=${ApplicationClass.CHARGE_STATION_SERVICE_KEY}&page=1&perPage=100")
    fun searchChargeStations(@Query("cond[addr::LIKE]") address: String): Call<SearchResponse>

}