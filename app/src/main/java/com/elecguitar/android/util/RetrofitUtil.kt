package com.elecguitar.android.util

import com.elecguitar.android.api.ChargeStationApi
import com.elecguitar.android.ApplicationClass

class RetrofitUtil {
    companion object{

        val chargeStationService = ApplicationClass.retrofit.create(ChargeStationApi::class.java)

    }
}