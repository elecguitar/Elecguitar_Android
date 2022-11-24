package com.elecguitar.android.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.elecguitar.android.ApplicationClass
import com.elecguitar.android.R
import com.elecguitar.android.api.FirebaseTokenApi
import com.elecguitar.android.databinding.ActivityMainBinding
import com.elecguitar.android.fragment.*
import com.elecguitar.android.util.CheckPermission
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.sqrt

private const val TAG = "MainActivity_싸피"
class MainActivity : AppCompatActivity() {

    private lateinit var beaconManager: BeaconManager
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var checkPermission: CheckPermission
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var accelerometerSensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var sensorEventListener: SensorEventListener? = null

    val runtimePermissions = arrayOf(
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

    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorEventListener = AccelerometerListener()

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
        createNotificationChannel(beacon_channel_id, "elecguitar_beacon")

        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            if(task.result != null){
                uploadToken(task.result!!)
            }
        })
        createNotificationChannel(noti_channel_id, "elecguitar_noti")
    }

    override fun onRestart() {
        super.onRestart()
        this.hideBottomNav(false)
    }

    override fun onResume() {
        super.onResume()
        // sensor Listener 등록
        // 구현
        sensorManager?.registerListener(
            sensorEventListener,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        // sensor Listener 삭제
        // 구현
        sensorManager?.unregisterListener(sensorEventListener)
    }

    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index) {
            // 차 상세 정보
            1 -> {
                transaction.replace(R.id.frameLayout, CarDetailFragment.newInstance(key, value))
                    .addToBackStack(null)
                mainViewModel.prevFragmentPos = 1
            }
            2 -> {
                transaction.replace(R.id.frameLayout, ArticleDetailFragment())
                    .addToBackStack(null)
                mainViewModel.prevFragmentPos = 2
            }
            3 -> {
                transaction.replace(R.id.frameLayout, BenefitFragment())
                    .addToBackStack(null)
                mainViewModel.prevFragmentPos = 3
            }
            4 -> {
                transaction.replace(R.id.frameLayout, HomeFragment())
                    .addToBackStack(null)
                mainViewModel.prevFragmentPos = 4
            }
            5 -> transaction.replace(R.id.frameLayout, RecommendFragment())
                .addToBackStack(null)

            6 -> transaction.replace(R.id.frameLayout, RecommendResFragment())
                .addToBackStack(null)
        }
        transaction.commit()
    }

    fun hideBottomNav(state : Boolean){
        binding.apply {
            if(state) {
                bottomAppBar.visibility =  View.GONE
                fab.visibility = View.GONE
            } else {
                bottomAppBar.visibility =  View.VISIBLE
                fab.visibility = View.VISIBLE
            }
        }
    }

    private val SHAKE_THRESHOLD_GRAVITY = 2.7F

    // 가속도 센서 이용
    private inner class AccelerometerListener : SensorEventListener {
        private var count = 0
        // 구현
        override fun onSensorChanged(event: SensorEvent?) {
            val axisX = event!!.values[0]
            val axisY = event!!.values[1]
            val axisZ = event!!.values[2]

            val gravityX = axisX / SensorManager.GRAVITY_EARTH
            val gravityY = axisY / SensorManager.GRAVITY_EARTH
            val gravityZ = axisZ / SensorManager.GRAVITY_EARTH

            val f = gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ
            val gForce = sqrt(f.toDouble()).toFloat()

            if(gForce > SHAKE_THRESHOLD_GRAVITY && mainViewModel.isStartRecommend){
                count += 1
                if(count >= 20){
                    count = 0
                    mainViewModel.isStartRecommend = false
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, 100))
                    };
                    if(mainViewModel.prevFragmentPos == 1){
                        mainViewModel.prevFragmentPos = 4
                    }
                    moveFragment(5,"",0)
                }
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

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
        }

        override fun didExitRegion(region: Region) { //발견 못함.
        }

        override fun didDetermineStateForRegion(state: Int, region: Region) { //상태변경
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
                        } else {
                            eventPopUpAble = true
                        }
                    }
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

    override fun onBackPressed() {
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을\n한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() //액티비티 종료
        }
    }

    companion object{
        const val PERMISSION_REQUEST_CODE = 8
        private const val BEACON_UUID = "fda50693-a4e2-4fb1-afcf-c6eb07647825"
        private const val BEACON_MAJOR = "10004"
        private const val BEACON_MINOR = "54480"
        private const val BLUETOOTH_ADDRESS = "54:6C:0E:BD:28:4E"
        private const val BEACON_DISTANCE = 3.0

        // Beacon Channel ID
        const val beacon_channel_id = "elecguitar_beacon_channel"

        // Notification Channel ID
        const val noti_channel_id = "elecguitar_noti_channel"

        fun uploadToken(token:String){
            // 새로운 토큰 수신 시 서버로 전송
            val storeService = ApplicationClass.retrofit.create(FirebaseTokenApi::class.java)
            storeService.uploadToken(token).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful){
                        val res = response.body()
                        Log.d(TAG, "onResponse: $res")
                    } else {
                        Log.d(TAG, "onResponse: Error Code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, t.message ?: "토큰 정보 등록 중 통신오류")
                }
            })
        }
    }
}