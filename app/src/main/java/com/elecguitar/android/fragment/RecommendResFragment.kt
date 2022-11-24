package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentRecommendResBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.service.CarListService
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.viewmodel.MainViewModel
import com.google.android.gms.common.internal.FallbackServiceBroker

private const val TAG = "RecommendResFragment_싸피"
class RecommendResFragment : Fragment() {
    private lateinit var binding: FragmentRecommendResBinding
    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()

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
        binding = FragmentRecommendResBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        CarListService().getAllCarList(GetAllCarCallback())

    }

    private fun initListener(){
        binding.apply{
            btnRcResBack.setOnClickListener {
                mainActivity.openFragment(5,"",0)
            }

            btnRcResMain.setOnClickListener {
                mainActivity.openFragment(mainViewModel.prevFragmentPos, "", 0)
                mainViewModel.isUpload = false
                mainViewModel.isStartRecommend = true
            }
        }
    }

    inner class GetAllCarCallback: RetrofitCallback<List<Car>> {
        override fun onSuccess(code: Int, responseData: List<Car>) {
            responseData.forEach{
                Log.d(TAG, "onSuccess: ${it.carName}")
                Log.d(TAG, "onSuccess: ${mainViewModel.recommendRes}")
                if(it.carName == mainViewModel.recommendRes){
                    binding.apply{
                        Glide.with(requireContext())
                            .load(it.img)
                            .into(tvRcResImg)
                        tvRcRes.text = mainViewModel.recommendRes
                    }
                    return@forEach
                }
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "차 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

}