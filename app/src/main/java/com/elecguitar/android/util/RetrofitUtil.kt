package com.elecguitar.android.util

import com.elecguitar.android.api.ChargeStationApi
import com.elecguitar.android.ApplicationClass
import com.elecguitar.android.api.CarListApi
import com.elecguitar.android.api.GeoCoderApi

class RetrofitUtil {
    companion object{

        val chargeStationService = ApplicationClass.evRetrofit.create(ChargeStationApi::class.java)
        val geoCoderService = ApplicationClass.naverRetrofit.create(GeoCoderApi::class.java)
        val carListService = ApplicationClass.retrofit.create(CarListApi::class.java)

    }
}