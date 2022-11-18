package com.elecguitar.android.service

import com.elecguitar.android.response.SearchResponse
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChargeStationService {

    fun getChargeStationByAddress(address: String, callback: RetrofitCallback<SearchResponse>){
        val chargeStationRequest: Call<SearchResponse> = RetrofitUtil.chargeStationService.searchChargeStations(address)

        chargeStationRequest.enqueue(object: Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val res = response.body()
                if(response.code() == 200){
                    if(res != null){
                        callback.onSuccess(response.code(), res)
                    }else{
                        callback.onFailure(response.code())
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

            }

        })


    }

}