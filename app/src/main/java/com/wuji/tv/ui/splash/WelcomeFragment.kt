package com.wuji.tv.ui.splash

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.admin.libcommon.ext.getProperty
import com.admin.libcommon.ext.log
import com.admin.libcommon.ext.setProperty
import com.wuji.tv.App
import com.wuji.tv.BuildConfig
import com.wuji.tv.R
import com.wuji.tv.ui.MainActivity
import com.wuji.tv.utils.RxUtils
import io.sdvn.apigateway.data.model.SharedUserList
import io.sdvn.apigateway.protocal.ASBaseListener
import io.sdvn.apigateway.repo.AsRepo
import io.sdvn.socket.Constants
import io.sdvn.socket.ResultListener
import io.sdvn.socket.SDVNApi
import kotlinx.android.synthetic.main.fragment_welcome.*
import okhttp3.Call
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.text.DecimalFormat

class WelcomeFragment : com.wuji.tv.common.BaseFragment() {

  private lateinit var connectivityManager: ConnectivityManager
  private val rxUtils by lazy {
    RxUtils()
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    connectivityManager =
      activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    initView()
    initEvent()
    initData()
  }

  private fun isNetworkConnected(): Boolean {
    connectivityManager.allNetworkInfo?.forEach {
      if (it.state == NetworkInfo.State.CONNECTED)
        return true
    }
    return false
  }

  private fun initData() {

    if (!isNetworkConnected()) {
      toast("检测到您未连接网络，应用无法正常使用，即将在3秒钟之后自动退出。")
      rxUtils.executionTimer(3) {
        activity?.finish()
      }
      return
    }

    SDVNApi.getInstance().liveConnStatus.observe(this, Observer {
      when (it) {
        Constants.CS_CONNECTED -> {
          checkBind()
        }
        Constants.CS_DISCONNECTED -> {

        }
        else -> {

        }
      }
    })



    nextBtn.setOnFocusChangeListener { _, hasFocus ->
      if (hasFocus) {
        webViewRoot.setBackgroundResource(R.color.transparent)
      } else {
        webViewRoot.setBackgroundResource(R.drawable.shape_welcome_web_view_focus)
      }
    }
    nextBtn.setOnClickListener {
      (activity as SplashActivity).nextPage()
    }
  }


  override fun onResume() {
    super.onResume()
    nextBtn.requestFocus()
  }


  private fun initEvent() {
    webView.setOnKeyListener { v, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN) {
        when (keyCode) {
          KeyEvent.KEYCODE_DPAD_LEFT -> {
            return@setOnKeyListener true
          }
          KeyEvent.KEYCODE_DPAD_RIGHT -> {
            nextBtn.requestFocus()
            return@setOnKeyListener true
          }
        }
      }
      return@setOnKeyListener false
    }
    nextBtn.setOnKeyListener { v, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN) {
        when (keyCode) {
          KeyEvent.KEYCODE_DPAD_LEFT -> {
            return@setOnKeyListener true
          }
          KeyEvent.KEYCODE_DPAD_RIGHT -> {
            return@setOnKeyListener true
          }
        }
      }
      return@setOnKeyListener false
    }
  }


  private fun initView() {
    splashRoot.show()
    tvHint.text = BuildConfig.VERSION_NAME
    registerRoot.remove()
  }

  private fun checkBind() {
    val isConnected = SDVNApi.getInstance().isConnected()
    val self = SDVNApi.getInstance().getSelf()
    self?.let {
      AsRepo().getSharedUserList(it.id, object :
        ASBaseListener<SharedUserList> {
        override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
        }

        override fun onSuccess(call: Call, data: SharedUserList) {
          if (data.users != null && data.users!!.isNotEmpty()) {
            doAsync {
              Thread.sleep(3000)
              runOnUiThread {
                activity?.finish()
                startActivity<MainActivity>()
              }
            }
          } else {
            runOnUiThread {
              showBindBanner()
            }
          }
        }
      })
    }
  }

  private fun showBindBanner() {

    webView.webViewClient = object : WebViewClient() {
      override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
      ) {
        handler?.proceed()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        splashRoot.remove()
        registerRoot.show()
      }
    }
    val set = webView.settings
    set.textZoom = 150
    webView.loadUrl("https://www.baidu.com/")
  }

  private fun showSplash() {

    splashRoot.show()
    registerRoot.remove()

    tvHint.text = getString(R.string.init_msg)

    doAsync {
      val property = getProperty("isInitSdvnService", "false")
      if (property == "true") {
        initSdvnSocket()
        return@doAsync
      }
      var flag = false
      var count = 0f
      while (count < 10f) {
        flag = isHaveStorage()
        "flag=$flag".log()
        if (flag) {
          break
        }
        Thread.sleep(1000)
        count++
        runOnUiThread {
          val decimalFormat = DecimalFormat("#")
          tvHint.text =
            "${getString(R.string.initing_hint)}${decimalFormat.format(count / 10f * 100)}%"
        }
      }

      if (flag) {
        runOnUiThread {
          tvHint.text = getString(R.string.start_server)
        }
        Thread.sleep(2000)
        initSdvnSocket()
      } else {
        runOnUiThread {
          alert {
            isCancelable = false
            title = getString(R.string.youqing_hint)
            message = getString(R.string.mount_hint)
            positiveButton(getString(R.string.only_online)) { dialog ->
              dialog.dismiss()
              tvHint.text = getString(R.string.start_server)
              doAsync {
                Thread.sleep(2000)
                initSdvnSocket()
              }

            }
            negativeButton(getString(R.string.yinpan_hint)) { dialog ->
              dialog.dismiss()
              tvHint.text = getString(R.string.check_yinpan)
              checkMount()
            }
          }.show()
        }
      }
    }
  }

  private fun checkMount() {
    doAsync {
      while (true) {
        if (isHaveStorage()) {
          runOnUiThread {
            tvHint.text = getString(R.string.mounted_start)
          }
          Thread.sleep(2000)
          initSdvnSocket()
          break
        }
        Thread.sleep(1000)
      }
    }
  }

  private fun initSdvnSocket() {
    SDVNApi.getInstance().init(App.app, object : ResultListener {
      override fun onSuccess() {
        doAsync {
          Thread.sleep(2000)
          setProperty("isInitSdvnService", "true")
          runOnUiThread {
            activity?.finish()
            startActivity<MainActivity>()

          }
        }
      }

      override fun onError(result: Int, msg: String?) {
        initSdvnSocket()
      }
    })
  }

  private fun isHaveStorage(): Boolean {
    val file = File("/mnt/")
    if (file.exists() && !file.listFiles().isNullOrEmpty()) {
      return true
    }
    return false
  }


  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    return false
  }

  override fun onDestroy() {
    super.onDestroy()
    rxUtils.cancelTimer()
  }


  override fun getLayoutID(): Int = R.layout.fragment_welcome
}