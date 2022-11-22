package com.elecguitar.android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elecguitar.android.databinding.RecyclerArticleItemBinding
import com.elecguitar.android.response.ArticleResponse

private const val TAG = "ArticleRecyclerViewAdap_싸피"
class ArticleRecyclerViewAdapter(
    private val context: Context,
    var articleList: MutableList<ArticleResponse>
): RecyclerView.Adapter<ArticleRecyclerViewAdapter.ArticleViewHolder>() {

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    lateinit var onItemClickListener : OnItemClickListener

    private val scale = context.resources.displayMetrics.density
    val layoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(convertDpToPixels(8),convertDpToPixels(100),convertDpToPixels(8),convertDpToPixels(100))
    }

    inner class ArticleViewHolder(val binding: RecyclerArticleItemBinding): RecyclerView.ViewHolder(binding.root){

        fun onBind(article: ArticleResponse, position: Int){
            binding.apply{
                tvArticleTitle.text = article.title
                Glide.with(context)
                    .load(article.img)
                    .into(imgArticle)
                tvArticleWriter.text = article.writer
                if(position == articleList.size - 1){
                    Log.d(TAG, "onBind: margin is success")
                    imgArticle.layoutParams = layoutParams
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(RecyclerArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.apply{
            onBind(articleList[position], position)
            itemView.setOnClickListener {
                onItemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int = articleList.size

    private fun convertDpToPixels(dp: Int): Int{
        return (dp * scale + 0.5f).toInt()
    }
}