package com.elecguitar.android.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.elecguitar.android.ApplicationClass
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentRecommendBinding
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import okio.Utf8
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

private const val TAG = "RecommendFragment_싸피"
class RecommendFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentRecommendBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private var imgUri: Uri? = null
    private lateinit var mHandler: Handler
    private lateinit var socket: Socket
    private lateinit var byteArray: ByteArrayOutputStream
    private lateinit var bytes: ByteArray
    private lateinit var dos: DataOutputStream
    private lateinit var dis: DataInputStream
    private lateinit var dialog: Dialog
    private var rotatedBitmap: Bitmap? = null
    private val ip = ApplicationClass.LOCAL_IP
    private val port = 3000

    private var galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        try {
            imgUri = it.data!!.data!!
            Glide.with(requireContext())
                .load(imgUri)
                .into(binding.tvRcImg)
            try{
                rotatedBitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, imgUri)
            }catch(e: Exception){
                e.printStackTrace()
            }
            mainViewModel.isUpload = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity.hideBottomNav(true)
        binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = LoadingDialog(requireContext())
        initListener()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(true)
    }

    private fun initListener(){
        binding.apply {
            cvRc.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                galleryLauncher.launch(intent)
            }

            btnRcStart.setOnClickListener {
                Log.d(TAG, "initListener: click start")
                if(rotatedBitmap != null && mainViewModel.isUpload){
                    Log.d(TAG, "initListener: bitmap create success")
                    dialog.show()
                    connect()
                }else{
                    Snackbar.make(it, "사진을 업로드 해주세요.", Snackbar.LENGTH_SHORT).show()
                }
            }

            btnRcCancel.setOnClickListener {
                mainViewModel.isStartRecommend = true
                mainActivity.openFragment(mainViewModel.prevFragmentPos, "", 0)
            }
        }
    }


    private fun connect() {
        mHandler = Handler()
        Log.w(TAG, "연결 하는중")
        // 받아오는거
        val checkUpdate: Thread = object : Thread() {
            override fun run() {
                byteArray = ByteArrayOutputStream()
                rotatedBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, byteArray)
                bytes = byteArray.toByteArray()
                Log.d(TAG, "run: ${bytes}")
                // 서버 접속
                try {
                    socket = Socket(ip, port)
                    Log.d(TAG, "서버 접속됨")
                    try {
                        dos = DataOutputStream(socket.getOutputStream()) // output에 보낼꺼 넣음
                        dis = DataInputStream(socket.getInputStream()) // input에 받을꺼 넣어짐

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.w(TAG, "버퍼생성 잘못됨")
                        dialog.dismiss()
                    }
                } catch (e1: IOException) {
                    Log.d(TAG, "서버접속못함")
                    dialog.dismiss()
                    e1.printStackTrace()
                }
                Log.w(TAG, "안드로이드에서 서버로 연결요청")
                try{
                    dos.writeUTF(bytes.size.toString())
                    dos.flush()

                    dos.write(bytes)
                    dos.flush()

                    var newData = ByteArray(4)
                    dis.read(newData,0,4)

                    val b2 = ByteBuffer.wrap(newData)
                    b2.order(ByteOrder.LITTLE_ENDIAN)

                    val ilen = b2.int
                    newData = ByteArray(ilen)
                    dis.read(newData,0,ilen)

                    val msg = String(newData, Charsets.UTF_8)
                    Log.d(TAG, "run: ${msg}")
                    mainViewModel.recommendRes = msg
                    dialog.dismiss()
                    mainActivity.openFragment(6,"",0)

                } catch(e: Exception){
                    dialog.dismiss()
                    Log.w(TAG, "error occur")
                }

            }
        }
        // 소켓 접속 시도, 버퍼생성
        checkUpdate.start()
    }

    inner class LoadingDialog(context: Context): Dialog(context){
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.waiting_dialog)

            setCancelable(false)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }


}