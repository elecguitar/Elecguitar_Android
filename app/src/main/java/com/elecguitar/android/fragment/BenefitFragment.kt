package com.elecguitar.android.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elecguitar.android.R
import com.elecguitar.android.adapter.BenefitViewPagerAdapter
import com.elecguitar.android.databinding.FragmentBenefitBinding

class BenefitFragment : Fragment() {

    private lateinit var binding: FragmentBenefitBinding

    private val sliderImageHandler: Handler = Handler()
    private val sliderImageRunnable = Runnable { binding.viewpagerBenefit.currentItem = binding.viewpagerBenefit.currentItem + 1 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBenefitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = arrayListOf<Int>().apply {
            add(R.drawable.banner1)
            add(R.drawable.banner2)
            add(R.drawable.banner3)
            add(R.drawable.banner4)
            add(R.drawable.banner5)
        }

        binding.viewpagerBenefit.apply {
            adapter = BenefitViewPagerAdapter(imageList, binding.viewpagerBenefit)
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderImageHandler.removeCallbacks(sliderImageRunnable)
                    sliderImageHandler.postDelayed(sliderImageRunnable, 5000)
                }
            })

        }
    }

    override fun onResume() {
        super.onResume()
        sliderImageHandler.postDelayed(sliderImageRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        sliderImageHandler.removeCallbacks(sliderImageRunnable)
    }
}