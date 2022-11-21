package com.elecguitar.android.response

import com.google.gson.annotations.SerializedName

data class GeoCoderResponse(
    @SerializedName("results")val results: List<Result>,
    @SerializedName("status")val status: Status
)