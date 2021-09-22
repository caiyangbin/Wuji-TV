package com.wuji.tv.di

import com.wuji.tv.database.AppDatabase
import com.wuji.tv.presenter.DevicesPresenter
import com.wuji.tv.presenter.FileManagerPresenter
import com.wuji.tv.presenter.FilePresenter
import com.wuji.tv.presenter.MainPresenter
import com.wuji.tv.repository.DevicesRepository
import com.wuji.tv.repository.FileManagerRepository
import com.wuji.tv.repository.FileRepository
import com.wuji.tv.repository.MainRepository
import com.wuji.tv.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //repository
    single { MainRepository(androidContext()) }

    //presenter
    single { MainPresenter(androidContext(), get()) }

    //viewModels
    viewModel { MainViewModel(get(), get()) }


    //repository
    single { FileRepository(androidContext()) }

    //presenter
    single { FilePresenter(androidContext(), get()) }

    //viewModels
    viewModel { FileViewModel(get(), get()) }


    //repository
    single {
        DevicesRepository(
            androidContext(),
            AppDatabase.getInstance(androidContext()).getDownloadListDao()
        )
    }

    //presenter
    single { DevicesPresenter(androidContext(), get()) }

    //viewModels
    viewModel { DevicesViewModel(get(), get()) }
    viewModel { DevicesViewModel3(get(), get()) }


    //repository
    single { FileManagerRepository(androidContext(),  AppDatabase.getInstance(androidContext()).getDownloadListDao()) }

    //presenter
    single { FileManagerPresenter(androidContext(), get()) }

    //viewModels
    viewModel { FileManagerViewModel(get(), get()) }

}