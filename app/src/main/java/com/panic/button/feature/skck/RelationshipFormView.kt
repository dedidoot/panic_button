package com.panic.button.feature.skck

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.FrameLayout
import com.panic.button.R
import com.panic.button.core.base.PopupWindow
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.LabelValueModel
import kotlinx.android.synthetic.main.view_relationship_form.view.*

class RelationshipFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var nationalityPopup: PopupWindow? = null
    private var religionPopup: PopupWindow? = null
    private var nationalityPopupEventListener: ((GeneralModel) -> Unit)? = null
    private var religionPopupEventListener: ((GeneralModel) -> Unit)? = null

    init {
        inflate(context, R.layout.view_relationship_form, this)
        setupNationalityPopup()
        setupReligionPopup()
        nationalityRelationshipTextView.setOnClickListener {
            nationalityPopup?.showPopup(nationalityRelationshipTextView)
        }
        religionRelationshipTextView.setOnClickListener {
            religionPopup?.showPopup(religionRelationshipTextView)
        }
    }

    private fun getTextChangedListener(listener: ((String) -> Unit)?): TextWatcher? {
        return object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener?.let { it(s?.toString() ?: "") }
            }
        }
    }

    private fun setupNationalityPopup() {
        context?.apply {
            nationalityPopup = PopupWindow(this)
        }
        nationalityPopup?.addItems(
            arrayListOf(
                GeneralModel(id = "wni", name = "WNI"),
                GeneralModel(id = "wna", name = "WNA")
            )
        )
        nationalityPopup?.setEventListener {
            nationalityPopupEventListener?.let { it1 -> it1(it) }
            nationalityRelationshipTextView.text = it.name
        }
    }

    private fun setupReligionPopup() {
        context?.apply {
            religionPopup = PopupWindow(this)
        }
        religionPopup?.setEventListener {
            religionPopupEventListener?.let { it1 -> it1(it) }
            religionRelationshipTextView.text = it.name
        }
    }

    fun setNationalityPopupEventListener(eventListener: ((GeneralModel) -> Unit)? = null) {
        this.nationalityPopupEventListener = eventListener
    }

    fun setReligionPopupEventListener(eventListener: ((GeneralModel) -> Unit)? = null) {
        this.religionPopupEventListener = eventListener
    }

    fun setNameRelationshipTextChangedListener(eventListener: ((String) -> Unit)? = null) {
        nameRelationshipEditText.addTextChangedListener(getTextChangedListener(eventListener))
    }

    fun setYearBirthRelationshipTextChangedListener(eventListener: ((String) -> Unit)? = null) {
        yearBirthRelationshipEditText.addTextChangedListener(getTextChangedListener(eventListener))
    }

    fun setJobRelationshipTextChangedListener(eventListener: ((String) -> Unit)? = null) {
        jobRelationshipEditText.addTextChangedListener(getTextChangedListener(eventListener))
    }

    fun setAddressRelationshipTextChangedListener(eventListener: ((String) -> Unit)? = null) {
        addressRelationshipEditText.addTextChangedListener(getTextChangedListener(eventListener))
    }

    fun setReligionPopup(data: ArrayList<LabelValueModel>?) {
        val items = ArrayList<GeneralModel>()
        data?.forEach {
            items.add(GeneralModel(id = it.value, name = it.label))
        }
        religionPopup?.addItems(items)
    }

    var nationalityRelationship: String?
        get() = nationalityRelationshipTextView.text?.toString()
        set(value) {
            nationalityRelationshipTextView.text = value
        }

    var religionRelationship: String?
        get() = religionRelationshipTextView.text?.toString()
        set(value) {
            religionRelationshipTextView.text = value
        }

    var titleRelationship: String?
        get() = titleRelationshipTextView.text?.toString()
        set(value) {
            titleRelationshipTextView.text = value
        }

    var nameRelationship: String?
        get() = nameRelationshipEditText.text?.toString()
        set(value) {
            nameRelationshipEditText.setText(value)
        }

    var yearBirthRelationship: String?
        get() = yearBirthRelationshipEditText.text?.toString()
        set(value) {
            yearBirthRelationshipEditText.setText(value)
        }

    var jobRelationship: String?
        get() = jobRelationshipEditText.text?.toString()
        set(value) {
            jobRelationshipEditText.setText(value)
        }

    var addressRelationship: String?
        get() = addressRelationshipEditText.text?.toString()
        set(value) {
            addressRelationshipEditText.setText(value)
        }
}