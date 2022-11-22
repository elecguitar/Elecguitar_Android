package com.elecguitar.android.viewmodel

import androidx.lifecycle.ViewModel
import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.util.ListLiveData

class MainViewModel: ViewModel() {
    var carList = ListLiveData()
    var markerChargeStation: ChargeStation? = null
}