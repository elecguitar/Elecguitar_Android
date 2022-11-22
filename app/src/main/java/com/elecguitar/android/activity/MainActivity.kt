package com.elecguitar.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.elecguitar.android.R
import com.elecguitar.android.adapter.ViewPagerAdapter
import com.elecguitar.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewpager.apply {
            adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            registerOnPageChangeCallback(PageChangeCallback())
            isUserInputEnabled = false
            currentItem = 1
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            navigationSelected(it)
        }

        binding.fab.setOnClickListener {
            binding.viewpager.currentItem = 1
        }
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)

        when (checked.itemId) {
            R.id.mapFragment -> {
                binding.viewpager.currentItem = 0
                return true
            }
            R.id.benefitFragment -> {
                binding.viewpager.currentItem = 2
                return true
            }
        }
        return false
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.bottomNavigationView.selectedItemId = when (position) {
                0 -> R.id.mapFragment
                1 -> R.id.homeFragment
                2 -> R.id.benefitFragment
                else -> error("no such position: $position")
            }
        }
    }
}