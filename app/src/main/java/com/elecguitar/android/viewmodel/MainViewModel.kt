package com.elecguitar.android.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ArticleResponse
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.util.ArticleListLiveData
import com.elecguitar.android.util.ListLiveData
import com.naver.maps.geometry.LatLng

class MainViewModel: ViewModel() {
    var carList = ListLiveData()
    var articleList = ArticleListLiveData()
    var markerChargeStation: ChargeStation? = null
    var articleDetail: ArticleResponse? = null
    
    var filterCompanyList = mutableListOf<String>()
    var filterCompanyIdList = mutableListOf<Int>()
    var filterStartPrice = 0
    var filterEndPrice = 20000
    var filterStartElecMileage = 0
    var filterEndElecMileage = 10

    var currPositionLat: Double? = null
    var currPositionLng: Double? = null
    var tts: TextToSpeech? = null

    var isDialogShow = false
    var isUpload = false
    var isStartRecommend = true
    var prevFragmentPos = 4

    var recommendRes: String? = null
}