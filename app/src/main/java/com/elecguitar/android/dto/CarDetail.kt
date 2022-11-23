package com.elecguitar.android.dto

data class CarDetail(
    var batteryCapacity: Int = 0,
    var capacity: Int = 0,
    var carId: Int = 0,
    var carName: String = "",
    var carType: String = "",
    var company: String = "",
    var elecMileage: Double = 0.0,
    var img: String = "",
    var manufactureDate: String = "",
    var maximumDistance: Int = 0,
    var maximumMotorOutput: Int = 0,
    var maximumMotorTorque: Int = 0,
    var maximumSpeed: Int = 0,
    var price: Int = 0,
    var sellStatus: String = "",
    var weight: Int = 0
)