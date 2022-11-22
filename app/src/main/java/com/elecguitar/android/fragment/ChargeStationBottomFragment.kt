package com.elecguitar.android.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentChargeStationBottomBinding
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChargeStationBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChargeStationBottomBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChargeStationBottomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chargeStation = mainViewModel.markerChargeStation
        binding.apply{
            tvCpid.text = "No. ${chargeStation!!.cpId}"
            tvCsAddress.text = chargeStation!!.addr

            val type = when(chargeStation!!.chargeTp){
                "1" -> "완속"
                "2" -> "급속"
                else -> "error"
            }
            tvType.text = type

            val way = when(chargeStation!!.cpTp){
                "1" -> "B타입(5핀)"
                "2" -> "C타입(5핀)"
                "3" -> "BC타입(5핀)"
                "4" -> "BC타입(7핀)"
                "5" -> "DC차 데모"
                "6" -> "AC 3상"
                "7" -> "DC콤보"
                "8" -> "DC차데모+DC콤보"
                "9" -> "DC차데모+AC3상"
                "10" -> "DC차데모+DC콤보, AC3상"
                else -> "error"
            }
            tvWay.text = way

            val state = when(chargeStation!!.cpStat){
                "1" -> {
                    "충전가능"
                }
                "2" -> {
                    "충전중"
                }
                else -> {
                    chipStatus.chipBackgroundColor = ColorStateList(
                        arrayOf(
                            intArrayOf(-android.R.attr.state_checked)
                        ),
                        intArrayOf(R.color.elecguitar_red)
                    )
                    "사용불가"
                }
            }
            chipStatus.text = state
        }

    }


    companion object {
        const val TAG = "ChargeStationBottomFragment"
        fun newInstance(): ChargeStationBottomFragment{
            return ChargeStationBottomFragment()
        }
    }
}