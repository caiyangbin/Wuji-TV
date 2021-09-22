package com.wuji.tv.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.admin.libcommon.base.BaseViewModel
import com.wuji.tv.model.PageItem
import com.wuji.tv.presenter.MainPresenter
import com.wuji.tv.repository.MainRepository

class MainViewModel(private val repository: MainRepository, private val presenter: MainPresenter) :
    BaseViewModel() {


    fun initPageTitles(): MutableLiveData<ArrayList<PageItem>> {
        return MutableLiveData<ArrayList<PageItem>>().apply {
           runOnUI {
               value = withIO { repository.getPageTitles() }
           }
        }
    }

    fun initPageFragments():MutableLiveData<ArrayList<Fragment>> {
        return MutableLiveData<ArrayList<Fragment>>().apply {
            runOnUI {
                value = withIO { repository.getPageFragments() }
            }
        }
    }

}