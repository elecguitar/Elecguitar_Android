package com.elecguitar.android.fragment


import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentMapBinding
import com.elecguitar.android.response.ChargeStation
import com.elecguitar.android.response.GeoCoderResponse
import com.elecguitar.android.response.SearchResponse
import com.elecguitar.android.service.ChargeStationService
import com.elecguitar.android.service.GeoCoderService
import com.elecguitar.android.util.RetrofitCallback
import java.io.IOException
import java.util.Locale

private const val TAG = "MapFragment_싸피"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private lateinit var cameraFocus: LatLng
    private var chargeStationList: MutableList<ChargeStation> = mutableListOf()
    private var markerList: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
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

        binding.naverChargeStationSearch.setOnClickListener {
            val res = GeoCoderService().getAddressByLatLng(
                "${cameraFocus.longitude},${cameraFocus.latitude}",
                GetAddressByLatLngCallback()
            )
            Log.d(TAG, "onViewCreated: ${res}")
            val address = getAddress(cameraFocus.latitude, cameraFocus.longitude)
            Log.d(TAG, "onViewCreated: ${address}")
            if (address != null) {
                ChargeStationService().getChargeStationByAddress(
                    address,
                    GetChargeStationByAddressCallback()
                )
            }
        }

    }

    override fun onMapReady(map: NaverMap) {
        Log.d(TAG, "onMapReady: ")
        naverMap = map
        cameraFocus = LatLng(
            naverMap.cameraPosition.target.latitude,
            naverMap.cameraPosition.target.longitude
        )

        val marker = Marker()
        marker.position = cameraFocus
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.focus_marker)

        // 사용자 현재 위치 받아오기
        var currentLocation: Location?
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLocation = location
                // 위치 오버레이의 가시성은 기본적으로 false로 지정되어 있습니다. 가시성을 true로 변경하면 지도에 위치 오버레이가 나타납니다.
                // 파랑색 점, 현재 위치 표시
                if (currentLocation != null) {
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


                // 카메라 현재위치로 이동
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        currentLocation!!.latitude,
                        currentLocation!!.longitude
                    )
                )
                naverMap.moveCamera(cameraUpdate)

                // 카메라 포커스에 현위치 정보를 담음
                cameraFocus = LatLng(
                    naverMap.cameraPosition.target.latitude,
                    naverMap.cameraPosition.target.longitude
                )
                marker.position = cameraFocus
            }

        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true

        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource


        // 카메라의 움직임에 대한 이벤트 리스너 인터페이스.
        // 참고 : https://navermaps.github.io/android-map-sdk/reference/com/naver/maps/map/package-summary.html
        naverMap.addOnCameraChangeListener { reason, animated ->
            cameraFocus = LatLng(
                // 현재 보이는 네이버맵의 정중앙 가운데로 마커 이동
                naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude
            )
            marker.position = cameraFocus
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

    private fun getAddress(lat: Double, lng: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult: String? = null
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
//            Log.d(TAG, "getAddress: ${address}")
            if (address.size > 0) {
                // 주소 받아오기
                val currentLocationAddress = address[0]
                if (currentLocationAddress.thoroughfare != null) {
                    currentLocationAddress.apply {
                        addressResult = "$thoroughfare"
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult
    }


    inner class GetChargeStationByAddressCallback : RetrofitCallback<SearchResponse> {
        override fun onSuccess(
            code: Int,
            responseData: SearchResponse
        ) {
            updateMarkers(responseData.chargeStationList)
            Log.d(TAG, "onSuccess: ${chargeStationList}")
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

        private fun updateMarkers(list: List<ChargeStation>) {
            if (list != null) {
                markerList.forEach {
                    it.map = null
                }
                markerList.clear()
                chargeStationList.clear()
                chargeStationList.addAll(list)
                chargeStationList.forEach {
                    val sMarker = Marker()
                    sMarker.position = LatLng(it.lat.toDouble(), it.longi.toDouble())
                    sMarker.map = naverMap
                    sMarker.icon = OverlayImage.fromResource(R.drawable.ev_marker)
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
            Log.d(TAG, "onSuccess: ${responseData.results}")
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