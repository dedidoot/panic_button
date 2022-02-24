package com.panic.button.feature.news

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.databinding.ActivityNewsBinding
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : MvvmActivity<ActivityNewsBinding, NewsViewModel>(NewsViewModel::class) {

    private var newsAdapter: NewsAdapter? = null

    override val layoutResource: Int = R.layout.activity_news

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        newsAdapter = NewsAdapter(this, viewModel.getNewsModel()) {
            viewModel.getNews()
        }

        viewModel.newsResponse.observe(this, Observer {
            it?.apply {
                if (viewModel.isFirstPage()) {
                    newsAdapter?.clear()
                }
                newsAdapter?.addItems(newsModel?.items ?: arrayListOf())
                viewModel.updatePage()
            }
        })
        viewModel.getNews()

        newsRecyclerView.adapter = newsAdapter
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, NewsActivity::class.java)
        }
    }
}