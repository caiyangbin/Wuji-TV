package com.wuji.tv.viewmodels

import com.admin.libcommon.base.BaseViewModel
import com.wuji.tv.presenter.DetailPresenter
import com.wuji.tv.repository.DetailRepository


class DetailViewModel(
    private val repository: DetailRepository,
    private val presenter: DetailPresenter
) : BaseViewModel() {


}
