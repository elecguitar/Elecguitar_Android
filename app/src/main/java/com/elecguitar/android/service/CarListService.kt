package com.elecguitar.android.service

import com.elecguitar.android.dto.Car
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarListService {
    fun getAllCarList(callback: RetrofitCallback<List<Car>>) {
        RetrofitUtil.carListService.getAllCarList().enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }
}