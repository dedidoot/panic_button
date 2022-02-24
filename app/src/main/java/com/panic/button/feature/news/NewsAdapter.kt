package com.panic.button.feature.news

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.panic.button.R
import com.panic.button.core.api.response.NewsItemModel
import com.panic.button.core.base.RecyclerAdapter
import com.panic.button.core.base.inflate
import com.panic.button.core.base.loadUrlWithPlaceHolderJaktim
import com.panic.button.core.base.replaceUrlJaktim
import kotlinx.android.synthetic.main.item_list_news.view.*

class NewsAdapter(private val currentContext: Context, data: ArrayList<NewsItemModel> = arrayListOf(), private val onNext : () -> Unit) :
    RecyclerAdapter<NewsItemModel, NewsAdapter.NewsItem>(currentContext, data) {

    override fun loadMore() {
        needToLoadMore = false
        onNext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItem {
        return NewsItem(currentContext.inflate(R.layout.item_list_news, parent))
    }

    inner class NewsItem(private var view: View) : BaseViewHolder(view) {

        override fun bind(item: NewsItemModel) {
            view.headerImageView?.loadUrlWithPlaceHolderJaktim(replaceUrlJaktim(item.cover?.url))
            view.titleTextView?.text = "${item.post_title}"
            view.timeTextView?.text = "${item.category?.category_name}"
            view.descTextView?.text = "${item.post_content}"
        }
    }
}