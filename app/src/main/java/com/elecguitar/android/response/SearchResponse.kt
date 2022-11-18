package com.elecguitar.android.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("currentCount") val currentCount: Int,
    @SerializedName("data") val chargeStationList: List<ChargeStation>,
    @SerializedName("matchCount")val matchCount: Int,
    @SerializedName("page")val page: Int,
    @SerializedName("perPage")val perPage: Int,
    @SerializedName("totalCount")val totalCount: Int
)