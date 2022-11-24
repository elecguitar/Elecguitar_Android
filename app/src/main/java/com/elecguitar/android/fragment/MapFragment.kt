package com.elecguitar.android.fragment


import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentMapBinding
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.response.GeoCoderResponse
import com.elecguitar.android.response.SearchResponse
import com.elecguitar.android.service.ChargeStationService
import com.elecguitar.android.service.GeoCoderService
import com.elecguitar.android.util.CheckPermission
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

private const val TAG = "MapFragment_싸피"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var checkPermission: CheckPermission
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private lateinit var cameraFocus: LatLng
    private var currentLocation: Location? = null
    private var currMarker: Marker? = null
    private var chargeStationList: MutableList<ChargeStation> = mutableListOf()
    private var regionList: MutableList<String> = mutableListOf()
    private var lastClickTime: Long = 0
    private var markerList: MutableList<Marker> = mutableListOf()
    private var isOpen: Boolean = false
    private var result: ArrayList<String>? = null
    private var selected: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkPermission = CheckPermission(requireContext())
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            mapView = naverMap
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, 1000)

        initListener()

    }

    private fun clearList(){
        markerList.forEach {
            it.map = null
        }

        if(markerList.size != 0){
            markerList.clear()
        }

        if(chargeStationList.size != 0){
            chargeStationList.clear()
        }
    }

    private fun initListener(){
        binding.apply{
            fabSearch.setOnClickListener {
                toggle()
            }

            fabSearchLocation.setOnClickListener {
                Log.d(TAG, "onViewCreated: ${cameraFocus.longitude},${cameraFocus.latitude}")
                GeoCoderService().getAddressByLatLng(
                    "${cameraFocus.longitude},${cameraFocus.latitude}",
                    GetAddressByLatLngCallback()
                )
            }

            fabSearchMic.setOnClickListener {
                startStt()
            }

            btnLocation.setOnClickListener {
                Log.d(TAG, "initListener: clclcl")
                if (!checkPermission.runtimeCheckPermission(requireContext(), *MainActivity().runtimePermissions)) {
                    ActivityCompat.requestPermissions(
                        requireActivity(), MainActivity().runtimePermissions,
                        MainActivity.PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun startStt(){
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.")
        startActivityForResult(i, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || resultCode != Activity.RESULT_OK || requestCode != 1000) {
            Toast.makeText(requireContext(), "오류발생", Toast.LENGTH_SHORT).show()
            return
        }
        result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) //인식된 데이터 list 받아옴.
        val sa: Array<String> = result!!.toTypedArray<String>()
        Log.d(TAG, "onActivityResult: $sa")
        val ad = AlertDialog.Builder(requireContext())
            .setTitle("선택하세요.")
            .setSingleChoiceItems(
                sa, -1
            ) { dialog, which -> selected = result!!.get(which) }
            .setPositiveButton(
                "확인"
            ) { dialog, which ->
                if (selected != null) {
                    clearList()
                    ChargeStationService().getChargeStationByAddress(
                        selected!!,
                        GetChargeStationByAddressCallback()
                    )
                }
            }
            .setNegativeButton("취소") { dialog, which ->
                selected = null
            }.create()
        ad.show()
    }

    private fun toggle(){
        if(isOpen){
            binding.fabSearch.setImageResource(R.drawable.ic_baseline_location_on_24)
            ObjectAnimator.ofFloat(binding.fabSearchLocation, View.TRANSLATION_Y, 0f).apply{
                start()
            }
            ObjectAnimator.ofFloat(binding.fabSearchMic, View.TRANSLATION_Y, 0f).apply {
                start()
            }
        }
        else{
            binding.fabSearch.setImageResource(R.drawable.ic_baseline_close_24)
            ObjectAnimator.ofFloat(binding.fabSearchLocation, View.TRANSLATION_Y, -180f).apply{
                start()
            }
            ObjectAnimator.ofFloat(binding.fabSearchMic, View.TRANSLATION_Y, -360f).apply {
                start()
            }
        }
        isOpen = !isOpen
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        cameraFocus = LatLng(
            naverMap.cameraPosition.target.latitude,
            naverMap.cameraPosition.target.longitude
        )

        currMarker = Marker().apply{
            position = cameraFocus
            this.map = naverMap
            icon = OverlayImage.fromResource(R.drawable.focus_marker)
        }

        getCurrentPositionAndMove()
        // 사용자 현재 위치 받아오기

        currMarker!!.position = cameraFocus

        binding.btnLocation.map = naverMap

        naverMap.locationSource = locationSource

        // 카메라의 움직임에 대한 이벤트 리스너 인터페이스.
        // 참고 : https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/package-summary.html
        naverMap.addOnCameraChangeListener { reason, animated ->
            cameraFocus = LatLng(
                // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            currMarker!!.position = cameraFocus
        }

    }

    private fun getCurrPosition(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(TAG, "getCurrPosition: ${location}")
                currentLocation = location
                // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                // 파랑색 점, 현재 위치 표시

                if (currentLocation != null) {
                    mainViewModel.currPositionLat = currentLocation!!.latitude
                    mainViewModel.currPositionLng = currentLocation!!.longitude

                    naverMap.locationOverlay.run {
                        isVisible = true
                        position = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    }

                } else {
                    naverMap.locationOverlay.run {
                        isVisible = true
                        position = LatLng(36.1093, 128.4166)
                    }
                }
            }
    }

    private fun getCurrentPositionAndMove(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(TAG, "getCurrPosition: ${location}")
                currentLocation = location
                // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                // 파랑색 점, 현재 위치 표시

                if (currentLocation != null) {
                    mainViewModel.currPositionLat = currentLocation!!.latitude
                    mainViewModel.currPositionLng = currentLocation!!.longitude
                    cameraFocus = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)

                    naverMap.locationOverlay.run {
                        isVisible = true
                        position = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                    }

                    val cameraUpdate = CameraUpdate.scrollTo(
                        LatLng(
                            currentLocation!!.latitude,
                            currentLocation!!.longitude
                        )
                    )
                    naverMap.moveCamera(cameraUpdate)

                    currMarker!!.position = cameraFocus

                } else {
                    naverMap.locationOverlay.run {
                        isVisible = true
                        position = LatLng(36.1093, 128.4166)
                    }
                }
            }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {

                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    inner class GetChargeStationByAddressCallback : RetrofitCallback<SearchResponse> {

        override fun onSuccess(
            code: Int,
            responseData: SearchResponse
        ) {
            updateMarkers(responseData.chargeStationList)
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

        private fun updateMarkers(list: List<ChargeStation>) {

            if (list != null) {
                chargeStationList.addAll(list)

                if(chargeStationList.size != 0 && chargeStationList != null){
                    chargeStationList = chargeStationList.distinct().toMutableList()
                }

                chargeStationList.forEach {

                    val sMarker = Marker().apply{
                        position = LatLng(it.lat.toDouble(), it.longi.toDouble())
                        map = naverMap
                        icon = OverlayImage.fromResource(R.drawable.ev_marker)
                        captionTextSize = 0.001f
                        captionText = it.cpId.toString() + it.csId.toString()
                        isHideCollidedCaptions = true

                        setOnClickListener{
                            getCurrPosition()

                            val elapsedRealtime = SystemClock.elapsedRealtime()
                            if((elapsedRealtime - lastClickTime) < 1000){
                                return@setOnClickListener true
                            }
                            lastClickTime = SystemClock.elapsedRealtime()

                            chargeStationList.forEach{
                                if(captionText == it.cpId.toString() + it.csId.toString()){
                                    mainViewModel.markerChargeStation = it
                                    ChargeStationBottomFragment.newInstance().show(
                                        parentFragmentManager, ChargeStationBottomFragment.TAG
                                    )
                                    return@forEach
                                }
                            }

                            true
                        }
                    }
                    markerList.add(sMarker)
                }
            }
        }

    }

    inner class GetAddressByLatLngCallback : RetrofitCallback<GeoCoderResponse> {
        override fun onSuccess(
            code: Int,
            responseData: GeoCoderResponse
        ) {
            val resultList = responseData.results
            Log.d(TAG, "resultList: ${resultList}")
            val regions = mutableListOf<String>()

            resultList.forEach{
                regions.add("${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}")
                Log.d(TAG, "onSuccess1: ${it.region.area1.name} ${it.region.area2.name} ${it.region.area3.name} ${it.region.area4.name}")
            }
            if(resultList.size > 3){
                resultList[3].let{
                    if(it.land.name != "" && it.land.name != null){
                        regions.add("${it.region.area1.name} ${it.region.area2.name} ${it.land.name}")
                        Log.d(TAG, "onSuccess2: ${it.region.area1.name} ${it.region.area2.name} ${it.land.name}")
                    }
                }
            }

            if(regionList.size != 0){
                regionList = regions.distinct() as MutableList<String>
            }

            clearList()

            regions.distinct().forEach{
                if (it != null) {
                    ChargeStationService().getChargeStationByAddress(
                        it,
                        GetChargeStationByAddressCallback()
                    )
                }
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1004
    }
}