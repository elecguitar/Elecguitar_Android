package com.elecguitar.android.response

import com.google.gson.annotations.SerializedName

data class ChargeStation(
    @SerializedName("addr") val addr: String,
    @SerializedName("chargeTp") val chargeTp: String,
    @SerializedName("cpId") val cpId: Int,
    @SerializedName("cpNm") val cpNm: String,
    @SerializedName("cpStat") val cpStat: String,
    @SerializedName("cpTp") val cpTp: String,
    @SerializedName("csId") val csId: Int,
    @SerializedName("csNm") val csNm: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("longi") val longi: String,
    @SerializedName("statUpdatetime") val statUpdatetime: String
)