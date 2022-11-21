package com.elecguitar.android.dto

data class Car (
    var carId: Int = 0,
    var carName: String = "",
    var img: String = "",
    var manufactureDate: String = "",
    var price: Int = 0,
    var elecMileage: Float = 0.0f,
    var batteryCapacity: Int = 0,
    var sellStatus: String = ""
)