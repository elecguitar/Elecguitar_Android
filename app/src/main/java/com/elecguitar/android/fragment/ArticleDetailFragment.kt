package com.elecguitar.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.elecguitar.android.R
import com.elecguitar.android.databinding.FragmentArticleDetailBinding
import com.elecguitar.android.databinding.FragmentChargeStationBottomBinding
import com.elecguitar.android.viewmodel.MainViewModel


class ArticleDetailFragment : Fragment() {
    private lateinit var binding: FragmentArticleDetailBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()

    }

    private fun initUi(){
        binding.apply {
            tvArticleTitle.text = mainViewModel.articleDetail!!.title
            tvArticleWriter.text = mainViewModel.articleDetail!!.writer
            tvArticleDate.text = mainViewModel.articleDetail!!.time
            tvArticleContent.text = mainViewModel.articleDetail!!.content
            Glide.with(requireContext())
                .load(mainViewModel.articleDetail!!.img)
                .into(imgArticle)
        }
    }

    companion object {
        const val TAG = "ArticleDetailFragment"
        fun newInstance(): ArticleDetailFragment{
            return ArticleDetailFragment()
        }
    }
}