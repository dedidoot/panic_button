package com.panic.button.core.base

import com.panic.button.feature.covid.CovidViewModel
import com.panic.button.feature.home.HomeViewModel
import com.panic.button.feature.ktpupload.IdentityCardViewModel
import com.panic.button.feature.login.LoginViewModel
import com.panic.button.feature.lostletter.LostLetterViewModel
import com.panic.button.feature.news.NewsViewModel
import com.panic.button.feature.police.PoliceViewModel
import com.panic.button.feature.prayer.PrayerScheduleViewModel
import com.panic.button.feature.register.RegisterViewModel
import com.panic.button.feature.registerprofile.RegisterProfileViewModel
import com.panic.button.feature.reportpolice.HistoryReportViewModel
import com.panic.button.feature.ronda.RondaViewModel
import com.panic.button.feature.skck.SkckViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object Modules {

    fun getModules(): List<Module> {
        val module = module {
            viewModel { LoginViewModel() }
            viewModel { RegisterViewModel() }
            viewModel { RegisterProfileViewModel() }
            viewModel { IdentityCardViewModel() }
            viewModel { HomeViewModel() }
            viewModel { CovidViewModel() }
            viewModel { PrayerScheduleViewModel() }
            viewModel { SkckViewModel() }
            viewModel { RondaViewModel() }
            viewModel { PoliceViewModel() }
            viewModel { NewsViewModel() }
            viewModel { LostLetterViewModel() }
            viewModel { HistoryReportViewModel() }
        }
        return listOf(module)
    }
}