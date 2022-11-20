package com.elecguitar.android.response

import com.google.gson.annotations.SerializedName

data class Region(
    @SerializedName("area0")val area0: Area,
    @SerializedName("area1")val area1: Area,
    @SerializedName("area2")val area2: Area,
    @SerializedName("area3")val area3: Area,
    @SerializedName("area4")val area4: Area
)