package com.wuji.tv.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wuji.tv.R
import com.wuji.tv.common.fileListItemDecoration
import com.wuji.tv.model.Download
import com.wuji.tv.ui.adapter.ProgressAdapter
import com.wuji.tv.utils.RxUtils
import com.wuji.tv.viewmodels.FileManagerViewModel
import com.wuji.tv.widget.SelfDialog
import kotlinx.android.synthetic.main.fragment_downloading.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.ext.android.inject


const val DOWNLOADING_FRAGMENT_GO_BACK = 0
const val DOWNLOADING_FRAGMENT_GO_BACK_DETAIL = 1

class DownloadingFragment : com.wuji.tv.common.BaseFragment() {

    private lateinit var downloadAdapter: ProgressAdapter
    private lateinit var downloadLayoutManager: LinearLayoutManager
    private var goBackState = DOWNLOADING_FRAGMENT_GO_BACK
    private var networkId = ""

    private val viewModel by inject<FileManagerViewModel>()
    private val rxUtils by lazy {
        RxUtils()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
        initData()
    }

    private fun initView() {
        downloadAdapter = ProgressAdapter(null)
        downloadLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvDownloading.apply {
            layoutManager = downloadLayoutManager
            adapter = downloadAdapter
            addItemDecoration(fileListItemDecoration)
        }
    }

    private var clickIndex = 0
    private fun initEvent() {
        downloadAdapter.setOnItemClickListener { view, i, download ->
            //viewModel.toggleDownload(download!!.ticket,download.session)
        }
        downloadAdapter.setOnItemSelectListener { view, i, _ ->
            if (i > 6) {
                rvDownloading.smoothScrollBy(view.x.toInt(), (view.y - 360).toInt())
            }
        }
        downloadAdapter.setOnItemKeyListener { view, keyCode, keyEvent, download, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_MENU -> {
                        clickIndex = position
                        val selfDialog = SelfDialog(context)
                        selfDialog.setMessage(getString(R.string.delete_ex))
                        selfDialog.setMessage2(getString(R.string.delete_ex2))
                        selfDialog.setCenterGravity(Gravity.CENTER)
                        selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
                            selfDialog.dismiss()
                        }
                        selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
                            selfDialog.dismiss()
                            cancelDownload(download)
                        }
                        selfDialog.show()
                        return@setOnItemKeyListener true
                    }
                }
            }
            return@setOnItemKeyListener false
        }
    }

    private fun cancelDownload(download: Download?){
        rxUtils.cancelInterval()
        if (download?.session.isNullOrEmpty()) {
            viewModel.cancelDownload(arrayOf(download!!.ticket))
            viewModel.cancel(download.ticket2, com.wuji.tv.App.token!!)
        }
        else if(download != null){
            viewModel.cancelDb(arrayOf(download.ticket),download.session)
        }
        viewModel.getDownloadingList(networkId,context!!)
    }

    private fun getProgress(){
        rxUtils.cancelInterval()
        rxUtils.executionInterval(1) {
            var hasDownload = false
            downloadAdapter.getDatas().forEachIndexed { index,download ->
                if(!downloadAdapter.getProgressIs100(index)){
                    hasDownload = true
                    if (download.session.isNullOrEmpty()) {
                        viewModel.getProgress(download.ticket)
                    } else {
                        viewModel.getDbProgress(download.ticket,download.session)
                    }
                }
            }
            if (!hasDownload){
                rxUtils.cancelInterval()
            }
        }
    }

    private fun initData() {
        goBackState = arguments?.getInt("goBackState") ?: 0
        networkId = arguments?.getString("networkId") ?: ""
        viewModel.getDownloadingList(networkId,context!!)
        viewModel.downloadListLiveData.observe(this, Observer { result ->
            if (result != null && result.isNotEmpty()) {
                tvError.remove()
                rvDownloading.show()
                downloadAdapter.setDatasAndRefresh(result)
                downloadLayoutManager.findViewByPosition(0)?.apply { requestFocus() }
                getProgress()
            } else {
                tvError.show()
                tvError.text = getString(R.string.download_ex_nothing)
                rvDownloading.remove()
            }
        })

        viewModel.progressLiveData.observe(this, Observer {
            if (it != null && !isDestroy) {
                downloadAdapter.getDatas().forEachIndexed { index, download ->
                    if (download.ticket == it.ticket) {
                        downloadAdapter.notifyItemChanged(index, it)
                    }
                }
            }
        })

        viewModel.downloadStatuLiveData.observe(this, Observer {
            when (it) {
                1 -> toast(getString(R.string.pause))
                2 -> toast(getString(R.string.start))
                4 -> toast(getString(R.string.ex_error))
                5 -> toast(getString(R.string.chipanyiman))
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
        rxUtils.cancelInterval()
        LiveEventBus.get("on_device_request").post("")
    }
    private var isDestroy = false

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if(event.action == KeyEvent.ACTION_UP){
            return false
        }
        when(event.keyCode){
            KeyEvent.KEYCODE_BACK -> {
                if(goBackState == DOWNLOADING_FRAGMENT_GO_BACK_DETAIL){
                    return findNavController().popBackStack(R.id.detailFragment,false)
                }
            }
        }
        return false
    }
    override fun getLayoutID(): Int = R.layout.fragment_downloading
}