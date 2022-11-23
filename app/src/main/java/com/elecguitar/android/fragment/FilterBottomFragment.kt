package com.elecguitar.android.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentFilterBottomBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.service.CarListService
import com.elecguitar.android.util.CommonUtil
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.slider.RangeSlider


class FilterBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterBottomBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_filter_bottom, container, false)
        binding = FragmentFilterBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        binding.apply {
            btnSearch.setOnClickListener {
                filterItems()
            }
        }

        sliderMove()
    }

    private fun initView() {
        binding.apply {
            mainViewModel.filterCompanyIdList.forEach { id ->
                Log.d("FilterBottomFragment_싸피", "initView: ${id}")

                view!!.findViewById<Chip>(id).isChecked = true
            }

            sliderPrice.apply {
                valueFrom = 1000f
                valueTo = 20000f
                values = arrayListOf(mainViewModel.filterStartPrice.toFloat(), mainViewModel.filterEndPrice.toFloat())
                stepSize = 1000f
            }

            sliderMileage.apply {
                valueFrom = 2f
                valueTo = 10f
                values = arrayListOf(mainViewModel.filterStartElecMileage.toFloat(), mainViewModel.filterEndElecMileage.toFloat())
                stepSize = 1f
            }

            tvPriceStart.text = CommonUtil().makeComma(mainViewModel.filterStartPrice)
            tvPriceEnd.text = CommonUtil().makeComma(mainViewModel.filterEndPrice)
            tvMileageStart.text = mainViewModel.filterStartElecMileage.toString()
            tvMileageEnd.text = mainViewModel.filterEndElecMileage.toString()
        }
    }

    private fun sliderMove() {
        binding.apply {
            sliderPrice.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    changePrice(binding.sliderPrice.values[0].toInt(), binding.sliderPrice.values[1].toInt())
                }
            })

            sliderMileage.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    changeMileage(binding.sliderMileage.values[0].toInt(), binding.sliderMileage.values[1].toInt())
                }
            })
        }
    }

    private fun changePrice(start: Int, end: Int) {
        binding.tvPriceStart.text = CommonUtil().makeComma(start)
        binding.tvPriceEnd.text = CommonUtil().makeComma(end)
    }

    private fun changeMileage(start: Int, end: Int) {
        binding.tvMileageStart.text = CommonUtil().makeComma(start)
        binding.tvMileageEnd.text = CommonUtil().makeComma(end)
    }

    private fun filterItems() {
        val filterCompany = mutableListOf<String>()
        var startPrice = 1000
        var endPrice = 12000
        var startElecMileage = 2
        var endElecMileage = 10

        mainViewModel.filterCompanyIdList.clear()

        binding.apply {
            chipGroup.checkedChipIds.forEach { id ->
                mainViewModel.filterCompanyIdList.add(id)
                filterCompany.add(chipGroup.findViewById<Chip>(id).text.toString())
            }
            startPrice = sliderPrice.values[0].toInt()
            endPrice = sliderPrice.values[1].toInt()
            startElecMileage = sliderMileage.values[0].toInt()
            endElecMileage = sliderMileage.values[1].toInt()
        }

        mainViewModel.apply {
            filterCompanyList = filterCompany
            filterStartPrice = startPrice
            filterEndPrice = endPrice
            filterStartElecMileage = startElecMileage
            filterEndElecMileage = endElecMileage
        }

        if (filterCompany.size == 0) {
            getFilteredList(endElecMileage, endPrice, startElecMileage, startPrice)
        } else {
            getFilteredListWithCompany(filterCompany, endElecMileage, endPrice, startElecMileage, startPrice)
        }
    }

    private fun getFilteredList(endElecMileage: Int, endPrice: Int, startElecMileage: Int, startPrice: Int) {
        CarListService().getPriceMileageFilteredCar(endElecMileage, endPrice, startElecMileage, startPrice, GetPriceMileageFilteredCarCallback())
    }

    inner class GetPriceMileageFilteredCarCallback : RetrofitCallback<List<Car>> {
        override fun onSuccess(code: Int, responseData: List<Car>) {
            responseData.let {
                mainViewModel.carList.replace(responseData)
                dismiss()
            }
        }

        override fun onError(t: Throwable) {
            Log.d("FilterBottomFragment_싸피", t.message ?: "차 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d("FilterBottomFragment_싸피", "onResponse: Error Code $code")
        }

    }

    private fun getFilteredListWithCompany(filterCompany: MutableList<String>, endElecMileage: Int, endPrice: Int, startElecMileage: Int, startPrice: Int) {
        CarListService().getPriceMileageCompanyFilteredCar(filterCompany, endElecMileage, endPrice, startElecMileage, startPrice, GetPriceMileageCompanyFilteredCarCallback())
    }

    inner class GetPriceMileageCompanyFilteredCarCallback : RetrofitCallback<List<Car>> {
        override fun onSuccess(code: Int, responseData: List<Car>) {
            responseData.let {
                mainViewModel.carList.replace(responseData)
                dismiss()
            }
        }

        override fun onError(t: Throwable) {
            Log.d("FilterBottomFragment_싸피", t.message ?: "차 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d("FilterBottomFragment_싸피", "onResponse: Error Code $code")
        }
    }

    companion object {
        const val TAG = "FilterBottomFragment"
        fun newInstance(): FilterBottomFragment{
            return FilterBottomFragment()
        }
    }
}