package com.elecguitar.android.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentCarDetailBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.dto.CarDetail
import com.elecguitar.android.service.CarDetailService
import com.elecguitar.android.service.CarListService
import com.elecguitar.android.util.CommonUtil
import com.elecguitar.android.util.RetrofitCallback

private const val TAG = "CarDetailFragment_싸피"
class CarDetailFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentCarDetailBinding

    private var carId = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

        arguments?.let {
            carId = it.getInt("carId")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CarDetailService().getCarById(carId, GetCarByIdCallback())

        binding.apply {
            ivBack.setOnClickListener {

            }
        }
    }

    inner class GetCarByIdCallback: RetrofitCallback<CarDetail> {
        override fun onSuccess(code: Int, responseData: CarDetail) {
            responseData.let {
                Log.d(TAG, "onSuccess: ${responseData}")
                initView(responseData)
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "차 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    fun initView(carDetailData: CarDetail) {
        binding.apply {
            if (carDetailData.sellStatus == "시판") {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.elecguitar_yellow))
                chip.text = "시판"
            } else {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.elecguitar_g1))
                chip.text = "단종"
            }

            Glide.with(requireContext())
                .load(carDetailData.img)
                .into(ivCar)

            tvName.text = carDetailData.carName
            tvManufactureDate.text = carDetailData.manufactureDate
            tvPrice.text = "${CommonUtil().makeComma(carDetailData.price)} 만원"
            tvMileage.text = "${carDetailData.elecMileage} km/kWh"
            tvBatteryCapacity.text = "${carDetailData.batteryCapacity} Ah"
            tvCarType.text = carDetailData.carType
            tvDistance.text = "${carDetailData.maximumDistance} km"
            tvCapacity.text = "${carDetailData.capacity} 명"
            tvMortorOutput.text = "${carDetailData.maximumMotorOutput} kw"
            tvMortorToque.text = "${carDetailData.maximumMotorTorque} Nm"
            tvWeight.text = "${carDetailData.weight} Kg"
            tvSpeed.text = "${carDetailData.maximumSpeed} km/h"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            CarDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }

}