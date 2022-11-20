package com.elecguitar.android.util

import com.elecguitar.android.api.ChargeStationApi
import com.elecguitar.android.ApplicationClass
import com.elecguitar.android.api.GeoCoderApi

class RetrofitUtil {
    companion object{

        val chargeStationService = ApplicationClass.retrofit.create(ChargeStationApi::class.java)
        val geoCoderService = ApplicationClass.naverRetrofit.create(GeoCoderApi::class.java)

    }
}