package com.wuji.tv.ui.fragment

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.wuji.tv.R
import com.wuji.tv.common.appsDecoration
import com.wuji.tv.model.AppsModel
import com.wuji.tv.ui.adapter.AppsAdapter
import kotlinx.android.synthetic.main.fragment_apps.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread


class AppsFragment : com.wuji.tv.common.BaseFragment() {


    private lateinit var urlsAdapter: AppsAdapter
    private lateinit var appsAdapter: AppsAdapter
    private lateinit var urlsLayoutManager: GridLayoutManager
    private lateinit var appsLayoutManager: GridLayoutManager
    private var currentUrlsPosition = 0
    private var currentAppsPosition = 0

    private val urls = ArrayList<AppsModel>()
    private val apps = ArrayList<AppsModel>()


    private val pkgs = arrayListOf(
        "com.wukongtv.wkhelper",
        "com.dangbei.zhushou",
        "com.dangbeimarket",
        "cn.com.imovie.player",
        "com.hpplay.happyplay.aw",
        "com.voole.dbcinema"
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        LiveEventBus.get("apps_request_focus").observe(this, Observer {
            rvApps.post {
                rvApps.requestFocus()
            }
        })

        initView()
        initEvent()
//        initUrls()
        initApps()
    }

    private fun initEvent() {

        appsAdapter.setOnItemClickListener { _, _, appsModel ->
            forwardToSpecialApp(appsModel!!.content!!)
        }

    }


    private fun initView() {
        appsLayoutManager = GridLayoutManager(activity, 3)

        appsAdapter = AppsAdapter(null)

        rvApps.layoutManager = appsLayoutManager

        rvApps.adapter = appsAdapter

        rvApps.addItemDecoration(appsDecoration)
    }

    private fun initUrls() {

        urls.add(AppsModel("google.com"))
        urls.add(AppsModel("baidu.com"))
        urls.add(AppsModel("qq.com"))
        urls.add(AppsModel("youtube.com"))
        urls.add(AppsModel("add"))

        urlsAdapter.setDatas(urls)
    }

    private fun initApps() {

        doAsync {
            val installedPackages =
                context!!.packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
            installedPackages.forEach {
                val flags = it.applicationInfo.flags
                if (flags and ApplicationInfo.FLAG_SYSTEM != ApplicationInfo.FLAG_SYSTEM) {
                    val title =
                        it.applicationInfo.loadLabel(context!!.packageManager).toString()
                    val icon = it.applicationInfo.loadIcon(context!!.packageManager)
                    val pkg = it.applicationInfo.packageName
                    if (pkg != "com.wuji.tv") {
                        val item = AppsModel(title, pkg, icon)
                        apps.add(item)
                    }
                }
            }
            runOnUiThread {
                appsAdapter.setDatasAndRefresh(apps)
            }
        }
    }

    private fun forwardToSpecialApp(packageName: String) {
        try {
            context?.packageManager?.getLaunchIntentForPackage(packageName).apply {
                context?.startActivity(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }


    override fun getLayoutID(): Int = R.layout.fragment_apps
}