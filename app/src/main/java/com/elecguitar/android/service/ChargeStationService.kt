package com.elecguitar.android.service

import android.util.Log
import com.elecguitar.android.response.SearchResponse
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "ChargeStationService_싸피"
class ChargeStationService {

    fun getChargeStationByAddress(address: String, callback: RetrofitCallback<SearchResponse>){

        val chargeStationRequest: Call<SearchResponse> = RetrofitUtil.chargeStationService.searchChargeStations(address)
        Log.d(TAG, "getChargeStationByAddress:$chargeStationRequest")

        chargeStationRequest.enqueue(object: Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

                Log.d(TAG, "onResponse: ${response}")

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
                Log.d(TAG, "onFailure: ")

            }

        })


    }

}