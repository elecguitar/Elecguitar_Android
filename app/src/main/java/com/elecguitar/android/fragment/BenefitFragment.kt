package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.adapter.ArticleRecyclerViewAdapter
import com.elecguitar.android.adapter.BenefitViewPagerAdapter
import com.elecguitar.android.adapter.CarAdapter
import com.elecguitar.android.databinding.FragmentBenefitBinding
import com.elecguitar.android.dto.Car
import com.elecguitar.android.response.ArticleResponse
import com.elecguitar.android.service.ArticleListService
import com.elecguitar.android.service.CarListService
import com.elecguitar.android.util.RetrofitCallback
import com.elecguitar.android.viewmodel.MainViewModel

private const val TAG = "BenefitFragment_싸피"
class BenefitFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentBenefitBinding
    private lateinit var articleAdapter: ArticleRecyclerViewAdapter
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sliderImageHandler: Handler = Handler()
    private val sliderImageRunnable = Runnable { binding.viewpagerBenefit.currentItem = binding.viewpagerBenefit.currentItem + 1 }
    private val imageList = arrayListOf<Int>().apply {
        add(R.drawable.banner1)
        add(R.drawable.banner2)
        add(R.drawable.banner3)
        add(R.drawable.banner4)
        add(R.drawable.banner5)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBenefitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        ArticleListService().getAllArticleList(GetAllArticleCallback())
    }

    private fun initAdapter(){
        binding.apply{
            viewpagerBenefit.apply {
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
    }

    override fun onResume() {
        super.onResume()
        sliderImageHandler.postDelayed(sliderImageRunnable, 5000)
    }

    override fun onPause() {
        super.onPause()
        sliderImageHandler.removeCallbacks(sliderImageRunnable)
    }

    inner class GetAllArticleCallback: RetrofitCallback<List<ArticleResponse>> {
        override fun onSuccess(code: Int, responseData: List<ArticleResponse>) {
            responseData.let {
                mainViewModel.articleList.addAll(responseData)
                articleAdapter = ArticleRecyclerViewAdapter(mainActivity, mainViewModel.articleList.value!!)

                Log.d(TAG, "onSuccess: ${mainViewModel.articleList.value}")
                mainViewModel.articleList.observe(viewLifecycleOwner) {
                    articleAdapter.articleList = it
                    articleAdapter.notifyDataSetChanged()
                }

                articleAdapter.onItemClickListener = object : ArticleRecyclerViewAdapter.OnItemClickListener {
                    override fun onClick(view: View, position: Int) {
                        // TODO : 상세 화면으로 이동
                    }
                }
            }

            binding.rvArticle.apply {
                layoutManager = GridLayoutManager(mainActivity, 1)
                adapter = articleAdapter
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