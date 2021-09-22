package com.wuji.tv.common

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.base.BaseViewModel.Companion.HIDE_LOADING
import com.admin.libcommon.base.BaseViewModel.Companion.SHOW_LOADING
import com.wuji.tv.Constants.Companion.CHANGE_LOADING_STATUS
import com.wuji.tv.ui.dialog.LoadingDialog
import org.jetbrains.anko.support.v4.runOnUiThread

abstract class BaseFragment : Fragment() {

    var loadingDialog: LoadingDialog? = null
    protected var isNavigationViewInit = false
    protected var lastView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(getApplicationContext())
                .inflateTransition(android.R.transition.move)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lastView = null
        loadingDialog = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (lastView == null) {
            lastView = inflater.inflate(getLayoutID(), container, false)
        }
        return lastView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!isNavigationViewInit || savedInstanceState != null){
            super.onViewCreated(view, savedInstanceState)
            loadingDialog = LoadingDialog(activity!!)
            LiveEventBus.get(CHANGE_LOADING_STATUS).observe(this, Observer {
                when (it) {
                    HIDE_LOADING -> hideLoading()
                    SHOW_LOADING -> showLoading()
                }
            })
        }
    }


//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        val homeActivity = activity!! as MainActivity
//        homeActivity.setFragmentKeyEventListener(this)
//        super.onActivityCreated(savedInstanceState)
//    }


    open fun showLoading() {
        runOnUiThread {
            if (!loadingDialog?.isShowing!!) {
                loadingDialog?.show()
            }
        }
    }

    open fun hideLoading() {
        runOnUiThread {
            if (loadingDialog?.isShowing!!) {
                loadingDialog?.dismiss()
            }
        }
    }

    open fun getApplicationContext() : Context? {
        return context
    }

    abstract fun getLayoutID(): Int
    abstract fun dispatchKeyEvent(event: KeyEvent): Boolean

}