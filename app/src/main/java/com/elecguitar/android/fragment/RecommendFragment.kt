package com.elecguitar.android.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentRecommendBinding
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.charset.StandardCharsets

private const val TAG = "RecommendFragment_싸피"
class RecommendFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentRecommendBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private var imgUrl: Uri? = null
    private lateinit var mHandler: Handler
    private lateinit var socket: Socket
    private lateinit var byteArray: ByteArrayOutputStream
    private lateinit var bytes: ByteArray
    private lateinit var dos: DataOutputStream
    private lateinit var dis: DataInputStream
    private var rotatedBitmap: Bitmap? = null
    private val ip = ApplicationClass.LOCAL_IP
    private val port = 3000

    private var galleryLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()) {
        try {
            imgUrl = it.data!!.data!!
            Glide.with(requireContext())
                .load(imgUrl)
                .into(binding.tvRcImg)
            try{
                rotatedBitmap = MediaStore.Images.Media.getBitmap(mainActivity.contentResolver, imgUrl)
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

            }

            btnRcCancel.setOnClickListener {
                mainViewModel.isStartRecommend = true
                mainActivity.openFragment(mainViewModel.prevFragmentPos, "", 0)
            }
        }
    }


}