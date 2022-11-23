package com.elecguitar.android.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.elecguitar.android.databinding.FragmentChargeStationBottomBinding
import com.elecguitar.android.response.ChargeStation
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "ChargeStationBottomFrag_싸피"
class ChargeStationBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChargeStationBottomBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private var chargeStation: ChargeStation? = null
    private var currLat: Double? = null
    private var currLng: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

        chargeStation = mainViewModel.markerChargeStation
        currLat = mainViewModel.currPositionLat
        currLng = mainViewModel.currPositionLng
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


        initListener()

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

    private fun initListener(){
        binding.apply {
            btnCsFindRoad.setOnClickListener {
                var intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "http://m.map.naver.com/route.nhn" +
                                "?menu=route" +
                                "&sname=현위치${getAddress(currLat!!, currLng!!,"")}" +
                                "&sx=${currLng}" +
                                "&sy=${currLat}" +
                                "&ename=목적지${getAddress(chargeStation!!.lat.toDouble(), chargeStation!!.longi.toDouble(), "")}" +
                                "&ex=${chargeStation!!.longi.toDouble()}" +
                                "&ey=${chargeStation!!.lat.toDouble()}" +
                                "&pathType=0" +
                                "&showMap=true"
                    )
                )
                startActivity(intent)
            }
            btnCsCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun getAddress(lat: Double, lng: Double, sted: String): String? {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult: String = sted
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
            Log.d(TAG, "getAddress: ${lat}, ${lng}")
            Log.d(TAG, "getAddress: ${address}")
            if (address.size > 0) {
                // 주소 받아오기
                val addr = address[0]
                addressResult = ": ${addr.adminArea} ${addr.locality} ${addr.thoroughfare} ${addr.featureName}"
                Log.d(TAG, "getAddress: ${addressResult}")
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult
    }


    companion object {
        const val TAG = "ChargeStationBottomFragment"
        fun newInstance(): ChargeStationBottomFragment{
            return ChargeStationBottomFragment()
        }
    }
}