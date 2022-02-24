package com.panic.button.feature.registerprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.panic.button.BR
import com.panic.button.R
import com.panic.button.core.api.isVisible
import com.panic.button.core.api.response.ProfileResponse
import com.panic.button.core.base.*
import com.panic.button.core.base.location.RequestLocation
import com.panic.button.core.model.GeneralModel
import com.panic.button.core.model.RegisterModel
import com.panic.button.databinding.ActivityRegisterProfileBinding
import com.panic.button.feature.ktpupload.IdentityCardActivity
import kotlinx.android.synthetic.main.activity_register_profile.*
import java.util.*

class RegisterProfileActivity :
    MvvmActivity<ActivityRegisterProfileBinding, RegisterProfileViewModel>(RegisterProfileViewModel::class) {

    private lateinit var genderPopup: PopupWindow
    private lateinit var provincePopup: PopupWindow
    private lateinit var cityPopup: PopupWindow
    private lateinit var subDistrictPopup: PopupWindow
    private lateinit var villagePopup: PopupWindow
    private lateinit var hamletPopup: PopupWindow
    private lateinit var relationshipPopup: PopupWindow

    override val layoutResource: Int = R.layout.activity_register_profile

    override val bindingVariable: Int = BR.viewModel

    override fun viewLoaded() {
        binding.activity = this
        viewModel.processIntent(intent)
        setupPopup()
        RequestLocation(this)
        registerObserver()
        setupFromEdit()
    }

    private fun setupFromEdit() {
        if (viewModel.isFromEditProfile.value == true) {
            titleBar.text = "Pengaturan"
            tacImageView.isVisible = false
            line1.isVisible = false
            profileImageView.isVisible = false
            line2.isVisible = false
            ktpImageView.isVisible = false
            button.text = "Simpan"
            viewModel.getProfile()
            viewModel.profileResponse.observe(this, Observer {
                it?.apply {
                    bindView(this)
                    viewModel.getProvince()
                }
            })
        } else {
            viewModel.getProvince()
        }
    }

    private fun bindView(profileResponse: ProfileResponse) {
        val registerModel = RegisterModel()
        registerModel.agreement = 1
        registerModel.name = profileResponse.data?.name
        registerModel.birthPlace = profileResponse.data?.birthPlace
        registerModel.birthday = profileResponse.data?.birthday
        registerModel.gender = profileResponse.data?.gender

        registerModel.citizenCardId = profileResponse.data?.getRealCitizenCardId()
        //citizenCardIdEditText.isEnabled = false

        registerModel.phone = profileResponse.data?.phone
        registerModel.address = profileResponse.data?.address
        registerModel.houseNumber = profileResponse.data?.houseNumber

        registerModel.rt = profileResponse.data?.rt
        registerModel.hamlet_id = profileResponse.data?.hamlet?.id

        registerModel.postalCode = profileResponse.data?.postalCode
        registerModel.latitude = BaseApplication.sessionManager?.latitude?.toDoubleOrNull() ?: 0.0
        registerModel.longitude = BaseApplication.sessionManager?.longitude?.toDoubleOrNull() ?: 0.0
        registerModel.emergencyContactFullName = profileResponse.data?.emergencyContact?.full_name
        registerModel.emergencyContactRelationship = profileResponse.data?.emergencyContact?.relationship
        registerModel.emergencyContactPhoneNumber = profileResponse.data?.emergencyContact?.phone_number
        registerModel.email = profileResponse.data?.email
        emailEditText.isVisible = true
        emailTextView.isVisible = true

        profileResponse.data?.hamlet?.apply {
            registerModel.villageId = village?.id
            registerModel.city = village?.subDistrict?.city?.id
            registerModel.subDistrictId = village?.subDistrict?.id
            registerModel.province = village?.subDistrict?.city?.province?.id
        }

        registerModel.avatarId = profileResponse.data?.avatar?.id

        profileResponse.data?.citizen_card_image?.id.takeIf { !it.isNullOrBlank() }?.let {
            registerModel.citizenCardImageId = it
        } ?: kotlin.run {
            registerModel.citizenCardImageId = viewModel.registerModel.value?.citizenCardImageId
        }

        profileResponse.data?.citizen_card_selfie?.id.takeIf { !it.isNullOrBlank() }?.let {
            registerModel.citizenCardSelfieId = it
        } ?: kotlin.run {
            registerModel.citizenCardSelfieId = viewModel.registerModel.value?.citizenCardSelfieId
        }

        if (registerModel.citizenCardImageId.isNullOrBlank() || registerModel.citizenCardSelfieId.isNullOrBlank()) {
            startActivity(IdentityCardActivity.onNewIntent(this, isFromEditProfile = true))
            finish()
        }

        registerModel.avatarUrlSession = replaceUrlJaktim(profileResponse.data?.avatar?.url)

        viewModel.registerModel.value = registerModel
        viewModel.refreshRegisterModel()
    }

    private fun registerObserver() {
        viewModel.provinceResponse.observe(this, Observer { response ->
            provincePopup.clearAll()
            provincePopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.registerModel.value?.province) == true || it.name?.contains("dki jakarta", ignoreCase = true) == true) {
                    viewModel.registerModel.value?.province = it.id
                    provinceTextView.text = it.name
                    viewModel.getCity(it.id)
                }
            }
        })
        viewModel.cityResponse.observe(this, Observer { response ->
            cityPopup.clearAll()
            cityPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.registerModel.value?.city) == true || it.name?.contains("jakarta timur", ignoreCase = true) == true) {
                    viewModel.registerModel.value?.city = it.id
                    cityTextView.text = it.name
                    viewModel.getSubDistrict(it.id)
                }
            }
        })
        viewModel.subDistrictResponse.observe(this, Observer { response ->
            subDistrictPopup.clearAll()
            subDistrictPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.registerModel.value?.subDistrictId) == true) {
                    subDistrictTextView.text = it.name
                    viewModel.getVillage(it.id)
                }
            }
        })
        viewModel.villageResponse.observe(this, Observer { response ->
            villagePopup.clearAll()
            villagePopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.registerModel.value?.villageId) == true) {
                    villageTextView.text = it.name
                    viewModel.getHamlet(it.id)
                }
            }
        })
        viewModel.hamletResponse.observe(this, Observer { response ->
            hamletPopup.clearAll()
            hamletPopup.addItems(response)

            response.forEach {
                if (it.id?.equals(viewModel.registerModel.value?.hamlet_id) == true) {
                    hamletTextView.text = it.name
                }
            }
        })
        viewModel.isSuccess.observe(this, Observer {
            if (it == true) {
                startActivity(IdentityCardActivity.onNewIntent(this))
            }
        })
        viewModel.isEdited.observe(this, Observer {
            if (it == true) {
                showAlert(this, "Berhasil ubah data")
                finish()
            }
        })
        viewModel.urlUploadSuccess.observe(this, Observer { result ->
            if (!result.isNullOrEmpty()) {
                avatarImageView.loadGlide(result) {
                    if (it) {
                        loadingImage.isVisible = false
                        cameraImageView.isVisible = false
                    } else {
                        loadingImage.isVisible = false
                        cameraImageView.isVisible = true
                    }
                }
            } else {
                loadingImage.isVisible = false
                cameraImageView.isVisible =
                    viewModel.registerModel.value?.avatarUrlSession.isNullOrEmpty()
                showAlert(this, "Gagal upload gambar, coba lagi!")
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

    fun onBirthday() {
        hideKeyboard(this)
        val calendar = DialogCalender(this)
        calendar.setIsStartYear1980()
        calendar.setupDialog({
            viewModel.registerModel.value?.birthday = it
            viewModel.refreshRegisterModel()
        }, DialogCalender.year_day_month2)
        calendar.showDialog()
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

    fun onEmergencyContactRelationship() {
        hideKeyboard(this)
        relationshipPopup.showPopup(relationshipTextView)
    }

    fun onCity() {
        hideKeyboard(this)
        cityPopup.showPopup(cityTextView)
    }

    fun onSubDistrict() {
        hideKeyboard(this)
        subDistrictPopup.showPopup(subDistrictTextView)
    }

    fun onGender() {
        hideKeyboard(this)
        genderPopup.showPopup(genderTextView)
    }

    fun onContinue() {
        hideKeyboard(this)
        viewModel.registerModel.value?.citizenCardId = citizenCardIdEditText.text.toString()
        if (button.text.toString().equals("simpan", ignoreCase = true)) {
            viewModel.onSave(isEdit = true)
        } else {
            viewModel.onSave(isEdit = false)
        }
    }

    private fun setupPopup() {
        setupGenderPopup()
        setupRelationshipPopup()
        setupProvincePopup()
        setupCityPopup()
        setupSubDistrictPopup()
        setupVillagePopup()
        setupHamletPopup()
    }

    private fun setupProvincePopup() {
        provincePopup = PopupWindow(this)
        provincePopup.setEventListener {
            viewModel.registerModel.value?.province = it.id
            provinceTextView.text = it.name
            viewModel.getCity(it.id)

            cityTextView.text = ""
            viewModel.registerModel.value?.city = ""

            subDistrictTextView.text = ""
            viewModel.registerModel.value?.subDistrictId = ""

            villageTextView.text = ""
            viewModel.registerModel.value?.villageId = ""

            hamletTextView.text =  ""
            viewModel.registerModel.value?.hamlet_id = ""
        }
    }

    private fun setupCityPopup() {
        cityPopup = PopupWindow(this)
        cityPopup.setEventListener {
            viewModel.registerModel.value?.city = it.id
            cityTextView.text = it.name
            viewModel.getSubDistrict(it.id)

            subDistrictTextView.text = ""
            viewModel.registerModel.value?.subDistrictId = ""

            villageTextView.text = ""
            viewModel.registerModel.value?.villageId = ""

            hamletTextView.text =  ""
            viewModel.registerModel.value?.hamlet_id = ""
        }
    }

    private fun setupSubDistrictPopup() {
        subDistrictPopup = PopupWindow(this)
        subDistrictPopup.setEventListener {
            viewModel.registerModel.value?.subDistrictId = it.id
            subDistrictTextView.text = it.name
            viewModel.getVillage(it.id)

            villageTextView.text = ""
            viewModel.registerModel.value?.villageId = ""

            hamletTextView.text =  ""
            viewModel.registerModel.value?.hamlet_id = ""
        }
    }

    private fun setupVillagePopup() {
        villagePopup = PopupWindow(this)
        villagePopup.setEventListener {
            viewModel.registerModel.value?.villageId = it.id
            villageTextView.text = it.name
            viewModel.getHamlet(it.id)

            hamletTextView.text =  ""
            viewModel.registerModel.value?.hamlet_id = ""
        }
    }

    private fun setupHamletPopup() {
        hamletPopup = PopupWindow(this)
        hamletPopup.setEventListener {
            viewModel.registerModel.value?.hamlet_id = it.id
            hamletTextView.text = it.name
        }
    }

    private fun setupGenderPopup() {
        genderPopup = PopupWindow(this)
        genderPopup.addItems(
            arrayListOf(
                GeneralModel(name = "Laki-laki"),
                GeneralModel(name = "Perempuan")
            )
        )
        genderPopup.setEventListener {
            viewModel.registerModel.value?.gender = it.name?.toLowerCase(Locale.getDefault())
            viewModel.refreshRegisterModel()
        }
    }

    private fun setupRelationshipPopup() {
        relationshipPopup = PopupWindow(this)
        relationshipPopup.addItems(
            arrayListOf(
                GeneralModel(name = "Ayah"),
                GeneralModel(name = "Ibu"),
                GeneralModel(name = "Pasangan"),
                GeneralModel(name = "Kakak"),
                GeneralModel(name = "Adik"),
                GeneralModel(name = "Saudara"),
                GeneralModel(name = "Teman"),
                GeneralModel(name = "Lain-lain")
            )
        )
        relationshipPopup.setEventListener {
            viewModel.registerModel.value?.emergencyContactRelationship = it.name?.toLowerCase(Locale.getDefault())
            viewModel.refreshRegisterModel()
        }
    }

    fun onGallery() {
        GalleryHelper(this).showGallery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GalleryHelper.ARG_GALLERY) {
            data?.data?.let {
                loadingImage.isVisible = true
                cameraImageView.isVisible = false

                try {
                    viewModel.uploadImage(FileUtil.from(this, it))
                } catch (e : Exception) {
                    showAlert(this, "Gambar tidak bisa di gunakan, mohon menggunakan gambar yang sudah ada di galeri")
                }
            } ?: kotlin.run {
                showAlert(this, "Gambar tidak ditemukan")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        fun onNewIntent(context: Context, isFromEditProfile : Boolean = false): Intent {
            val intent = Intent(context, RegisterProfileActivity::class.java)
            intent.putExtra(RegisterProfileViewModel.EXTRA_IS_FROM_EDIT_PROFILE, isFromEditProfile)
            return intent
        }
    }
}