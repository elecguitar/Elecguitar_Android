package com.elecguitar.android.viewmodel

import androidx.lifecycle.ViewModel
import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.util.ListLiveData
import com.naver.maps.geometry.LatLng

class MainViewModel: ViewModel() {
    var carList = ListLiveData()
    var markerChargeStation: ChargeStation? = null
    
    var filterCompanyList = mutableListOf<String>()
    var filterCompanyIdList = mutableListOf<Int>()
    var filterStartPrice = 1000
    var filterEndPrice = 12000
    var filterStartElecMileage = 2
    var filterEndElecMileage = 10
    
    var currPositionLat: Double? = null
    var currPositionLng: Double? = null
}