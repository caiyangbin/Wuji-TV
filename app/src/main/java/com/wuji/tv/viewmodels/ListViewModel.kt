package com.wuji.tv.viewmodels

import com.admin.libcommon.base.BaseViewModel
import com.wuji.tv.presenter.ListPresenter
import com.wuji.tv.repository.ListRepository


class ListViewModel(
    private val repository: ListRepository,
    private val presenter: ListPresenter
) : BaseViewModel() {


}
