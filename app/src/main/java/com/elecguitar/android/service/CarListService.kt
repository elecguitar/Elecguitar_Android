package com.elecguitar.android.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elecguitar.android.dto.Car
import com.elecguitar.android.util.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CarListService_싸피"
class CarListService {
    fun getAllCarList(): LiveData<List<Car>> {
        val carLiveData: MutableLiveData<List<Car>> = MutableLiveData()

        RetrofitUtil.carListService.getAllCarList().enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        carLiveData.value = res
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
            }
        })

        return carLiveData
    }
}