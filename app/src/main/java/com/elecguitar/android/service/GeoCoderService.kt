package com.elecguitar.android.service

import android.util.Log
import com.elecguitar.android.response.GeoCoderResponse
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "GeoCoderService_싸피"
class GeoCoderService {

    fun getAddressByLatLng(latLng: String, callback: RetrofitCallback<GeoCoderResponse>){
        val addressRequest: Call<GeoCoderResponse> = RetrofitUtil.geoCoderService.getAddressByLatLng(latLng)

        addressRequest.enqueue(object: Callback<GeoCoderResponse> {
            override fun onResponse(
                call: Call<GeoCoderResponse>,
                response: Response<GeoCoderResponse>
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

            override fun onFailure(call: Call<GeoCoderResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: fail...")
            }

        })
    }

}