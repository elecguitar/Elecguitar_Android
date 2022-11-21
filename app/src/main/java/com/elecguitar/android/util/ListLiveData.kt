package com.elecguitar.android.util

import androidx.lifecycle.MutableLiveData
import com.elecguitar.android.dto.Car

class ListLiveData : MutableLiveData<MutableList<Car>>() {
    private val datas = mutableListOf<Car>()

    init {
        value = datas
    }

    fun addAll(items: List<Car>) {
        datas.addAll(items)
        value = datas
    }

    fun sortByPriceAsc() {
        val temp = value!!.toMutableList()
        temp.sortBy { it.price }
        value = temp.filter {
            it.price > 0
        }.toMutableList()
    }

    fun sortByPriceDesc() {
        val temp = value!!.toMutableList()
        temp.sortByDescending { it.price }
        value = temp.filter {
            it.price > 0
        }.toMutableList()
    }

    fun sortByMileageAsc() {
        val temp = value!!.toMutableList()
        temp.sortBy { it.elecMileage }
        value = temp.filter {
            it.elecMileage > 0
        }.toMutableList()
    }

    fun sortByMileageDesc() {
        val temp = value!!.toMutableList()
        temp.sortByDescending { it.elecMileage }
        value = temp.filter {
            it.elecMileage > 0
        }.toMutableList()
    }
}