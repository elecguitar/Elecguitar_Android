package com.elecguitar.android.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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

        binding.apply {
            (requireContext() as MainActivity).apply{
                setSupportActionBar(toolbar)
                supportActionBar!!.setDisplayShowCustomEnabled(true)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
                setHasOptionsMenu(true)
            }
        }

        CarDetailService().getCarById(carId, GetCarByIdCallback())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.car_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mainActivity.openFragment(4, "", 0)
                true
            }
            else -> false
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

            setCompanyLogo(carDetailData.company)

            tvName.text = carDetailData.carName
            tvManufactureDate.text = carDetailData.manufactureDate

            tvPrice.text = if (carDetailData.price != 0) {
                "${CommonUtil().makeComma(carDetailData.price)} 만원"
            } else {
                "-"
            }
            tvMileage.text = if (carDetailData.elecMileage != 0.0) {
                "${carDetailData.elecMileage} km/kWh"
            } else {
                "-"
            }
            tvBatteryCapacity.text = if (carDetailData.batteryCapacity != 0) {
                "${carDetailData.batteryCapacity} Ah"
            } else {
                "-"
            }
            tvCarType.text = if (carDetailData.carType != "") {
                carDetailData.carType
            } else {
                "-"
            }
            tvDistance.text = if (carDetailData.maximumDistance != 0) {
                "${carDetailData.maximumDistance} km"
            } else {
                "-"
            }
            tvCapacity.text = if (carDetailData.capacity != 0) {
                "${carDetailData.capacity} 명"
            } else {
                "-"
            }
            tvMortorOutput.text = if (carDetailData.maximumMotorOutput != 0) {
                "${carDetailData.maximumMotorOutput} kw"
            } else {
                "-"
            }
            tvMortorToque.text = if (carDetailData.maximumMotorTorque != 0) {
                "${carDetailData.maximumMotorTorque} Nm"
            } else {
                "-"
            }
            tvWeight.text = if (carDetailData.weight != 0) {
                "${carDetailData.weight} Kg"
            } else {
                "-"
            }
            tvSpeed.text = if (carDetailData.maximumSpeed != 0) {
                "${carDetailData.maximumSpeed} km/h"
            } else {
                "-"
            }
        }
    }

    private fun setCompanyLogo(company: String) {
        if (company == "현대") {
            binding.ivCompany.setImageResource(R.drawable.c1)
        } else if (company == "기아") {
            binding.ivCompany.setImageResource(R.drawable.c2)
        } else if (company == "쌍용") {
            binding.ivCompany.setImageResource(R.drawable.c3)
        } else if (company == "르노코리아") {
            binding.ivCompany.setImageResource(R.drawable.c4)
        } else if (company == "쉐보레") {
            binding.ivCompany.setImageResource(R.drawable.c5)
        } else if (company == "제네시스") {
            binding.ivCompany.setImageResource(R.drawable.c6)
        } else if (company == "BMW") {
            binding.ivCompany.setImageResource(R.drawable.c7)
        } else if (company == "벤츠") {
            binding.ivCompany.setImageResource(R.drawable.c8)
        } else if (company == "아우디") {
            binding.ivCompany.setImageResource(R.drawable.c9)
        } else if (company == "폭스바겐") {
            binding.ivCompany.setImageResource(R.drawable.c10)
        } else if (company == "볼보") {
            binding.ivCompany.setImageResource(R.drawable.c11)
        } else if (company == "토요타") {
            binding.ivCompany.setImageResource(R.drawable.c12)
        } else if (company == "렉서스") {
            binding.ivCompany.setImageResource(R.drawable.c13)
        } else if (company == "닛산") {
            binding.ivCompany.setImageResource(R.drawable.c14)
        } else if (company == "미니") {
            binding.ivCompany.setImageResource(R.drawable.c15)
        } else if (company == "르노") {
            binding.ivCompany.setImageResource(R.drawable.c16)
        } else if (company == "푸조") {
            binding.ivCompany.setImageResource(R.drawable.c17)
        } else if (company == "DS") {
            binding.ivCompany.setImageResource(R.drawable.c18)
        } else if (company == "재규어") {
            binding.ivCompany.setImageResource(R.drawable.c19)
        } else if (company == "테슬라") {
            binding.ivCompany.setImageResource(R.drawable.c20)
        } else if (company == "포르쉐") {
            binding.ivCompany.setImageResource(R.drawable.c21)
        } else if (company == "미쓰비시") {
            binding.ivCompany.setImageResource(R.drawable.c22)
        } else if (company == "미아 전기자동차") {
            binding.ivCompany.setImageResource(R.drawable.c23)
        } else if (company == "쯔더우") {
            binding.ivCompany.setImageResource(R.drawable.c24)
        } else if (company == "캠시스") {
            binding.ivCompany.setImageResource(R.drawable.c25)
        } else if (company == "폴스타") {
            binding.ivCompany.setImageResource(R.drawable.c26)
        } else if (company == "디피코") {
            binding.ivCompany.setImageResource(R.drawable.c27)
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