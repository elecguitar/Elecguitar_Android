package com.elecguitar.android.api

import com.elecguitar.android.dto.Car
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CarListApi {

    @GET("car")
    fun getAllCarList(): Call<List<Car>>

    @GET("car/price-elecmileage")
    fun getPriceMileageFilteredCar(
        @Query("endElecMileage") endElecMileage: Int,
        @Query("endPrice") endPrice: Int,
        @Query("startElecMileage") startElecMileage: Int,
        @Query("startPrice") startPrice: Int)
    : Call<List<Car>>

    @GET("car/price-elecmileage-company")
    fun getPriceMileageCompanyFilteredCar(
        @Query("companyList") companyList: List<String>,
        @Query("endElecMileage") endElecMileage: Int,
        @Query("endPrice") endPrice: Int,
        @Query("startElecMileage") startElecMileage: Int,
        @Query("startPrice") startPrice: Int)
    : Call<List<Car>>
}