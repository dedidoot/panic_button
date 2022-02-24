package com.panic.button.feature.lostletter

import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.log
import com.panic.button.core.api.response.FieldModel
import com.panic.button.core.base.*
import com.panic.button.databinding.FragmentFieldBinding
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.android.synthetic.main.fragment_field.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FieldFragment : MvvmFragment<FragmentFieldBinding, LostLetterViewModel>(LostLetterViewModel::class) {

    private lateinit var mContext : Context
    private var currentShowGalleryIndex = 0

    override val layoutResource: Int = R.layout.fragment_field

    override val bindingVariable: Int = BR.viewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        viewModel.isNextPage.observe(this, Observer {
            if (it == true) {
                lostLetterView.removeAllViews()
            }
        })

        viewModel.fileId.observe(this, Observer {
            it?.apply {
                val view = getCurrentItemLostLetterView()
                view.itemsUploadView.lastOrNull()?.fileId = this
            }
        })

        viewModel.isSuccess.observe(this, Observer {
            if (it == true) {
                BaseDialogView(mContext)
                    .setMessage("Data berhasil dikirim, terima kasih atas kepercayaan Anda!")
                    .setPositiveString("Oke")
                    .setOnClickPositive {
                        (mContext as Activity).finish()
                    }
                    .show()
            }
        })

        viewModel.errorsMessage.observe(this, Observer {
            it?.apply {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
    }

    override fun viewLoaded() {
        binding.fragment = this
        viewModel.lostLetterFieldResponse.observe(this, Observer {
            it?.apply {
                setupLostLetterTypeView(this.data)
            }
        })
        additionalEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    private fun setupLostLetterTypeView(types: ArrayList<FieldModel>?) {
        lostLetterView.removeAllViews()
        GlobalScope.launch(Dispatchers.Main) {
            types?.forEachIndexed { index, it ->
                val view = ItemLostLetterView(mContext)
                view.setViewSlug("${it.slug}")
                view.setNameTypeTxt(it.title)
                view.setAddUploadEventListener(index) {
                    currentShowGalleryIndex = index
                    GalleryHelper(mContext).showGallery()
                }
                lostLetterView.addView(view)
            }
        }
    }

    fun onCreateLostLetter() {
        val params =  HashMap<String, Any>()

        for (index in 0 until lostLetterView.childCount) {
            val view = lostLetterView.getChildAt(index) as ItemLostLetterView
            val filesId = ArrayList<String>()
            view.itemsUploadView.forEach {
                it.fileId?.apply {
                    filesId.add(this)
                }
                log("data image uploaded ${it.fileId}")
            }

            filesId.takeIf { it.isNotEmpty() }?.apply {
                params["proof_${view.slug}"] = this
            }
        }
        viewModel.sendLostLetter(params)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GalleryHelper.ARG_GALLERY) {
            data?.data?.let {
                checkUploadImage(it)
            } ?: kotlin.run {
                showAlert(mContext, "Gambar tidak ditemukan")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkUploadImage(it: Uri) {
        try {
            val file = FileUtil.from(mContext, it)
            viewModel.isLoading.value = true
            GlobalScope.launch {
                val compressedImageFile = Compressor.compress(requireContext(), file) {
                    quality(
                        RemoteConfigHelper.getLong(RemoteConfigKey.QUALITY_IMAGE_COMPRESS).toInt()
                    )
                }
                delay(1000)

                (mContext as Activity).runOnUiThread {
                    val view = getCurrentItemLostLetterView()
                    view.setFile(compressedImageFile)
                    viewModel.uploadImage(compressedImageFile, view.slug)
                }
            }
        } catch (e : Exception) {
            viewModel.isLoading.value = false
            showAlert(requireContext(), "Gambar tidak ditemukan, mohon upload gambar yang benar!")
        }
    }

    private fun getCurrentItemLostLetterView(): ItemLostLetterView {
        return lostLetterView.getChildAt(currentShowGalleryIndex) as ItemLostLetterView
    }

    fun onLostDate() {
        context?.apply {
            hideKeyboard(this)
            val calendar = DialogCalender(this)
            calendar.setupDialog({
                viewModel.lostDate.value = it
            }, DialogCalender.year_day_month2)
            calendar.showDialog()
        }
    }

    fun onLostTime() {
        val calendar: Calendar = Calendar.getInstance()
        val timeSetListener =
            OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
                val time = simpleDateFormat.format(calendar.time)
                viewModel.lostTime.value = time
            }

        TimePickerDialog(mContext, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }
}