package com.panic.button.feature.reportpolice

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.databinding.ActivityHistoryReportBinding
import kotlinx.android.synthetic.main.activity_history_report.*

class HistoryReportActivity : MvvmActivity<ActivityHistoryReportBinding, HistoryReportViewModel>(HistoryReportViewModel::class) {

    private var adapter: HistoryReportAdapter? = null

    override val layoutResource: Int = R.layout.activity_history_report

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        adapter = HistoryReportAdapter(this, arrayListOf()) {
            viewModel.getReportHistory()
        }

        viewModel.historyResponse.observe(this, Observer {
            it?.apply {
                if (viewModel.isFirstPage()) {
                    adapter?.clear()
                }
                adapter?.needToLoadMore = result?.size ?: 0 > 5
                adapter?.addItems(result ?: arrayListOf())
                viewModel.updatePage()
            }
        })
        viewModel.getReportHistory()
        reportRecyclerView.adapter = adapter
    }

    companion object {
        fun onNewIntent(context: Context): Intent {
            return Intent(context, HistoryReportActivity::class.java)
        }
    }
}