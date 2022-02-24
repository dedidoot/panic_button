package com.panic.button.feature.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.panic.button.R
import com.panic.button.core.api.response.NewsItemModel
import com.panic.button.core.base.isVisible
import com.panic.button.core.base.loadUrlWithPlaceHolderJaktim
import com.panic.button.core.base.replaceUrlJaktim
import com.panic.button.feature.news.NewsActivity
import kotlinx.android.synthetic.main.item_news.view.*

class Pager(val context: Context) : PagerAdapter() {

    private var newItems: ArrayList<NewsItemModel> = arrayListOf()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_news, container, false)
        itemView?.titleTextView?.isVisible = newItems[position].isShowImageView
        itemView?.timeTextView?.isVisible = newItems[position].isShowImageView
        itemView?.descTextView?.isVisible = newItems[position].isShowImageView
        itemView?.readMoreTextView?.isVisible = newItems[position].isShowImageView
        itemView?.headerImageView?.loadUrlWithPlaceHolderJaktim(replaceUrlJaktim(newItems[position].cover?.url))
        itemView?.titleTextView?.text = newItems.getOrNull(position)?.post_title ?: ""
        itemView?.timeTextView?.text = newItems.getOrNull(position)?.category?.category_name ?: ""
        itemView?.descTextView?.text = newItems.getOrNull(position)?.post_content ?: ""
        itemView?.readMoreTextView?.setOnClickListener {
            context.startActivity(NewsActivity.onNewIntent(context))
        }
        container.addView(itemView)
        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    fun setItems(newItems: ArrayList<NewsItemModel>) {
        this.newItems = newItems
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return newItems.size
    }
}