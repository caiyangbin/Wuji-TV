package com.wuji.tv.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.ext.log
import com.wuji.tv.BuildConfig
import com.wuji.tv.R
import com.wuji.tv.common.setCurrentFocus
import com.wuji.tv.model.UpdateData
import com.wuji.tv.ui.MainActivity
import com.wuji.tv.ui.adapter.MainPageListAdapter
import com.wuji.tv.ui.adapter.ViewPagerAdapter
import com.wuji.tv.viewmodels.MainViewModel
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_main_page_list.view.*
import okhttp3.Call
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.alert
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*

class MainFragment : com.wuji.tv.common.BaseFragment() {

    private val viewModel by inject<MainViewModel>()

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var pagesAdapter: MainPageListAdapter
    private lateinit var pagesLayoutManager: LinearLayoutManager
    private var currentPosition = 0

    companion object {
        var isRootFragment = true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isNavigationViewInit) {
            super.onViewCreated(view, savedInstanceState)
            initView()
            initData()
            initEvent()
            isNavigationViewInit = true
        }
    }

    private fun initView() {
        pagesAdapter = MainPageListAdapter(null)
        pagesLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        pagesRv.apply {
            adapter = pagesAdapter
            layoutManager = pagesLayoutManager
        }

        viewPager.offscreenPageLimit = 4
        viewPagerAdapter = ViewPagerAdapter(fragmentManager!!).apply {
            viewPager.adapter = this
        }
    }


    private fun checkUpdate() {
        doAsync {
            val i = Random().nextInt()
            val response =
                OkHttpUtils.get().url("https://www.baidu.com/")
                    .build().execute()
            if (response.isSuccessful) {
                val gson = Gson()
                val updateData = gson.fromJson(response.body()?.string(), UpdateData::class.java)
                val versionCode = BuildConfig.VERSION_CODE
                if (updateData.newVersion > versionCode) {
                    OkHttpUtils.get().url(updateData.appDownloadUrl).build()
                        .execute(object :
                            FileCallBack(context!!.filesDir.absolutePath, "xxx.apk") {
                            override fun onResponse(file: File?, id: Int) {
                                "file"
                                file?.apply {
                                    val dialog = AlertDialog.Builder(activity)
                                    dialog.setNegativeButton(getString(R.string.confirm)) { dialog, _ ->
                                        dialog.dismiss()
                                        installApk(file)
                                    }
                                    dialog.setMessage(getString(R.string.update_hint))
                                    dialog.setCancelable(false)
                                    dialog.show()
                                }
                            }

                            override fun onError(call: Call?, e: Exception?, id: Int) {
                                "e=$e".log()
                            }
                        })
                }
            }
        }
    }

    private fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        } else {
            //Android7.0之后获取uri要用contentProvider
            val uri = FileProvider.getUriForFile(
                context!!,
                "com.wuji.tv.provider",
                file
            )
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun initData() {

        checkUpdate()


        LiveEventBus.get("page_request_focus").observe(this, Observer {
            pagesRv.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
            pagesLayoutManager.setCurrentFocus(currentPosition)
        })

        viewModel.initPageTitles().observe(this, Observer { datas ->
            pagesAdapter.setDatasAndRefresh(datas)
            pagesLayoutManager.setCurrentFocus(0)
        })

        viewModel.initPageFragments().observe(this, Observer { datas ->
            viewPagerAdapter.setFragment(datas)
        })
    }


    private fun initEvent() {
        pagesAdapter.setOnItemSelectListener { _, i, _ ->
            viewPager.currentItem = i
            currentPosition = i
        }
        pagesAdapter.setOnItemClickListener { view, i, pageItem ->
            if (i == 0) {
                LiveEventBus.get("refresh_device").post("")
            }
        }
        pagesAdapter.setOnItemKeyListener { view, keyCode, keyEvent, _, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (position == pagesAdapter.itemCount - 1) {
                            return@setOnItemKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (position == 0) {
                            return@setOnItemKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        setItemColor(view, position)
                    }
                }
            }
            return@setOnItemKeyListener false
        }
    }

    private fun setItemColor(view: View, position: Int) {
        pagesRv.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        when (position) {
//            0 -> LiveEventBus.get("discovery_request_focus").post("")
            0 -> LiveEventBus.get("contacts_request_focus").post("")
            1 -> LiveEventBus.get("file_request_focus").post("")
            2 -> LiveEventBus.get("apps_request_focus").post("")
//            4 -> LiveEventBus.get("options_request_focus").post("")
            3 -> LiveEventBus.get("settings_request_focus").post("")
        }
        view.postDelayed({
            when (currentPosition) {
//                0 -> view.icon.setImageResource(R.mipmap.quanzi_focus)
                0 -> view.icon.setImageResource(R.mipmap.cloud_focus)
                1 -> view.icon.setImageResource(R.mipmap.file_icon)
                2 -> view.icon.setImageResource(R.mipmap.app_focus)
                3 -> view.icon.setImageResource(R.mipmap.settings)
            }
        }, 20)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        val homeActivity = activity!! as MainActivity
//        homeActivity.setFragmentKeyEventListener(this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        if (event.action == KeyEvent.ACTION_DOWN) {
//            when (event.keyCode) {
//                KeyEvent.KEYCODE_BACK -> {
//                    "back".log()
//                    return true
//                }
//            }
//        }
        if(event.action == KeyEvent.ACTION_UP){
            return false
        }
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK && isRootFragment) {
            alert {
                message = getString(R.string.exit_hint)
                negativeButton(getString(R.string.cancel)) { dialog ->
                    dialog.dismiss()
                }
                positiveButton(getString(R.string.confirm)) { dialog ->
                    (activity as MainActivity).finish()
                    dialog.dismiss()
                }
            }.show()
            return true
        }
        return false
    }

    override fun getLayoutID(): Int = R.layout.fragment_main
}