package com.panic.button.core.base.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.*
import androidx.databinding.adapters.ListenerUtil
import androidx.databinding.adapters.TextViewBindingAdapter.AfterTextChanged
import com.panic.button.R
import com.panic.button.databinding.ViewTextFieldBinding

@InverseBindingMethods(
    value = [InverseBindingMethod(
        type = TestView::class,
        attribute = "bind:filterStringValue",
        method = "getFilterValue",
        event = "android:filterStringValuetAttrChanged"
    )]
)

class TestView : FrameLayout {

    private lateinit var mBinding: ViewTextFieldBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_text_field,
            this,
            true
        )
    }

    val filterValue: String
        get() = mBinding.textFieldInputEditText.text.toString()

    companion object {
        @BindingAdapter(
            value = ["bind:filterTitle", "bind:filterStringValue", "bind:filterDateValue"],
            requireAll = false
        )
        @JvmStatic
        fun setFilterBinding(positionView: TestView, filterTitle: String?, filterStringValue: String?, filterDateValue: Long?) {
            positionView.mBinding.textFieldInputEditText.setText(filterTitle)
            if (filterStringValue != null) positionView.mBinding.textFieldInputEditText.setText(
                filterStringValue
            )
            if (filterDateValue != null) positionView.mBinding.textFieldInputEditText.setText(
                "sippp"
            )
        }

        @BindingAdapter(
            value = ["android:afterTextChanged", "android:filterStringValuetAttrChanged"],
            requireAll = false
        )
        @JvmStatic
        fun setTextWatcher(filterPositionView: TestView, after: AfterTextChanged?, textAttrChanged: InverseBindingListener?) {
            val newValue: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    after?.afterTextChanged(s)
                    textAttrChanged?.onChange()
                }
            }
            val oldValue = ListenerUtil.trackListener(filterPositionView.mBinding.textFieldInputEditText, newValue, R.id.textWatcher)
            if (oldValue != null) {
                filterPositionView.mBinding.textFieldInputEditText.removeTextChangedListener(oldValue)
            }
            filterPositionView.mBinding.textFieldInputEditText.addTextChangedListener(newValue)
        }
    }
}