package com.elecguitar.android.service

import android.util.Log
import com.elecguitar.android.dto.CarDetail
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CarDetailService_싸피"
class CarDetailService {
    fun getCarById(carId: Int, callback: RetrofitCallback<CarDetail>) {
        RetrofitUtil.carDetailService.getCarById(carId).enqueue(object : Callback<CarDetail> {
            override fun onResponse(call: Call<CarDetail>, response: Response<CarDetail>) {
                val res = response.body()
                Log.d(TAG, "onResponse: ${res}")
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<CarDetail>, t: Throwable) {
                Log.d(TAG, "onFailure Throwable -> ${t.message}")
                callback.onError(t)
            }
        })
    }
}