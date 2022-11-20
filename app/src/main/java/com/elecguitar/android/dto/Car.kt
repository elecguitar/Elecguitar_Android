package com.elecguitar.android.dto

data class Car (
    var id: Int = 0,
    var name: String = "",
    var imageUrl: String = "",
    var manufactureDate: String = "",
    var price: Int = 0,
    var elecMileage: Float = 0.0f,
    var batteryCapacity: Int = 0,
    var sellStatus: String = ""
)