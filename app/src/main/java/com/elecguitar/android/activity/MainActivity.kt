package com.elecguitar.android.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.elecguitar.android.R
import com.elecguitar.android.databinding.ActivityMainBinding
import com.elecguitar.android.fragment.*
import com.elecguitar.android.util.CheckPermission
import com.elecguitar.android.viewmodel.MainViewModel
import org.altbeacon.beacon.*

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 8
        private const val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"
        private const val BEACON_MAJOR = "10004"
        private const val BEACON_MINOR = "54480"
        private const val BLUETOOTH_ADDRESS = "54:6C:0E:BD:28:4E"
        private const val BEACON_DISTANCE = 5.0
    }

    private lateinit var beaconManager: BeaconManager
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var checkPermission: CheckPermission

    private var bluetoothAdapter: BluetoothAdapter? = null

    private val runtimePermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    // Beacon의 Region 설정
    // 비교데이터들로, 설치 지역이 어딘지 판단하기 위한 데이터.
    private val region = Region(
        "estimote",
        listOf(
            Identifier.parse(BEACON_UUID),
            Identifier.parse(BEACON_MAJOR),
            Identifier.parse(BEACON_MINOR)),
        BLUETOOTH_ADDRESS
    )
    private var eventPopUpAble = true

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()

        binding.apply {
            bottomNavigationView.apply {
                setOnNavigationItemSelectedListener { item ->
                    when(item.itemId){
                        R.id.mapFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, MapFragment())
                                .commit()
                            true
                        }
                        R.id.benefitFragment -> {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, BenefitFragment())
                                .commit()
                            true
                        }
                        else -> false
                    }
                }
            }

            fab.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, HomeFragment())
                    .commit()
            }
        }

        setBeacon()

        createNotificationChannel("ssafy_channel", "ssafy")
    }

    override fun onRestart() {
        super.onRestart()
        this.hideBottomNav(false)
    }

    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index){
            // 차 상세 정보
//            1 -> transaction.replace(R.id.frameLayout, CarDetailFragment())
//                .addToBackStack(null)
            // TODO : 뉴스 상세 넣기
            2 -> transaction.replace(R.id.frameLayout, ArticleDetailFragment())
                .addToBackStack(null)
        }
        transaction.commit()
    }

    fun hideBottomNav(state : Boolean){
        if(state) binding.bottomNavigationView.visibility =  View.GONE
        else binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun setBeacon(){
        //BeaconManager 지정
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        checkPermission = CheckPermission(this)
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Toast.makeText(this, "블루투스 기능을 확인해 주세요.", Toast.LENGTH_SHORT).show()
            val bleIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(bleIntent, 1)
        }

        if (!checkPermission.runtimeCheckPermission(this, *runtimePermissions)) {
            ActivityCompat.requestPermissions(this, runtimePermissions, PERMISSION_REQUEST_CODE)
        } else { //이미 전체 권한이 있는 경우
            startScan()
        }
    }

    private fun startScan() {
        beaconManager.addMonitorNotifier(monitorNotifier)
        beaconManager.startMonitoring(region)

        beaconManager.addRangeNotifier(rangeNotifier)
        beaconManager.startRangingBeacons(region)
    }

    var monitorNotifier: MonitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(region: Region) { //발견 함.
            Log.d(TAG, "I just saw an beacon for the first time!")
        }

        override fun didExitRegion(region: Region) { //발견 못함.
            Log.d(TAG, "I no longer see an beacon")
        }

        override fun didDetermineStateForRegion(state: Int, region: Region) { //상태변경
            Log.d(TAG, "I have just switched from seeing/not seeing beacons: $state")
        }
    }

    var rangeNotifier: RangeNotifier = object : RangeNotifier {
        override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
            beacons?.run{
                if (isNotEmpty()) {
                    forEach { beacon ->
                        // 사정거리 내에 있을 경우 이벤트 표시 다이얼로그 팝업
                        if (beacon.distance <= BEACON_DISTANCE) {
                            if (!mainViewModel.isDialogShow) {
                                showPickUpDialog()
                                mainViewModel.isDialogShow = true
                            }
                            Log.d(TAG, "didRangeBeaconsInRegion: distance 이내.")
                        } else {
                            Log.d(TAG, "didRangeBeaconsInRegion: distance 이외.")
                            eventPopUpAble = true
                        }
                        Log.d( TAG,"distance: " + beacon.distance + " id:" + beacon.id1 + "/" + beacon.id2 + "/" + beacon.id3)
                    }
                }
                if (isEmpty()) {
                    Log.d(TAG, "didRangeBeaconsInRegion: 비컨을 찾을 수 없습니다.")
                }
            }
        }
    }

    private fun showPickUpDialog() {
        CouponDialogFragment.newInstance().show(
            supportFragmentManager, CouponDialogFragment.TAG
        )
    }

    // NotificationChannel 설정
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)

            val notificationManager: NotificationManager
                    = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        beaconManager.stopMonitoring(region)
        beaconManager.stopRangingBeacons(region)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 권한을 모두 획득했다면.
                startScan()
            } else {
                checkPermission.requestPermission()
            }
        }
    }
}