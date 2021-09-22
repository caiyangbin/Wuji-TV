package com.wuji.tv.repository

import android.content.Context
import androidx.fragment.app.Fragment
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.model.AccessResult
import com.wuji.tv.model.BaseResult
import com.wuji.tv.model.PageItem
import com.wuji.tv.ui.fragment.AppsFragment
import com.wuji.tv.ui.fragment.DevicesFragment
import com.wuji.tv.ui.fragment.LocalFragment


class MainRepository(private val context: Context) {

    suspend fun access(token: String): BaseResult<AccessResult>? {
        val params = HashMap<String, Any>()
        params["token"] = token
        val body = HashMap<String, Any>()
        body["method"] = "access"
        body["session"] = ""
        body["params"] = params
        return App.api?.access(body)
//        return App.sdvnApi?.access(getBaseUrl(App.ip!!, PORT_URL),body)
    }

    fun getPageTitles(): ArrayList<PageItem> {
        return ArrayList<PageItem>().apply {
//            add(PageItem("探索"))
            add(PageItem(context.getString(R.string.online)))
            add(PageItem(context.getString(R.string.local)))
            add(PageItem(context.getString(R.string.app)))
//            add(PageItem("设置"))
        }
    }

    fun getPageFragments(): ArrayList<Fragment> {
        return ArrayList<Fragment>().apply {
//            add(DiscoveryFragment())
            add(DevicesFragment())
            add(LocalFragment())
            add(AppsFragment())
//            add(SettingsFragment())
        }
    }
}