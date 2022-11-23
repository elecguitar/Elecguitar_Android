package com.elecguitar.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.elecguitar.android.R
import com.elecguitar.android.databinding.ActivityMainBinding
import com.elecguitar.android.fragment.ArticleDetailFragment
import com.elecguitar.android.fragment.BenefitFragment
import com.elecguitar.android.fragment.HomeFragment
import com.elecguitar.android.fragment.MapFragment

class MainActivity : AppCompatActivity() {

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
            3-> transaction.replace(R.id.frameLayout, BenefitFragment())
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
}