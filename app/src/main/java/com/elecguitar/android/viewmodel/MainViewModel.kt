package com.elecguitar.android.viewmodel

import androidx.lifecycle.ViewModel
import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.util.ArticleListLiveData
import com.elecguitar.android.util.ListLiveData
import com.naver.maps.geometry.LatLng

class MainViewModel: ViewModel() {
    var carList = ListLiveData()
    var articleList = ArticleListLiveData()
    var markerChargeStation: ChargeStation? = null
    var currPositionLat: Double? = null
    var currPositionLng: Double? = null
}