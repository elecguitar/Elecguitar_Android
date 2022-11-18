package com.elecguitar.android.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.elecguitar.android.fragment.BenefitFragment
import com.elecguitar.android.fragment.HomeFragment
import com.elecguitar.android.fragment.MapFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MapFragment()
            1 -> HomeFragment()
            2 -> BenefitFragment()
            else -> HomeFragment()
        }
    }

}