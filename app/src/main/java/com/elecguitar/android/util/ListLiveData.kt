package com.elecguitar.android.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.elecguitar.android.dto.Car

private const val TAG = "ListLiveData_싸피"
class ListLiveData : MutableLiveData<MutableList<Car>>() {
    private val datas = mutableListOf<Car>()

    init {
        value = datas
    }

    fun addAll(items: List<Car>) {
        datas.addAll(items)
        value = datas
    }

    fun replace(items: List<Car>) {
        val temp = value!!.toMutableList()
        items.forEach { item ->
            temp.add(item)
        }
        value = temp
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