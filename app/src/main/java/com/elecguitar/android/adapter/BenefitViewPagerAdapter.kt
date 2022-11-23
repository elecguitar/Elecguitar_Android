package com.elecguitar.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elecguitar.android.databinding.ViewpagerBenefitItemBinding

class BenefitViewPagerAdapter(
    private val sliderItems: MutableList<Int>,
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<BenefitViewPagerAdapter.BenefitViewPagerViewHolder>(){

    inner class BenefitViewPagerViewHolder(binding: ViewpagerBenefitItemBinding): RecyclerView.ViewHolder(binding.root){
        private val imageView = binding.slideImageview

        fun onBind(image: Int){
            imageView.setImageResource(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitViewPagerViewHolder {
        return BenefitViewPagerViewHolder(ViewpagerBenefitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BenefitViewPagerViewHolder, position: Int) {
        holder.onBind(sliderItems[position])
        if(position == sliderItems.size - 1){
            viewPager.post(runnable)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    private val runnable = Runnable { sliderItems.addAll(sliderItems) }
}