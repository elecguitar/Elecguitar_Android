package com.elecguitar.android.fragment

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.elecguitar.android.R
import com.elecguitar.android.activity.MainActivity
import com.elecguitar.android.databinding.FragmentArticleDetailBinding
import com.elecguitar.android.viewmodel.MainViewModel
import java.util.*


private const val TAG = "ArticleDetailFragment"
class ArticleDetailFragment : Fragment() {
    private lateinit var binding: FragmentArticleDetailBinding
    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private var isPlay: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
        mainViewModel.tts = TextToSpeech(requireContext(), OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                mainViewModel.tts!!.language = Locale.KOREA
            }
        })
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

    override fun onStop() {
        super.onStop()
        if (mainViewModel.tts != null) {
            // 음성 출력을 중단하고 대기 Queue 의 데이터를 비운다.
            mainViewModel.tts!!.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.tts!!.shutdown()
    }

    private fun initUi(){
        binding.apply {
            (requireContext() as MainActivity).apply{
                setSupportActionBar(topAppBar)
                supportActionBar!!.setDisplayShowCustomEnabled(true)
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_left_arrow)
                setHasOptionsMenu(true)
            }
            tvArticleTitle.text = mainViewModel.articleDetail!!.title
            tvArticleWriter.text = mainViewModel.articleDetail!!.writer
            tvArticleDate.text = mainViewModel.articleDetail!!.time
            tvArticleContent.text = mainViewModel.articleDetail!!.content
            Glide.with(requireContext())
                .load(mainViewModel.articleDetail!!.img)
                .into(imgArticle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_detail_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return  when(item.itemId){
            android.R.id.home -> {
                mainActivity.openFragment(3, "", 0)
                true
            }
            R.id.tts -> {
                if(isPlay){
                    isPlay = false
                    mainViewModel.tts!!.stop()
                }else{
                    isPlay = true
                    mainViewModel.tts!!.speak("${binding.tvArticleTitle.text}\n ${binding.tvArticleContent.text}", TextToSpeech.QUEUE_FLUSH, null, null) //from min 21
                }
                true
            }
            else -> false
        }

    }

    companion object {
        const val TAG = "ArticleDetailFragment"
        fun newInstance(): ArticleDetailFragment{
            return ArticleDetailFragment()
        }
    }
}