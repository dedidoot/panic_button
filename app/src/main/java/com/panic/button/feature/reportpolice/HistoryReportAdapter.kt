package com.panic.button.feature.reportpolice

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.panic.button.R
import com.panic.button.core.api.response.HistoryModel
import com.panic.button.core.base.*
import com.panic.button.core.base.view.ItemUploadView
import com.panic.button.core.model.MediaModel
import kotlinx.android.synthetic.main.item_list_reports.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryReportAdapter(private val currentContext: Context, data: ArrayList<HistoryModel> = arrayListOf(), private val onNext : () -> Unit) :
    RecyclerAdapter<HistoryModel, HistoryReportAdapter.ReportItem>(currentContext, data) {

    override fun loadMore() {
        needToLoadMore = false
        onNext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportItem {
        return ReportItem(currentContext.inflate(R.layout.item_list_reports, parent))
    }

    inner class ReportItem(private var view: View) : BaseViewHolder(view) {
        override fun bind(item: HistoryModel) {
            GlobalScope.launch {
                (currentContext as Activity).runOnUiThread {
                    view.filesView?.removeAllViews()
                    item.images?.forEach {
                        view.filesView?.addView(getMediaView(it))
                    }

                    view.followUpsView?.isVisible = !item.follow_ups.isNullOrEmpty()
                    view.filesFollowUpView?.removeAllViews()
                    item.follow_ups?.forEach {
                        view.filesFollowUpView?.addView(getMediaView(it.images?.firstOrNull(),  it.content))
                    }
                }
            }

            view.titleTextView?.text = "${item.title}"
            view.timeTextView?.text = "${item.created_at}"
            if (item.follow_ups.isNullOrEmpty()) {
                view.descTextView?.text = "${item.report}\n"
            }else {
                view.descTextView?.text = "${item.report}"
            }
        }

        private fun getMediaView(it: MediaModel?, content : String?=null): ItemUploadView {
            val mediaView = ItemUploadView(currentContext)
            val tmpUrl = it?.url?.replace("//", "http://") ?: ""
            if (it?.isPdf() == true) {
                mediaView.setIcon(R.drawable.ic_pdf)
                mediaView.setBackgroundCard(ContextCompat.getColor(currentContext, R.color.grey_2))
            } else if (it?.isVideo() == true) {
                mediaView.setIcon(R.drawable.ic_play_red)
                mediaView.setBackgroundCard(ContextCompat.getColor(currentContext, R.color.grey_2))
            } else {
                mediaView.setGoneAdd_1Image()
                mediaView.setImageUrl(tmpUrl)
            }
            mediaView.setItemViewEventListener {
                if (!content.isNullOrBlank()) {
                    BaseDialogView(currentContext)
                        .setTitle("Deskripsi")
                        .setMessage(content)
                        .setPositiveString("Buka File")
                        .setNegativeString("Nanti")
                        .showNegativeButton()
                        .setOnClickNegative {  }
                        .setOnClickPositive {
                            currentContext.openBrowser(tmpUrl)
                        }.show()
                } else {
                    currentContext.openBrowser(tmpUrl)
                }
            }
            return mediaView
        }
    }
}