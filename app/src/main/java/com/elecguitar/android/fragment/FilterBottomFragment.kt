package com.elecguitar.android.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentFilterBottomBinding
import com.elecguitar.android.util.CommonUtil
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider


class FilterBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFilterBottomBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        binding.apply {
            sliderPrice.apply {
                valueFrom = 1000f
                valueTo = 20000f
                values = arrayListOf(1000f, 20000f)
                stepSize = 1000f
            }

            sliderMileage.apply {
                valueFrom = 2f
                valueTo = 10f
                values = arrayListOf(2f, 10f)
                stepSize = 1f
            }

            btnSearch.setOnClickListener {
                filterItems()
            }
        }

        sliderMove()
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
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "FilterBottomFragment"
        fun newInstance(): FilterBottomFragment{
            return FilterBottomFragment()
        }
    }
}