package com.panic.button.feature.police

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.KeyListener
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.base.*
import com.panic.button.databinding.ActivityCovidOfficerBinding
import kotlinx.android.synthetic.main.activity_covid_officer.*

class CovidOfficerActivity : MvvmActivity<ActivityCovidOfficerBinding, PoliceViewModel>(PoliceViewModel::class) {

    private lateinit var provincePopup: PopupWindow
    private lateinit var cityPopup: PopupWindow
    private lateinit var subDistrictPopup: PopupWindow
    private lateinit var villagePopup: PopupWindow
    private lateinit var hamletPopup: PopupWindow
    private lateinit var textKeyListener: KeyListener
    private lateinit var phoneKeyListener: KeyListener

    override val layoutResource: Int = R.layout.activity_covid_officer

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        setupKeyListener()
        setupPopup()
        registerObserver()
        viewModel.getProvince()
        nikEditTextChangedListener()
    }

    private fun nikEditTextChangedListener() {
        nikEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                messageTextView.isVisible = false
                formView.isVisible = false
            }
        })
    }

    private fun setupKeyListener() {
        textKeyListener = EditText(this).keyListener
        val phoneEditText = EditText(this)
        phoneEditText.inputType = InputType.TYPE_CLASS_PHONE
        phoneKeyListener = phoneEditText.keyListener
    }

    private fun registerObserver() {
        viewModel.isSuccessLogout.observe(this, Observer {
            it?.takeIf { it }?.apply {
                BaseApplication.baseApplication?.logout()
                finish()
            }
        })
        viewModel.isSuccessCovidRegister.observe(this, Observer {
            it?.takeIf { it }?.apply {
                successRegisterCovidDialog()
            }
        })

        viewModel.checkCovidResponse.observe(this, Observer { response ->
            response?.apply {
                setupCheckCovidView()
            }
        })

        viewModel.provinceResponse.observe(this, Observer { response ->
            provincePopup.clearAll()
            provincePopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.province.value) == true || it.name?.contains("dki jakarta", ignoreCase = true) == true) {
                    viewModel.province.value = it.id
                    provinceTextView.text = it.name
                    viewModel.getCity(it.id)
                }
            }
        })
        viewModel.cityResponse.observe(this, Observer { response ->
            cityPopup.clearAll()
            cityPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.city.value) == true || it.name?.contains("jakarta timur", ignoreCase = true) == true) {
                    viewModel.city.value = it.id
                    cityTextView.text = it.name
                    viewModel.getSubDistrict(it.id)
                }
            }
        })
        viewModel.subDistrictResponse.observe(this, Observer { response ->
            subDistrictPopup.clearAll()
            subDistrictPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.subDistrictId.value) == true) {
                    subDistrictTextView.text = it.name
                    viewModel.getVillage(it.id)
                }
            }
        })
        viewModel.villageResponse.observe(this, Observer { response ->
            villagePopup.clearAll()
            villagePopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.villageId.value) == true) {
                    villageTextView.text = it.name
                    viewModel.getHamlet(it.id)
                }
            }
        })
        viewModel.hamletResponse.observe(this, Observer { response ->
            hamletPopup.clearAll()
            hamletPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.hamlet_id.value) == true) {
                    hamletTextView.text = it.name
                }
            }
        })
        viewModel.errorsMessage.observe(this, Observer {
            if (!it.isNullOrBlank()) {
                makeHandler(500) {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        })
    }

    private fun successRegisterCovidDialog() {
        BaseDialogView(this).setPositiveString("Oke").setMessage("Data covid berhasil disimpan, terimakasih!").setOnClickPositive {
            startActivity(onNewIntent(this))
            finish()
        }.show()
    }

    private fun setupCheckCovidView() {
        formView.isVisible = true

        val isCovidFound = viewModel.checkCovidResponse.value?.data != null
        messageTextView.isVisible = isCovidFound.not()
        registerButton.isVisible = isCovidFound.not()
        titleSubDistrictTextView.isVisible = isCovidFound.not()
        subDistrictTextView.isVisible = isCovidFound.not()
        titleVillageTextView.isVisible = isCovidFound.not()
        villageTextView.isVisible = isCovidFound.not()
        titleHamletTextView.isVisible = isCovidFound.not()
        hamletTextView.isVisible = isCovidFound.not()

        statusTextView.isVisible = isCovidFound
        statusEditText.isVisible = isCovidFound

        if (isCovidFound) {
            nameEditText.keyListener = null
            phoneEditText.keyListener = null
            addressEditText.keyListener = null
        } else {
            nameEditText.keyListener = textKeyListener
            phoneEditText.keyListener = phoneKeyListener
            addressEditText.keyListener = textKeyListener
        }
    }

    fun onSettingPolice(view: View) {
        if (menuView.visibility == View.VISIBLE) {
            hideMenu()
        } else {
            menuView.visibility = View.VISIBLE
            inputView.visibility = View.GONE
        }
    }

    fun onCancel(view: View) {
        hideMenu()
    }

    fun onMenu(view: View) {
        hideMenu()
    }

    private fun hideMenu() {
        menuView.visibility = View.GONE
        inputView.visibility = View.VISIBLE
    }

    fun onLogout(view: View) {
        viewModel.postLogout()
    }

    fun onCheckNik() {
        hideKeyboard(this)
        viewModel.getCheckNik()
    }

    fun onRegister() {
        hideKeyboard(this)
        viewModel.postCovidRegister()
    }

    private fun setupPopup() {
        setupProvincePopup()
        setupCityPopup()
        setupSubDistrictPopup()
        setupVillagePopup()
        setupHamletPopup()
    }

    private fun setupProvincePopup() {
        provincePopup = PopupWindow(this)
        provincePopup.setEventListener {
            viewModel.province.value = it.id
            provinceTextView.text = it.name
            viewModel.getCity(it.id)

            cityTextView.text = ""
            viewModel.city.value = ""

            subDistrictTextView.text = ""
            viewModel.subDistrictId.value= ""

            villageTextView.text = ""
            viewModel.villageId.value = ""

            hamletTextView.text =  ""
            viewModel.hamlet_id.value = ""
        }
    }

    private fun setupCityPopup() {
        cityPopup = PopupWindow(this)
        cityPopup.setEventListener {
            viewModel.city.value = it.id
            cityTextView.text = it.name
            viewModel.getSubDistrict(it.id)

            subDistrictTextView.text = ""
            viewModel.subDistrictId.value = ""

            villageTextView.text = ""
            viewModel.villageId.value = ""

            hamletTextView.text =  ""
            viewModel.hamlet_id.value = ""
        }
    }

    private fun setupSubDistrictPopup() {
        subDistrictPopup = PopupWindow(this)
        subDistrictPopup.setEventListener {
            viewModel.subDistrictId.value= it.id
            subDistrictTextView.text = it.name
            viewModel.getVillage(it.id)

            villageTextView.text = ""
            viewModel.villageId.value = ""

            hamletTextView.text =  ""
            viewModel.hamlet_id.value = ""
        }
    }

    private fun setupVillagePopup() {
        villagePopup = PopupWindow(this)
        villagePopup.setEventListener {
            viewModel.villageId.value = it.id
            villageTextView.text = it.name
            viewModel.getHamlet(it.id)

            hamletTextView.text =  ""
            viewModel.hamlet_id.value = ""
        }
    }

    private fun setupHamletPopup() {
        hamletPopup = PopupWindow(this)
        hamletPopup.setEventListener {
            viewModel.hamlet_id.value = it.id
            hamletTextView.text = it.name
        }
    }

    fun onProvince() {
        hideKeyboard(this)
        provincePopup.showPopup(provinceTextView)
    }

    fun onVillageOffice() {
        hideKeyboard(this)
        villagePopup.showPopup(villageTextView)
    }

    fun onHamlet() {
        hideKeyboard(this)
        hamletPopup.showPopup(hamletTextView)
    }

    fun onCity() {
        hideKeyboard(this)
        cityPopup.showPopup(cityTextView)
    }

    fun onSubDistrict() {
        hideKeyboard(this)
        subDistrictPopup.showPopup(subDistrictTextView)
    }

    companion object {

        fun onNewIntent(context: Context): Intent {
            return Intent(context, CovidOfficerActivity::class.java)
        }
    }
}