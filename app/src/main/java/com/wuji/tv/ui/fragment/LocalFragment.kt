package com.wuji.tv.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.local_manager_fragment.*
import com.wuji.tv.Constants.Companion.ERROR
import com.wuji.tv.Constants.Companion.GET_DEVICE
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_TITLE
import com.wuji.tv.R
import com.wuji.tv.common.deviceDecoration
import com.wuji.tv.model.LocalFile
import com.wuji.tv.ui.adapter.LocalDeviceAdapter
import com.wuji.tv.viewmodels.FileManagerViewModel
import org.koin.android.ext.android.inject

@Suppress("UNCHECKED_CAST")
class LocalFragment : com.wuji.tv.common.BaseFragment() {

    private lateinit var deviceAdapter: LocalDeviceAdapter
    private lateinit var deviceLayoutManager: GridLayoutManager

    private var lastClickPosition = 1
    private val titlePositions = ArrayList<Int>()

    private val viewModel by inject<FileManagerViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLiveData()
        initLiveEvent()
        initView()
        initEvent()
    }


    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initLiveEvent() {
        LiveEventBus.get("file_request_focus").observe(this, Observer {
            rvLocalDevices.post {
                deviceLayoutManager.findViewByPosition(lastClickPosition)?.requestFocus()
            }
        })
    }

    private fun initLiveData() {
        viewModel.liveData.observe(this, Observer {
            when (it.dataType) {
                GET_DEVICE -> {
                    val devices = it.data as ArrayList<LocalFile>
                    deviceAdapter.setDatasAndRefresh(devices)
                }
                ERROR -> {
                    val e = it.data as Exception
                    tvError.show()
                    rvLocalDevices.remove()
                    tvError.text = e.message
                }
            }
        })
    }

    private fun initEvent() {

        deviceAdapter.setOnItemKeyListener { view, keyCode, keyEvent, device, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnItemKeyListener handleDeviceLeftKeyEvent(position)
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        return@setOnItemKeyListener handleDeviceRightKeyEvent(position)
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        return@setOnItemKeyListener handleDeviceDownKeyEvent(position)
                    }
                }
            }
            return@setOnItemKeyListener false
        }

        deviceAdapter.setOnItemClickListener { view, i, localFile ->
            lastClickPosition = i
            findNavController().navigate(R.id.localFileBrowserFragment, Bundle().apply {
                putString("root_path", localFile!!.path)
            })
        }

        deviceAdapter.setOnItemSelectListener { view, i, _ ->
            if (i > 6) {
                rvLocalDevices.smoothScrollBy(view.x.toInt(), (view.y - 360).toInt())
            }
        }
        tvError.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                ViewCompat.animate(v).scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }
    }

    private fun handleDeviceDownKeyEvent(position: Int): Boolean {
        if (position < deviceAdapter.itemCount && position > deviceAdapter.itemCount - 3) {
            return true
        }
        return false
    }

    private fun handleDeviceRightKeyEvent(position: Int): Boolean {
        if (position == deviceAdapter.itemCount - 1) {
            return true
        }
        var lastTitlePosition = -1
        titlePositions.forEach {
            if (position > it) {
                lastTitlePosition = it
            }
        }
        if ((position - lastTitlePosition) % 3 == 0) {
            return true
        }
        return false
    }

    private fun handleDeviceLeftKeyEvent(position: Int): Boolean {
        var lastTitlePosition = -1
        titlePositions.forEach {
            if (position > it) {
                lastTitlePosition = it
            }
        }
        if ((position - lastTitlePosition - 1) % 3 == 0) {
            LiveEventBus.get("page_request_focus").post("")
            return true
        }
        return false
    }

    private fun initView() {

        deviceAdapter = LocalDeviceAdapter(null)
        deviceLayoutManager = GridLayoutManager(activity, 3)
        deviceLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (deviceAdapter.getItemViewType(position) == ITEM_VIEW_TYPE_TITLE) {
                    if (!titlePositions.contains(position)) {
                        titlePositions.add(position)
                        // 排序确保title position是从小到大，方便后面比对取出
                        titlePositions.sort()
                    }
                    return 3
                }
                return 1
            }
        }
        rvLocalDevices.adapter = deviceAdapter
        rvLocalDevices.layoutManager = deviceLayoutManager
        rvLocalDevices.addItemDecoration(deviceDecoration)
    }


    private fun initData() {
        viewModel.getDevices()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    override fun getLayoutID(): Int = R.layout.local_manager_fragment
}