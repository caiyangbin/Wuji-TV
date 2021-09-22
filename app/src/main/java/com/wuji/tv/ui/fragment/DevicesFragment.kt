package com.wuji.tv.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.base.BaseViewModel
import com.admin.libcommon.ext.doOnIO
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.BuildConfig
import com.wuji.tv.R
import com.wuji.tv.common.deviceHomeDecoration
import com.wuji.tv.common.deviceManageDecoration
import com.wuji.tv.database.AppDatabase
import com.wuji.tv.model.*
import com.wuji.tv.ui.MainActivity
import com.wuji.tv.ui.adapter.DeviceAdapter
import com.wuji.tv.ui.adapter.ProgressNetworkAdapter
import com.wuji.tv.utils.BitmapUtils
import com.wuji.tv.utils.FileUtils
import com.wuji.tv.viewmodels.DevicesViewModel3
import com.wuji.tv.widget.MyLinearLayoutManager
import com.wuji.tv.widget.SelfDialog
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import io.sdvn.apigateway.TokenCallback
import io.sdvn.apigateway.data.model.BindDevices
import io.sdvn.apigateway.data.model.BindNetsInfo
import io.sdvn.apigateway.data.model.CircleDevice
import io.sdvn.apigateway.data.model.GsonBaseProtocolV2
import io.sdvn.apigateway.protocal.ASBaseListener
import io.sdvn.apigateway.repo.AsRepo
import io.sdvn.socket.Constants
import io.sdvn.socket.ResultListener
import io.sdvn.socket.SDVNApi
import io.sdvn.socket.data.SDVNDevice
import io.sdvn.socket.utils.CommonUtils
import io.weline.devhelper.DevTypeHelper
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.item_device.view.*
import okhttp3.Call
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DevicesFragment : com.wuji.tv.common.BaseFragment(), BitmapUtils.DownloadListener {

    private val handler = Handler()
    private val viewModel by inject<DevicesViewModel3>()

    private lateinit var networksAdapter: DeviceAdapter
    private lateinit var progressNetworkAdapter: ProgressNetworkAdapter
    private lateinit var sharedDeviceAdapter: DeviceAdapter
    private lateinit var localDeviceAdapter: DeviceAdapter
    private lateinit var managerDeviceAdapter: DeviceAdapter
    private lateinit var progressNetworkLayoutManager: LinearLayoutManager
    private lateinit var networksLayoutManager: LinearLayoutManager
    private lateinit var sharedDeviceLayoutManager: LinearLayoutManager
    private lateinit var localDeviceLayoutManager: LinearLayoutManager
    private lateinit var managerDeviceLayoutManager: LinearLayoutManager

    private var networks = ArrayList<RemoteDeviceModel>()
    private var progressNetwork = ArrayList<String>()
    private var networksError = ArrayList<RemoteDeviceModel>()
    private var networksSize = 0
    private var sharedDevicesNew = ArrayList<RemoteDeviceModel>()
    private var sharedDevicesOld = ArrayList<RemoteDeviceModel>()
    private var sharedDevicesError = ArrayList<RemoteDeviceModel>()
//    private var sharedDevicesSize = 0
    private var localDevicesOld = ArrayList<RemoteDeviceModel>()
    private var localDevicesNew = ArrayList<RemoteDeviceModel>()
    private var localDevicesError = ArrayList<RemoteDeviceModel>()
    private var token: String? = null
    private val myDevicesId = Vector<String>()
    private val myNetworksId = Vector<String>()
    private val myDevicesGb2cRatio = HashMap<String, Double>()
    private val myNetworksGb2cRatio = HashMap<String, Double>()

    private var dataGetting = false
    private var currentGetDataDevice: RemoteDeviceModel? = null
    private var currentNetworksId: String? = null
    private var localNetworkId: String? = null
    private var lastNetworksClickPosition: Int = -1
    private var lastSharedClickPosition: Int = -1
    private var lastLocalClickPosition: Int = -1
    private var lastManagerClickPosition: Int = -1
    private var onStop: Boolean = false


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onStop = false
        if(!isNavigationViewInit || savedInstanceState != null){
            initLocalDevice()
            initView()
            initLiveBus()
            initLiveData()
            initEvent()
            checkUpdate()
            liveConnStatus()
            isNavigationViewInit = true
        }
    }

    override fun onResume() {
        super.onResume()
        initFocus()
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(refreshImgRunnable)
        super.onDestroy()
    }

    private fun initFocus() {
        if(lastNetworksClickPosition != -1){
            networksLayoutManager.findViewByPosition(lastNetworksClickPosition)?.requestFocus()
            lastNetworksClickPosition = -1
            if(currentNetworksId != localNetworkId){
                switchNetwork(localNetworkId!!, true)
            }
        }
        else if(lastSharedClickPosition != -1){
            sharedDeviceLayoutManager.findViewByPosition(lastSharedClickPosition)?.requestFocus()
            lastSharedClickPosition = -1
        }
        else if(lastLocalClickPosition != -1){
            localDeviceLayoutManager.findViewByPosition(lastLocalClickPosition)?.requestFocus()
            lastLocalClickPosition = -1
        }
        else if(lastManagerClickPosition != -1){
            managerDeviceLayoutManager.findViewByPosition(lastManagerClickPosition)?.requestFocus()
            lastManagerClickPosition = -1
            if(currentNetworksId != localNetworkId){
                switchNetwork(localNetworkId!!, true)
            }
        }
    }

    override fun onStop() {
        onStop = true
        super.onStop()
    }

    private fun initLiveBus() {
        LiveEventBus.get("on_device_request").observe(this, androidx.lifecycle.Observer {
            "on_device_request".log("DeviceFragment")
        })
        LiveEventBus.get("refresh_device").observe(this, androidx.lifecycle.Observer {
            liveConnStatus()
        })
    }

    private fun initView() {
        tvDeviceName.typeface = Typeface.createFromAsset(resources.assets, "fonts/tab.TTF")

        progressNetworkAdapter = ProgressNetworkAdapter()
        rvProgressNetwork.adapter = progressNetworkAdapter
        progressNetworkLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        progressNetworkLayoutManager.orientation = RecyclerView.VERTICAL
        rvProgressNetwork.layoutManager = progressNetworkLayoutManager

        networksLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        networksLayoutManager.orientation = RecyclerView.HORIZONTAL
        networksAdapter = DeviceAdapter(null,layoutType = 2)
        rvNetwork.adapter = networksAdapter
        rvNetwork.layoutManager = networksLayoutManager
        rvNetwork.addItemDecoration(deviceHomeDecoration)
        rvNetwork.animation = null

        sharedDeviceAdapter = DeviceAdapter(null)
        sharedDeviceLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        sharedDeviceLayoutManager.orientation = RecyclerView.HORIZONTAL
        rvSharedDevice.adapter = sharedDeviceAdapter
        rvSharedDevice.layoutManager = sharedDeviceLayoutManager
        rvSharedDevice.addItemDecoration(deviceHomeDecoration)
        rvSharedDevice.animation = null

        localDeviceLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        localDeviceLayoutManager.orientation = RecyclerView.HORIZONTAL
        localDeviceAdapter = DeviceAdapter(null)
        rvLocalDevice.adapter = localDeviceAdapter
        rvLocalDevice.layoutManager = localDeviceLayoutManager
        rvLocalDevice.addItemDecoration(deviceHomeDecoration)
        rvLocalDevice.animation = null

        managerDeviceLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        managerDeviceLayoutManager.orientation = RecyclerView.HORIZONTAL
        managerDeviceAdapter = DeviceAdapter(null, layoutType = 1)
        rvManager.adapter = managerDeviceAdapter
        rvManager.layoutManager = managerDeviceLayoutManager
        rvManager.addItemDecoration(deviceManageDecoration)
        rvManager.animation = null
    }

    private fun initEvent() {

        networksAdapter.setOnFocusChangeListener(object :
            DeviceAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                networksAdapter.setLastFocusPosition(position)
                deviceOnFocusChange(0)
            }

        })
        sharedDeviceAdapter.setOnFocusChangeListener(object :
            DeviceAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                sharedDeviceAdapter.setLastFocusPosition(position)
                deviceOnFocusChange(1)
            }

        })
        localDeviceAdapter.setOnFocusChangeListener(object :
            DeviceAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                localDeviceAdapter.setLastFocusPosition(position)
                deviceOnFocusChange(2)
            }

        })
        managerDeviceAdapter.setOnFocusChangeListener(object :
            DeviceAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                managerDeviceAdapter.setLastFocusPosition(position)
                deviceOnFocusChange(3)
            }

        })

        tvError.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        LiveEventBus.get("page_request_focus").post("")
                    }
                }
            }
            return@setOnKeyListener true
        }

        tvError.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                ViewCompat.animate(v).scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                ViewCompat.animate(v).scaleX(1f).scaleY(1f).setDuration(200).start()
            }
        }

        tvError.setOnClickListener {
            liveConnStatus()
        }
        progressNetworkAdapter.setOnItemClickListener { _, _, position ->
            if(position == progressNetwork.lastIndex){
                lastManagerClickPosition = 0
                layoutProgress.visibility = View.GONE
                findNavController().navigate(R.id.downloadingFragment)
            }
            else if(currentNetworksId == networks[position].sdvnNetworkId!!
                && currentNetworksId != localNetworkId){
                lastManagerClickPosition = 0
                layoutProgress.visibility = View.GONE
                findNavController().navigate(R.id.downloadingFragment, Bundle().apply {
                    putString("networkId", networks[position].sdvnNetworkId!!)
                })
            }
            else{
                mShowLoading()
                doOnIO {
                    switchNetwork(networks[position].sdvnNetworkId!!)
                }
            }
        }
        networksAdapter.setOnItemClickListener { _, position, _ ->
            mShowLoading()
            lastNetworksClickPosition = position
            viewModel.initApi(localDevicesOld[0].sdvnDevice!!)
            viewModel.access(token!!, true)
        }
        sharedDeviceAdapter.setOnItemClickListener { _, position, remoteDevice ->
            if (!DevTypeHelper.isNas(remoteDevice?.sdvnDevice!!.devClass)
                && !DevTypeHelper.isNasByFeature(remoteDevice.sdvnDevice!!.feature)
                && remoteDevice.sdvnDevice!!.isSelectable) {
                showSelectSNDialog(remoteDevice.sdvnDevice!!.id,true)

            } else {
                lastSharedClickPosition = position
                viewModel.initApi(localDevicesOld[0].sdvnDevice!!)
                viewModel.access(token!!, true)
            }
        }
        localDeviceAdapter.setOnItemClickListener { _, position, remoteDevice ->
            lastLocalClickPosition = position
            if (!DevTypeHelper.isNas(remoteDevice?.sdvnDevice!!.devClass)
                && !DevTypeHelper.isNasByFeature(remoteDevice.sdvnDevice!!.feature)
                && remoteDevice.sdvnDevice!!.isSelectable) {
                showSelectSNDialog(remoteDevice.sdvnDevice!!.id,false)

            } else {
                viewModel.initApi(localDevicesOld[0].sdvnDevice!!)
                viewModel.access(token!!, true)
            }
        }
        managerDeviceAdapter.setOnItemClickListener { _, _, remoteDevice ->
            remoteDevice?.apply {
                when(sdvnDevice?.name){
                    getString(R.string.downloaded) -> {
                        lastManagerClickPosition = 1
                        findNavController().navigate(R.id.downloadedBrowserFragment)
                    }
                    getString(R.string.dowloading) -> {
                        progressNetwork.clear()
                        for (network in networks) {
                            progressNetwork.add(network.sdvnDevice?.name ?: "")
                        }
                        progressNetwork.add("朋友/我的设备")
                        if (progressNetwork.size > 1) {
                            layoutProgress.visibility = View.VISIBLE
                            progressNetworkAdapter.setNewInstance(progressNetwork)
                            rvProgressNetwork.post {
                                progressNetworkLayoutManager.findViewByPosition(0)?.requestFocus()
                            }
                        } else {
                            lastManagerClickPosition = 0
                            findNavController().navigate(R.id.downloadingFragment)
                        }
                    }
                    getString(R.string.local_storage) -> {
                        findNavController().navigate(R.id.localFragment)
                    }
                    getString(R.string.run_app) -> {
                        findNavController().navigate(R.id.appsFragment)
                    }
                }
            }
        }
    }

    private fun initLiveData() {
//        viewModel.setLiveDataListener(this)
        viewModel.liveData.observe(this, Observer {
            liveData(it)
        })
    }

    private fun liveConnStatus() {
        mShowProgressBar()
        SDVNApi.getInstance().liveConnStatus.observe(this, Observer {
            when (it) {
                Constants.CS_CONNECTED -> {
                    getMyNetwork()
                }
                Constants.CS_DISCONNECTED -> {
                    hideProgressBar()
                    showError(getString(R.string.device_error_hint))
                }
                else -> {
                    hideProgressBar()
                }
            }
        })
    }

    private fun getMyNetwork(){
        AsRepo().getMyNetwork(object : ASBaseListener<BindNetsInfo> {
            override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
                if (onStop) return
                runOnUiThread {
                    toast("故障：AsRepo().getMyNetwork onFailure $errMsg")
                }
            }

            override fun onSuccess(call: Call, d: BindNetsInfo) {
                if (onStop) return
                d.apply {
                    data?.apply {
                        list?.forEach {
                            if (it.isCharge && it.networkStatus == 0 && it.userStatus == 0 && it.flowStatus == 0) {
                                addNetworks(it.ownerId, it.networkId)
                            }
                        }
                    }
                }
                runOnUiThread {
                    getNetworks()
                }
            }
        })

    }

    private fun getNetworks(){
        SDVNApi.getInstance().liveNetworks.observe(this, Observer {
            if (onStop) return@Observer
            if (layoutProgress.visibility == View.VISIBLE) return@Observer
            it?.apply {
                doOnIO {
                    if (!dataGetting && !deviceGetting && lastNetworksClickPosition == -1 && layoutProgress.visibility == View.GONE) {
                        dataGetting = true
                        networksSize = networks.size
                        for (device in networks) {
                            device.isNewData = false
                        }
                        forEach { sdvn ->
                            addNetworks(sdvn.uid, sdvn.id)
                            if (sdvn.isCurrent) {
                                currentNetworksId = sdvn.id
                            }
                        }
                        App.isCircle = (currentNetworksId != localNetworkId)
                        App.networkId = currentNetworksId
                        getNetworkDevice()
                    }
                }
            }
        })
    }

    private fun addNetworks(uid: String?, id: String?) {
        if(uid == localDevicesNew[0].sdvnDevice?.userid){
            localNetworkId = id
        }
        else{
            var has = false
            for (device in networks){
                if(device.sdvnNetworkId == id){
                    has = true
                    device.isNewData = true
                    break
                }
            }
            if(!has){
                networks.add(
                    RemoteDeviceModel(
                        getString(R.string.circle),
                        false,
                        sdvnNetworkId = id
                    )
                )
            }
        }
    }


    private fun getNetworkDevice(){
        Thread{
            if(localNetworkId == null){
                return@Thread
            }
            if(networks.isEmpty()){
                runOnUiThread {
                    progressBar?.progress = 50
                }
                if(currentNetworksId != localNetworkId){
                    switchNetwork(localNetworkId!!, true)
                }
                else {
                    getBindDevices()
                }
                return@Thread
            }
            currentGetDataDevice?.let {
                if(it.sdvnDevice == null){
                    networks.remove(currentGetDataDevice!!)
                }
            }
            for (index in 0 until networks.size){
                val device = networks[index]
                if(device.sdvnDevice == null){
                    progressBar?.progress = 50 * index / networks.size
                    runOnUiThread { runProgress(50 * (index+1) / networks.size) }
                    currentGetDataDevice = device
                    if(checkDeviceInfoEN()){
                        getNetworkDevice()
                    }
                    else{
                        switchNetwork(device.sdvnNetworkId!!)
                    }
                    return@Thread
                }
            }
            runOnUiThread {
                progressBar?.progress = 50
            }
            if(currentNetworksId != localNetworkId){
                runOnUiThread {
                    logText = "A"
                    tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
                }
                switchNetwork(localNetworkId!!, true)
            }
            else{
                runOnUiThread {
                    logText = "B"
                    tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
                }
                myNetworksId.clear()
                getBindDevices()
            }
        }.start()
    }
    private var logText = ""

    private fun runProgress(maxProgress: Int){
        Handler().postDelayed({
            if(progressBar.progress < maxProgress && !onStop){
                progressBar.progress = progressBar.progress+1
                runProgress(maxProgress)
            }
        },1000)
    }

    private var isSwitchNetwork = false
    private var isSwitchLocalNetwork = false
    private fun switchNetwork(operatorId: String, isLocalNetwork: Boolean = false){
        isSwitchNetwork = true
        myNetworksId.clear()
        isSwitchLocalNetwork = isLocalNetwork
        if(isLocalNetwork){
            SDVNApi.getInstance().switchNetwork(operatorId, object : ResultListener {
                override fun onError(p0: Int, p1: String?) {
                    switchNetworkError(!isSwitchLocalNetwork)
                }

                override fun onSuccess() {
                    switchNetworkSuccess(operatorId, !isSwitchLocalNetwork)
                }
            })
        }
        else {
            AsRepo().switchNetwork(
                operatorId,
                localDevicesNew[0].sdvnDevice?.id!!,
                object : ASBaseListener<GsonBaseProtocolV2<Any>> {
                    override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
                        switchNetworkError(isSwitchLocalNetwork)
                    }

                    override fun onSuccess(call: Call, data: GsonBaseProtocolV2<Any>) {
                        switchNetworkSuccess(operatorId, isSwitchLocalNetwork)
                    }
                })
        }
    }
    private fun switchNetworkError(isLocalNetwork: Boolean){
        if (onStop) return
        if (isLocalNetwork) return
        isGoNextFragment = false
        runOnUiThread {
            logText = "$logText/Err"
            tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
        }
        if (layoutProgress.visibility == View.VISIBLE) {
            mHideLoading()
        } else if (lastNetworksClickPosition == -1) {
            networks.remove(currentGetDataDevice)
            getNetworkDevice()
        } else {
            lastNetworksClickPosition = -1
            mHideLoading()
        }
    }
    private fun switchNetworkSuccess(operatorId: String, flag: Boolean){
        if (onStop) return
        if (flag) return
        if (logText.contains("A")){
            runOnUiThread {
                logText = "$logText/Suc"
                tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
            }
        }
        currentNetworksId = operatorId
        App.isCircle = (operatorId != localNetworkId)
        App.networkId = operatorId
        if (layoutProgress.visibility == View.VISIBLE) {
            mHideLoading()
            onStop = true
            runOnUiThread {
                lastManagerClickPosition = 0
                layoutProgress.visibility = View.GONE
                findNavController().navigate(R.id.downloadingFragment, Bundle().apply {
                    putString("networkId", operatorId)
                })
            }
        } else if (lastNetworksClickPosition == -1) {
            if (currentNetworksId != localNetworkId) {
                getNetworkProvide(operatorId)
            } else {
                getBindDevices()
                isSwitchNetwork = false
            }
        } else {
            if(networks[lastNetworksClickPosition].sdvnDevice?.vip.isNullOrEmpty()){
                getNetworkProvide(operatorId)
            }
            else{
                mHideLoading()
                goNextFragment(networks[lastNetworksClickPosition])
                isSwitchNetwork = false
            }
        }
    }

    private fun getNetworkProvide(networkId: String){
        AsRepo().getNetworkProvide(networkId, object : ASBaseListener<CircleDevice> {
            override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
                if (onStop) return
                networks.remove(currentGetDataDevice)
                getNetworkDevice()
                isSwitchNetwork = false
            }

            override fun onSuccess(call: Call, data: CircleDevice) {
                if (onStop) return
                data.data?.apply {
                    list?.forEach {
                        if (it.srvmain!!) {
                            myNetworksId.add(it.deviceid)
                            if (!it.mbpointratio.isNullOrEmpty()) {
                                myNetworksGb2cRatio[it.deviceid] = it.mbpointratio?.toDouble()!!
                            }
                        }
                    }
                }
                isSwitchNetwork = false
                if (myNetworksId.isNotEmpty()) {
                    getToken()
                }
                else if(lastNetworksClickPosition != -1){
                    switchNetwork(networks[lastNetworksClickPosition].sdvnNetworkId!!)
                }
                else {
                    networks.remove(currentGetDataDevice)
                    getNetworkDevice()
                }
            }
        })

    }

    private fun getBindDevices() {
        if(myDevicesId.isNotEmpty()){
            getToken()
            return
        }
        AsRepo().getBindDevices(object : ASBaseListener<BindDevices> {
            override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
                if (onStop) return
                hideProgressBar()
                showError(getString(R.string.device_error_hint))
                if (logText.contains("A") || logText.contains("B")){
                    runOnUiThread {
                        logText = "$logText/fai"
                        tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
                    }
                }
            }

            override fun onSuccess(call: Call, data: BindDevices) {
                if (onStop) return
                if (logText.contains("A") || logText.contains("B")){
                    runOnUiThread {
                        logText = "$logText/token"
                        tvLoading.text = "$logText-${getString(R.string.resource_loading)}"
                    }
                }
                data.apply {
                    devices?.forEach {
                        if (it.isOwner()) {
                            myDevicesId.add(it.devId)
                        }
                        myDevicesGb2cRatio[it.devId] = it.gb2cRatio
                    }
                }
                getToken()
            }
        })
    }

    private fun getToken() {
        if(token != null){
            getDevice()
            return
        }
        SDVNApi.getInstance().getToken(object : TokenCallback {
            override fun onError(p0: Int, p1: String?) {
                runOnUiThread {
                    hideProgressBar()
                    showError(getString(R.string.device_error_hint))
                }
            }

            override fun success(token: String?) {
                runOnUiThread {
                    token?.apply {
                        App.token = token
                        this@DevicesFragment.token = token
                        getDevice()
                    } ?: run {
                        hideProgressBar()
                        showError(getString(R.string.device_error_hint))
                    }
                }
            }
        })
    }

    var deviceGetting = false
    var getDevice = false
    var isGoNextFragment = false
    private fun getDevice() {
        //添加共享设备
        deviceGetting = false
        if(getDevice) return
        getDevice = true
        SDVNApi.getInstance().liveDevices.observe(this, Observer {
            if (onStop) return@Observer
            if (isSwitchNetwork) return@Observer
            if (lastNetworksClickPosition != -1 && !isGoNextFragment) return@Observer
            if (lastManagerClickPosition != -1) return@Observer
            if (deviceGetting) return@Observer
            if (layoutProgress.visibility == View.VISIBLE) return@Observer
            deviceGetting = true
            if (myNetworksId.isEmpty()) {
                for (device in sharedDevicesNew) {
                    device.isNewData = false
                }
                for (device in localDevicesNew) {
                    device.isNewData = false
                }
                localDevicesNew[0].isNewData = true
            }
//            sharedDevicesSize = sharedDevices.size
//            localDevicesSize = localDevices.size
            it?.forEach { result ->
                if (myNetworksId.isNotEmpty()) {
                    if(lastNetworksClickPosition != -1 && myNetworksId.contains(result.id)){
                        if (isGoNextFragment){
                            isGoNextFragment = false
                            networks[lastNetworksClickPosition].sdvnDevice = result
                            mHideLoading()
                            goNextFragment(networks[lastNetworksClickPosition])
                        }
                        return@forEach
                    }
                    else if (myNetworksId.contains(result.id) && currentGetDataDevice?.sdvnDevice == null) {
                        currentGetDataDevice?.sdvnDevice = result
                        currentGetDataDevice?.price =
                            "${myNetworksGb2cRatio[result.id] ?: 0}${
                                getString(
                                    R.string.price_unit
                                )
                            }"
                        return@forEach
                    }
                } else {
                    if (!DevTypeHelper.isNas(result.devClass)) {
                        //文件服务设备
                        if (DevTypeHelper.isNasByFeature(result.feature)) {
                            if (CommonUtils.getManufacturer(result.devClass) == 0) {
                                when (result.devClass) {
                                    Constants.OT_OSX, Constants.OT_MINIPC, Constants.OT_WINDOWS,
                                    Constants.OT_ANDROID, Constants.OT_IOS -> {
                                    }
                                    else -> {
                                        setSDVNDevice(result)
                                    }
                                }
                            } else {
                                when (CommonUtils.getDeviceType(result.devClass)) {
                                    Constants.DT_MACOS, Constants.DT_WINDOWS, Constants.DT_ANDROID,
                                    Constants.DT_IOS -> {
                                    }
                                    else -> {
                                        setSDVNDevice(result)
                                    }
                                }
                            }
                        }
                        else if (result.isSelectable){
                            setSDVNDevice(result)
                        }
                    } else {
                        setSDVNDevice(result)
                    }
                }
            }
            if (myNetworksId.isNotEmpty() && currentGetDataDevice?.sdvnDevice == null) {
                deviceGetting = false
            } else if(lastNetworksClickPosition == -1) {
                getSession()
            }
        })
    }

    private fun setSDVNDevice(result: SDVNDevice){
        if (myDevicesId.contains(result.id)) {
            var has = false
            for (deviceModel in localDevicesNew){
                if(deviceModel.sdvnDevice?.vip == result.vip){
                    has = true
                    deviceModel.isNewData = true
                }
            }
            if(!has){
                val price = myDevicesGb2cRatio[result.id] ?: 0
                localDevicesNew.add(
                    RemoteDeviceModel(
                        getString(R.string.local_device),
                        false,
                        result,
                        price = "${price}${getString(R.string.price_unit)}",
                        name = result.name,
                        isNewData = true
                    )
                )
            }
        } else {
            var has = false
            for (deviceModel in sharedDevicesNew){
                if(deviceModel.sdvnDevice?.vip == result.vip){
                    has = true
                    deviceModel.isNewData = true
                }
            }
            for (deviceModel in networks){
                if(deviceModel.sdvnDevice?.vip == result.vip){
                    has = true
                    break
                }
            }
            if(!has){
                val price = myDevicesGb2cRatio[result.id] ?: 0
                sharedDevicesNew.add(
                    RemoteDeviceModel(
                        getString(R.string.shared_device),
                        false,
                        result,
                        price = "${price}${getString(R.string.price_unit)}",
                        name = result.name,
                        isNewData = true
                    )
                )
            }
        }
    }

    private fun getSession(){
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        Thread{
            try {
                if(myNetworksId.isNotEmpty()){
                    viewModel.initApi(currentGetDataDevice?.sdvnDevice!!)
                    Thread.sleep(200)
                    if(onStop) return@Thread
                    viewModel?.access(token!!, false)
                }
                else{
                    for (index in 0 until sharedDevicesNew.size){
                        val device = sharedDevicesNew[index]
                        val progress = 50 + 50 * index / (sharedDevicesNew.size+localDevicesNew.size)
                        if (progressBar?.progress ?: 0 < progress){
                            runOnUiThread {
                                progressBar?.progress = progress
                            }
                        }
                        if(device.session.isNullOrEmpty()
                            && !(!DevTypeHelper.isNas(device.sdvnDevice!!.devClass)
                                    && !DevTypeHelper.isNasByFeature(device.sdvnDevice!!.feature)
                                    && device.sdvnDevice!!.isSelectable)
                            && !checkDeviceInfoToDB(device)){
                            currentGetDataDevice = device
                            viewModel.initApi(device.sdvnDevice!!)
                            viewModel.access(token!!, false)
                            return@Thread
                        }
                        else if(device.session == "-1"){
                            device.session = "0"
                            sharedDevicesError.add(device)
                        }
                    }
                    for (index in 0 until localDevicesNew.size){
                        val device = localDevicesNew[index]
                        val progress = 50 + 50 * (index+sharedDevicesNew.size) / (sharedDevicesNew.size+localDevicesNew.size)
                        if (progressBar?.progress ?: 0 < progress){
                            runOnUiThread {
                                progressBar?.progress = progress
                            }
                        }
                        if(device.session.isNullOrEmpty()
                            && !(!DevTypeHelper.isNas(device.sdvnDevice!!.devClass)
                                    && !DevTypeHelper.isNasByFeature(device.sdvnDevice!!.feature)
                                    && device.sdvnDevice!!.isSelectable)
                            && (!checkDeviceInfoToDB(device) /*|| device.sdvnDevice?.vip == "localhost"*/)){
                            currentGetDataDevice = device
                            viewModel.initApi(device.sdvnDevice!!)
                            viewModel.access(token!!, device.sdvnDevice?.vip == "localhost")
                            return@Thread
                        }
                        else if(device.session == "-1"){
                            device.session = "0"
                            localDevicesError.add(device)
                        }
                    }
                    runOnUiThread {
                        progressBar?.progress = progressBar.max
                        for (device in networks){
                            if(!device.isNewData){
                                networksError.add(device)
                            }
                        }
                        for (device in sharedDevicesNew) {
                            if(!device.isNewData){
                                sharedDevicesError.add(device)
                            }
                        }
                        for (device in localDevicesNew) {
                            if(!device.isNewData){
                                localDevicesError.add(device)
                            }
                        }
                        for (device in networksError){
                            networks.remove(device)
                        }
                        for (device in sharedDevicesError){
                            sharedDevicesNew.remove(device)
                        }
                        for (device in localDevicesError){
                            localDevicesNew.remove(device)
                        }
                        sharedDevicesError.clear()
                        localDevicesError.clear()
                    }
                    refreshDevice()
                    dataGetting = false
                    deviceGetting = false
                    currentGetDataDevice = null
                }
            }catch (e : java.lang.Exception){

            }
        }.start()
    }

    private fun getDeviceData(){
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        Thread{
            try {
                if(viewModel != null
                    && currentGetDataDevice != null
                    && currentGetDataDevice?.sdvnDevice != null
                    && currentGetDataDevice?.session != null)
                {
                    viewModel.initApi(currentGetDataDevice?.sdvnDevice!!)
                    viewModel.getFileList2("/${MOVIE_POSTER}", currentGetDataDevice?.session!!)
                }
            }catch (e:java.lang.Exception){}
        }.start()
    }

    private fun getReadTxt(){
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        if(currentGetDataDevice?.movie_poster_in.isNullOrEmpty()){
            if(myNetworksId.isNotEmpty()){
                getNetworkDevice()
            }
            else{
                getSession()
            }
        }
        else{
            Thread{
                try {
                    viewModel.readTxt(
                        currentGetDataDevice?.session!!,
                        currentGetDataDevice?.movie_poster_in!!
                    )
                } catch (e: java.lang.Exception){

                }

            }.start()
        }
    }

    private var maxGetNetworksSession = 20
    fun liveData(it: BaseViewModel.ValueModel) {
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        when (it.dataType) {
            com.wuji.tv.Constants.ACCESS_LOCAL -> {
                val session = (it.data as AccessResult).session
                if (session.isNullOrEmpty()) {
                    viewModel.access(token!!, true)
                }
                else {
                    if (lastNetworksClickPosition != -1) {
                        isGoNextFragment = true
                        localDevicesOld[0].session = session
                        switchNetwork(networks[lastNetworksClickPosition].sdvnNetworkId!!)
                    }
                    else if(lastSharedClickPosition != -1){
                        localDevicesOld[0].session = session
                        goNextFragment(sharedDevicesOld[lastSharedClickPosition])
                    }
                    else if(lastLocalClickPosition != -1){
                        localDevicesOld[0].session = session
                        goNextFragment(localDevicesOld[lastLocalClickPosition])
                    }
                    else{
                        localDevicesNew[0].session = session
                        getDeviceData()
                    }
                }
            }
            com.wuji.tv.Constants.ACCESS -> {
                val session = (it.data as AccessResult).session
                if (session.isNullOrEmpty()) {
                    if (myNetworksId.isNotEmpty()) {
                        if (--maxGetNetworksSession > 0) {
                            getSession()
                        } else {
                            maxGetNetworksSession = 20
                            networks.remove(currentGetDataDevice)
                            getNetworkDevice()
                        }
                    } else {
                        currentGetDataDevice?.session = "-1"
                        getSession()
                    }
                } else {
                    currentGetDataDevice?.session = session
                    getDeviceData()
                }
            }
            com.wuji.tv.Constants.GET_REMOTE_FILE_LIST -> {
                currentGetDataDevice?.isGetData = true
                val list = it.data as ArrayList<MyFile>
                if (list.isEmpty()) {
                    if (myNetworksId.isNotEmpty()) {
                        if (it.isException) {
                            getDeviceData()
                        } else {
                            currentGetDataDevice?.hasPoster = false
                            downloadImg()
                        }
                    } else {
                        currentGetDataDevice?.hasPoster = false
                        updateDeviceInfo()
                        getSession()
                    }
                } else {
                    var hasMenu = false
                    var hasMovie = false
                    for (file in list) {
                        if (file.ftype == "dir") {
                            if (file.name.equals(MOVIE_POSTER_MENU, true)) {
                                hasMenu = true
                            } else if (file.name.equals(MOVIE_POSTER_MOVIE, true)) {
                                hasMovie = true
                            }
                        } else if (file.ftype == "txt" && FileUtils.getFileNoExtension2(file.name)
                                .equals(
                                    MOVIE_POSTER_IN, true
                                )
                        ) {
                            currentGetDataDevice?.movie_poster_in = file.path
                        } else if (file.ftype == "pic") {
                            if (FileUtils.getFileNoExtension2(file.name).equals(
                                    MOVIE_POSTER_BG, true
                                )
                            ) {
                                currentGetDataDevice?.movie_poster_bg = file.path
                            }
                            else if (FileUtils.getFileNoExtension2(file.name).equals(
                                    MOVIE_POSTER_COVER, true
                                )
                            ) {
                                currentGetDataDevice?.movie_poster_cover = file.path
                            }
                            else if (FileUtils.getFileNoExtension2(file.name).equals(
                                    MOVIE_POSTER_LOGO, true
                                )
                            ) {
                                currentGetDataDevice?.movie_poster_logo = file.path
                            }
                        }
                    }
                    if (!hasMenu || !hasMovie) {
                        currentGetDataDevice?.hasPoster = false
                    }
                    getReadTxt()
                }
            }
            com.wuji.tv.Constants.TXT_RESULT -> {
                val context = (it.data as TxtResult).context
                if (!context.isNullOrEmpty()) {
                    val list = context.split("\r\n")
                    if (list.size == 5) {
                        currentGetDataDevice?.name = list[0]
                        currentGetDataDevice?.plot = list[1]
                        currentGetDataDevice?.updateTime = Date().time.toString()
                        currentGetDataDevice?.type = list[3]
                        //currentGetDataDevice?.newNumber = list[4]
                    }
                }
                if (myNetworksId.isNotEmpty()) {
                    downloadImg()
                } else {
                    updateDeviceInfo()
                    getSession()
                }
            }
            com.wuji.tv.Constants.ERROR -> {
                val exception = it.data as Exception
                toast(exception.message!!)
            }
        }
    }

    private fun updateDeviceInfo(){
        Thread{
            synchronized (this){
                currentGetDataDevice?.let { it ->
                    val device =  DeviceInfoModel(
                        it.sdvnDevice!!.id,
                        it.sdvnNetworkId?:"",
                        it.sdvnDevice!!.vip,
                        it.sdvnDevice!!.userid,
                        it.sdvnDevice!!.devClass,
                        it.sdvnDevice!!.isSelectable,
                        it.sdvnDevice!!.name,
                        it.name,
                        it.plot,
                        it.price,
                        it.updateTime,
                        it.type,
                        it.movie_poster_bg,
                        it.movie_poster_cover,
                        it.movie_poster_logo,
                        ""
                    )
                    AppDatabase.getInstance(App.app.applicationContext).getDeviceInfoDao().delete(device.deviceId)
                    AppDatabase.getInstance(App.app.applicationContext).getDeviceInfoDao().insert(device)
                }
            }
        }.start()
    }

    private fun checkDeviceInfoEN() : Boolean{
        val deviceInfoModel =
            AppDatabase.getInstance(App.app.applicationContext).getDeviceInfoDao()
                .getDeviceInfoNetworkId(currentGetDataDevice?.sdvnNetworkId!!)
        return if(deviceInfoModel == null){
            false
        } else{
            currentGetDataDevice?.sdvnDevice = SDVNDevice()
            currentGetDataDevice?.movie_poster_bg = deviceInfoModel.movie_poster_bg
            currentGetDataDevice?.movie_poster_cover = deviceInfoModel.movie_poster_cover
            currentGetDataDevice?.movie_poster_logo = deviceInfoModel.movie_poster_logo
            currentGetDataDevice?.name = deviceInfoModel.name
            currentGetDataDevice?.plot = deviceInfoModel.plot
            currentGetDataDevice?.updateTime = deviceInfoModel.updateTime
            currentGetDataDevice?.type = deviceInfoModel.type
            currentGetDataDevice?.price = deviceInfoModel.price
            currentGetDataDevice?.sdvnNetworkId = deviceInfoModel.networkId
            currentGetDataDevice?.sdvnDevice?.id = deviceInfoModel.deviceId
            currentGetDataDevice?.sdvnDevice?.userid = deviceInfoModel.userid
            currentGetDataDevice?.sdvnDevice?.vip = ""//deviceInfoModel.vip
            currentGetDataDevice?.sdvnDevice?.devClass = deviceInfoModel.devClass
            currentGetDataDevice?.sdvnDevice?.isSelectable = deviceInfoModel.isSelectable
            currentGetDataDevice?.sdvnDevice?.name = deviceInfoModel.sdvnName
            true
        }
    }

    private fun checkDeviceInfoToDB(device: RemoteDeviceModel) : Boolean{
        //"checkDeviceInfoToDB deviceId:${device.sdvnDevice?.id ?: ""}".log("DeviceFragment")
        device.sdvnDevice?.id ?: return false
        val deviceInfoModel =
            AppDatabase.getInstance(App.app.applicationContext).getDeviceInfoDao()
                .getDeviceInfo(device.sdvnDevice!!.id)
        return if(deviceInfoModel == null){
            false
        } else{
            device.movie_poster_bg = deviceInfoModel.movie_poster_bg
            device.movie_poster_cover = deviceInfoModel.movie_poster_cover
            device.movie_poster_logo = deviceInfoModel.movie_poster_logo
            device.name = deviceInfoModel.name
            device.plot = deviceInfoModel.plot
            device.price = deviceInfoModel.price
            device.updateTime = deviceInfoModel.updateTime
            device.type = deviceInfoModel.type
            true
        }
    }

    private fun downloadImg(){
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        val ftPathBg = "${currentGetDataDevice?.sdvnDevice!!.id}${currentGetDataDevice?.movie_poster_bg!!}".replace(
            "/",
            "-"
        )
        val ftPathLogo = "${currentGetDataDevice?.sdvnDevice!!.id}${currentGetDataDevice?.movie_poster_logo!!}".replace(
            "/",
            "-"
        )
        val ftPathCover = "${currentGetDataDevice?.sdvnDevice!!.id}${currentGetDataDevice?.movie_poster_cover!!}".replace(
            "/",
            "-"
        )
        val file = "${App.app.applicationContext.filesDir?.path}/images/device/${currentGetDataDevice?.sdvnDevice?.id}"
        if(!currentGetDataDevice?.movie_poster_bg.isNullOrEmpty()
            && !File(file, ftPathBg).exists()) {
            BitmapUtils().downloadImg(
                App.app.applicationContext, currentGetDataDevice?.movie_poster_bg!!,
                file,
                currentGetDataDevice?.session!!, currentGetDataDevice?.sdvnDevice!!.vip,
                currentGetDataDevice?.sdvnDevice!!.id, this
            )
        }
        else if(!currentGetDataDevice?.movie_poster_logo.isNullOrEmpty()
            && !File(file, ftPathLogo).exists()){
            BitmapUtils().downloadImg(
                App.app.applicationContext, currentGetDataDevice?.movie_poster_logo!!,
                file,
                currentGetDataDevice?.session!!, currentGetDataDevice?.sdvnDevice!!.vip,
                currentGetDataDevice?.sdvnDevice!!.id, this
            )
        }
        else if(!currentGetDataDevice?.movie_poster_cover.isNullOrEmpty()
            && !File(file, ftPathCover).exists()){
            BitmapUtils().downloadImg(
                App.app.applicationContext, currentGetDataDevice?.movie_poster_cover!!,
                file,
                currentGetDataDevice?.session!!, currentGetDataDevice?.sdvnDevice!!.vip,
                currentGetDataDevice?.sdvnDevice!!.id, this
            )
        }
        else {
            updateDeviceInfo()
            getNetworkDevice()
        }
    }

    override fun downloadListener(isSuccess: Boolean) {
        if(onStop) return
        if(layoutProgress.visibility == View.VISIBLE) return
        downloadImg()
    }

    private fun setDeviceNameStart(type: Int){
        handler.postDelayed(Runnable {
//            deviceOnFocusChange(type)
            smoothScrollTo(type)
        }, 100)
    }

    private fun smoothScrollToUp(){
        var type = currentType
        if(currentType > 0 && rvNetwork?.visibility == View.VISIBLE){
            type = 0
        }
        if(currentType > 1 && rvSharedDevice?.visibility == View.VISIBLE){
            type = 1
        }
        if(currentType > 2 && rvLocalDevice?.visibility == View.VISIBLE){
            type = 2
        }
        if(type != currentType){
            smoothScrollTo(type)
        }
    }

    private fun smoothScrollToDown(){
        var type = currentType
        if(currentType < 3){
            type = 3
        }
        if(currentType < 2 && rvLocalDevice?.visibility == View.VISIBLE){
            type = 2
        }
        if(currentType < 1 && rvSharedDevice?.visibility == View.VISIBLE){
            type = 1
        }
        if(type != currentType){
            smoothScrollTo(type)
        }
    }

    private var currentType = 0
    private fun smoothScrollTo(type: Int){
        var y = 0
        if(type > 0 && rvNetwork?.visibility == View.VISIBLE){
            y += layoutNetwork.height
            y += rvNetwork.height
        }
        if(type > 1 && rvSharedDevice?.visibility == View.VISIBLE){
            y += layoutShared.height
            y += rvSharedDevice.height
        }
        if(type > 2 && rvLocalDevice?.visibility == View.VISIBLE){
            y += layoutLocal.height
            y += rvLocalDevice.height
        }
        currentType = type
        scrollViewDevices?.smoothScrollTo(0, y)

        Handler().postDelayed({
            val lm =
                when(type){
                    0 -> networksLayoutManager
                    1 -> sharedDeviceLayoutManager
                    2 -> localDeviceLayoutManager
                    else -> managerDeviceLayoutManager
                }
            val position =
                when(type){
                    0 -> networksAdapter.getLastFocusPosition()
                    1 -> sharedDeviceAdapter.getLastFocusPosition()
                    2 -> localDeviceAdapter.getLastFocusPosition()
                    else -> managerDeviceAdapter.getLastFocusPosition()
                }
            lm.findViewByPosition(position)?.apply { requestFocus() }
        },100)
    }

    private fun deviceOnFocusChange(type: Int){
        when(type){
            0 -> {
                if (rvNetwork == null) return
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    && networksAdapter.getLastFocusPosition() > 2
                    && networksAdapter.getLastFocusPosition() < networksAdapter.itemCount-1){
                    rvNetwork.smoothScrollBy(420, 0)
                }
                val index = "(${networksAdapter.getLastFocusPosition() + 1}/${networksAdapter.itemCount})"
                tvDeviceIndex.text = index
                tvNetworkIndex.text = index
                if (networksAdapter.getLastFocusPosition() < networksAdapter.getDatas().size) {
                    val device = networksAdapter.getDatas()[networksAdapter.getLastFocusPosition()]
                    refreshPlotInfo(device)
                    refreshImg(device)
                }
            }
            1 -> {
                if (rvSharedDevice == null) return
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    && sharedDeviceAdapter.getLastFocusPosition() > 2
                    && sharedDeviceAdapter.getLastFocusPosition() < sharedDeviceAdapter.itemCount-1){
                    rvSharedDevice.smoothScrollBy(420, 0)
                }
                val index = "(${sharedDeviceAdapter.getLastFocusPosition() + 1}/${sharedDeviceAdapter.itemCount})"
                tvDeviceIndex.text = index
                tvSharedIndex.text = index
                val data = sharedDeviceAdapter.getDatas()[sharedDeviceAdapter.getLastFocusPosition()]
                refreshPlotInfo(data)
                refreshImg(data)
            }
            2 -> {
                if (rvLocalDevice == null) return
                val index = "(${localDeviceAdapter.getLastFocusPosition() + 1}/${localDeviceAdapter.itemCount})"
                tvDeviceIndex.text = index
                tvLocalIndex.text = index
                val device = localDeviceAdapter.getDatas()[localDeviceAdapter.getLastFocusPosition()]
                refreshPlotInfo(device)
                refreshImg(device)
                if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    && localDeviceAdapter.getLastFocusPosition() > 2
                    && localDeviceAdapter.getLastFocusPosition() < localDeviceAdapter.itemCount-1){
                    rvLocalDevice.smoothScrollBy(420, 0)
                }
            }
            3 -> {
                if (rvManager == null) return
                val device = managerDeviceAdapter.getDatas()[managerDeviceAdapter.getLastFocusPosition()]
                tvDeviceName.text = device.sdvnDevice?.name
                tvDevicePlot.text = ""
                tvDevicePrice.text = ""
                tvDeviceType.text = device.type
                val index = "(${managerDeviceAdapter.getLastFocusPosition() + 1}/${managerDeviceAdapter.itemCount})"
                tvDeviceIndex.text = index
                tvManagerIndex.text = index
                homeBgImage.setImageResource(R.mipmap.bg_home)
                homeLogoImage.setImageResource(R.drawable.bg_video_unselected)
            }
        }
    }

    private fun refreshPlotInfo(data: RemoteDeviceModel)
    {
        val name = if(data.name.isNullOrEmpty()) data.sdvnDevice?.name!! else data.name
        try {
            tvDeviceName?.text = name
            val plot = if(data.plot.isNullOrEmpty()) resources.getString(R.string.plot_home_default) else data.plot
            tvDevicePlot?.text = "${getString(R.string.plot_home)}$plot"
            tvDevicePrice?.text = "${getString(R.string.flow_unit_price)}${data.price}"
            tvDeviceType?.text = if(data.type.isNullOrEmpty()) resources.getString(R.string.type_default) else data.type
        }catch (e: java.lang.Exception){

        }
    }

    private var deviceOld: RemoteDeviceModel? = null
    private val mHandler = Handler()
    private val refreshImgRunnable = Runnable{
        deviceOld?.apply {
            BitmapUtils().loadingAndSaveImgAddAnimate(
                movie_poster_bg, homeBgImage,homeBgImageUp,
                "${App.app.applicationContext.filesDir?.path}/images/device/${sdvnDevice?.id}",
                session, sdvnDevice!!.vip,
                sdvnDevice!!.id, App.app.applicationContext, errorImgId = R.mipmap.bg_home,isThumbnail = false
            )
        }
    }
    private fun refreshImg(device: RemoteDeviceModel)
    {
        deviceOld = device
        mHandler.removeCallbacks(refreshImgRunnable)
        mHandler.postDelayed(refreshImgRunnable,500)
    }

    private fun refreshDevice()
    {
        if(onStop) return
        runOnUiThread {
            try {
                showData()
                if(networks.isEmpty()){
                    layoutNetwork.visibility = View.GONE
                    rvNetwork.visibility = View.GONE
                }
                else{
                    networks.sortBy { it.name }
                    if(rvNetwork.visibility == View.GONE){
                        layoutNetwork.visibility = View.VISIBLE
                        rvNetwork.visibility = View.VISIBLE
                        networksSize = networks.size
                        networksAdapter.setDatasAndRefresh(networks)
                        rvNetwork.post {
                            networksLayoutManager.findViewByPosition(0)?.requestFocus()
                        }
                        tvNetworkIndex.text = "(${networks.size})"
                    }
                    else if(networksSize == networks.size){
                        networksAdapter.setDatas(networks)
                    }
                    else{
                        networksSize = networks.size
                        networksAdapter.setDatasAndRefresh(networks)
                    }
                }
                if(sharedDevicesNew.isEmpty()){
                    sharedDevicesOld.clear()
                    sharedDeviceAdapter.setDatasAndRefresh(sharedDevicesOld)
                    layoutShared.visibility = View.GONE
                    rvSharedDevice.visibility = View.GONE
                }
                else{
                    sharedDevicesNew.sortBy { it.name }
                    if(rvSharedDevice.visibility == View.GONE){
                        layoutShared.visibility = View.VISIBLE
                        rvSharedDevice.visibility = View.VISIBLE
                        sharedDevicesOld.clear()
                        sharedDevicesOld.addAll(sharedDevicesNew)
                        sharedDeviceAdapter.setDatasAndRefresh(sharedDevicesOld)
                        if(networks.isEmpty()){
                            rvSharedDevice.post {
                                sharedDeviceLayoutManager.findViewByPosition(0)?.requestFocus()
                            }
                        }
                    }
                    else if(sharedDevicesOld.size == sharedDevicesNew.size){
                        for(i in 0 until sharedDevicesOld.size){
                            if(sharedDevicesOld[i].session != sharedDevicesNew[i].session){
                                sharedDevicesOld.clear()
                                sharedDevicesOld.addAll(sharedDevicesNew)
                                sharedDeviceAdapter.setDatasAndRefresh(sharedDevicesOld)
                                break
                            }
                        }
                    }
                    else{
                        if(sharedDeviceAdapter.getLastFocusPosition() >= sharedDevicesNew.size){
                            sharedDeviceAdapter.setLastFocusPosition(0)
                        }
                        sharedDevicesOld.clear()
                        sharedDevicesOld.addAll(sharedDevicesNew)
                        sharedDeviceAdapter.setDatasAndRefresh(sharedDevicesOld)
                    }
                    tvSharedIndex.text = "(${sharedDevicesOld.size})"
                }
                if(localDevicesNew.isEmpty()){
                    layoutLocal.visibility = View.GONE
                    rvLocalDevice.visibility = View.GONE
                }
                else{
                    localDevicesNew.sortBy { it.name }
                    if(rvLocalDevice.visibility == View.GONE){
                        layoutLocal.visibility = View.VISIBLE
                        rvLocalDevice.visibility = View.VISIBLE
                        localDevicesOld.clear()
                        localDevicesOld.addAll(localDevicesNew)
                        localDeviceAdapter.setDatasAndRefresh(localDevicesOld)
                        if(networks.isEmpty() && sharedDevicesNew.isEmpty()){
                            rvLocalDevice.post {
                                localDeviceLayoutManager.findViewByPosition(0)?.requestFocus()
                            }
                        }
                    }
                    else if(localDevicesOld.size == localDevicesNew.size){
                        for(i in 0 until localDevicesOld.size){
                            if(localDevicesOld[i].session != localDevicesNew[i].session){
                                localDevicesOld.clear()
                                localDevicesOld.addAll(localDevicesNew)
                                localDeviceAdapter.setDatasAndRefresh(localDevicesOld)
                                break
                            }
                        }
                    }
                    else{
                        if(localDeviceAdapter.getLastFocusPosition() >= localDevicesNew.size){
                            localDeviceAdapter.setLastFocusPosition(0)
                        }
                        localDevicesOld.clear()
                        localDevicesOld.addAll(localDevicesNew)
                        localDeviceAdapter.setDatasAndRefresh(localDevicesOld)
                    }
                    tvLocalIndex.text = "(${localDevicesOld.size})"
                }
                if(rvManager.visibility == View.GONE){
                    layoutManager.visibility = View.VISIBLE
                    rvManager.visibility = View.VISIBLE
                    managerDeviceAdapter.setDatasAndRefresh(initManagerDevice())
                    if(networks.isEmpty() && sharedDevicesNew.isEmpty() && localDevicesNew.isEmpty()){
                        rvManager.post {
                            managerDeviceLayoutManager.findViewByPosition(0)?.requestFocus()
                        }
                    }
                    tvManagerIndex.text = "(${4})"
                }
                tvDeviceTitle?.show()
            } catch (e: java.lang.Exception){

            }
        }
        hideProgressBar()
    }

    private fun goNextFragment(remoteDevice: RemoteDeviceModel?){
        remoteDevice?.apply {
                    viewModel.initApi(sdvnDevice!!)
                        val bundle = Bundle()
                        val s = PosterBundleModel(
                            token!!,
                            localDevicesOld[0].session,
                            sdvnDevice?.vip!!,
                            sdvnDevice?.id!!,
                            sdvnDevice?.name!!
                        )
                        bundle.putSerializable(PosterFragment.KEY_BUNDLE_DEVICE, s)
                        findNavController().navigate(R.id.posterFragment, bundle)
        }
    }

    private fun showSelectSNDialog(_id: String, isSharedDevice: Boolean){
        val selectedSn = SDVNApi.getInstance().selectedSn(_id)
        var message = getString(R.string.net_normal_hint)
        var id = ""
        if (!selectedSn) {
            id = _id
            message = getString(R.string.net_jiedian_hint)
        }
        val selfDialog = SelfDialog(context)
        selfDialog.setMessage(message)
        selfDialog.setCenterGravity(Gravity.CENTER)
        selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
            selfDialog.dismiss()
        }
        selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
            selectSmartNode(id,isSharedDevice)
            selfDialog.dismiss()
        }
        selfDialog.show()
    }

    private fun selectSmartNode(id: String, isSharedDevice: Boolean){
        SDVNApi.getInstance()
            .selectSmartNode(id, object : ResultListener {
                override fun onSuccess() {
                    runOnUiThread {
                        if (id == "") {
                            toast(getString(R.string.net_normal))
                        } else {
                            toast(getString(R.string.net_jiedian))
                        }
                        if(isSharedDevice){
                            sharedDeviceAdapter.notifyItemChanged(sharedDeviceAdapter.getLastFocusPosition())
                            if (localDeviceAdapter.mLastSelectedSn != -1){
                                localDeviceAdapter.notifyItemChanged(localDeviceAdapter.mLastSelectedSn)
                            }
                        }
                        else{
                            localDeviceAdapter.notifyItemChanged(localDeviceAdapter.getLastFocusPosition())
                            if (sharedDeviceAdapter.mLastSelectedSn != -1){
                                sharedDeviceAdapter.notifyItemChanged(sharedDeviceAdapter.mLastSelectedSn)
                            }
                        }
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                }
            })
    }

    private fun showError(s: String?) {
        runOnUiThread {
            tvError?.show()
            tvError?.text = s
        }
    }

    private fun showData() {
        runOnUiThread {
            tvError?.remove()
        }
    }

    private fun initManagerDevice(): ArrayList<RemoteDeviceModel> {
        val devices = ArrayList<RemoteDeviceModel>()
        val downloading = SDVNDevice()
        downloading.name = getString(R.string.dowloading)
        downloading.owner = getString(R.string.downloading_all_ex)
        devices.add(
            RemoteDeviceModel(
                getString(R.string.download_manager),
                false,
                downloading,
                managerResId = R.mipmap.ic_manager_dowloading,
                type = "查看正在下载的任务列表"
            )
        )
        val downloaded = SDVNDevice()
        downloaded.name = getString(R.string.downloaded)
        downloaded.owner = getString(R.string.all_downloaded_file)
        devices.add(
            RemoteDeviceModel(
                getString(R.string.download_manager),
                false,
                downloaded,
                managerResId = R.mipmap.ic_manager_dowloaded,
                type = "查看已经下载完成的资源"
            )
        )
        val localStorage = SDVNDevice()
        localStorage.name = getString(R.string.local_storage)
        localStorage.owner = getString(R.string.local_storage)
        devices.add(
            RemoteDeviceModel(
                getString(R.string.download_manager),
                false,
                localStorage,
                managerResId = R.mipmap.ic_manager_storage,
                type = "直接访问本地存储设备和资源"
            )
        )
        val runApp = SDVNDevice()
        runApp.name = getString(R.string.run_app)
        runApp.owner = getString(R.string.run_app)
        devices.add(
            RemoteDeviceModel(
                getString(R.string.download_manager),
                false,
                runApp,
                managerResId = R.mipmap.ic_manager_run_app,
                type = "应用内快捷运行本机已安装的APP应用"
            )
        )
        return devices
    }

    private fun initLocalDevice() {
        SDVNApi.getInstance().getSelf()?.apply {
            localDevicesNew.clear()
            this.vip = "localhost"
            localDevicesNew.add(RemoteDeviceModel(getString(R.string.local_device), false, this))
        }
    }

    private fun checkUpdate() {
        doAsync {
            val i = Random().nextInt()
            val response =
                OkHttpUtils.get().url("${com.wuji.tv.Constants.APP_UPDATE_URI}?=$i")
                    .build().execute()
            if (response.isSuccessful) {
                val gson = Gson()
                val updateData = gson.fromJson(response.body()?.string(), UpdateData::class.java)
                val versionCode = BuildConfig.VERSION_CODE
                if (updateData.newVersion > versionCode) {
                    OkHttpUtils.get().url(updateData.appDownloadUrl).build()
                        .execute(object :
                            FileCallBack(
                                App.app.applicationContext.filesDir.absolutePath,
                                "xxx.apk"
                            ) {
                            override fun onResponse(file: File?, id: Int) {
                                "file"
                                file?.apply {
                                    val dialog = AlertDialog.Builder(App.app.applicationContext)
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
                App.app.applicationContext,
                "com.wuji.tv.provider",
                file
            )
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun showLoading() {
    }
    fun mShowProgressBar() {
        runOnUiThread {
            layoutProgressBar.visibility = View.VISIBLE
        }
    }
    fun mShowLoading() {
        runOnUiThread {
            if (!loadingDialog?.isShowing!!) {
                loadingDialog?.show()
            }
        }
    }

    override fun hideLoading() {
    }
    fun hideProgressBar() {
        runOnUiThread {
            try {
                layoutProgressBar.visibility = View.GONE
            } catch (e: Exception){

            }
        }
    }
    fun mHideLoading() {
        runOnUiThread {
            try {
                if (loadingDialog?.isShowing!!) {
                    loadingDialog?.dismiss()
                }
            } catch (e: Exception){

            }
        }
    }


    private var keyCode = -1
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if(event.action == KeyEvent.ACTION_UP){
            return false
        }
        keyCode = event.keyCode
        when(event.keyCode){
            KeyEvent.KEYCODE_BACK -> {
                if (layoutProgress.visibility == View.VISIBLE) {
                    layoutProgress.visibility = View.GONE
                    initFocus()
                    return true
                }
                val selfDialog = SelfDialog(context)
                selfDialog.setMessage(getString(R.string.exit_hint))
                selfDialog.setCenterGravity(Gravity.CENTER)
                selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
                    selfDialog.dismiss()
                }
                selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
                    (activity as MainActivity).finish()
                    selfDialog.dismiss()
                }
                selfDialog.show()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if ((rvProgressNetwork.hasFocus())) {
                    return rvProgressNetwork.getLastFocusPosition() == 0
                }
                else{
                    smoothScrollToUp()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (rvManager.hasFocus()
                ) {
                    return true
                }
                else if ((rvProgressNetwork.hasFocus())) {
                    return rvProgressNetwork.getLastFocusPosition() == progressNetworkAdapter.itemCount - 1
                }
                else{
                    smoothScrollToDown()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (layoutProgress.visibility == View.VISIBLE) {
                    return true
                }
                else if ((rvNetwork.hasFocus()
                            && networksAdapter.getLastFocusPosition() == 0)
                    ||(rvSharedDevice.hasFocus()
                            && sharedDeviceAdapter.getLastFocusPosition() == 0)
                    || (rvLocalDevice.hasFocus()
                            && localDeviceAdapter.getLastFocusPosition() == 0)
                    || (rvManager.hasFocus()
                            && managerDeviceAdapter.getLastFocusPosition() == 0)
                ) {
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (layoutProgress.visibility == View.VISIBLE) {
                    return true
                }
                else if ((rvNetwork.hasFocus()
                            && networksAdapter.getLastFocusPosition() == networksAdapter.itemCount - 1)
                    ||(rvSharedDevice.hasFocus()
                            && sharedDeviceAdapter.getLastFocusPosition() == sharedDeviceAdapter.itemCount - 1)
                    || (rvLocalDevice.hasFocus()
                            && localDeviceAdapter.getLastFocusPosition() == localDeviceAdapter.itemCount - 1)
                    || (rvManager.hasFocus()
                            && managerDeviceAdapter.getLastFocusPosition() == managerDeviceAdapter.itemCount - 1)
                ) {
                    return true
                }
            }
        }
        return false
    }

    override fun getApplicationContext(): Context {
        return App.app.applicationContext
    }
    override fun getLayoutID(): Int = R.layout.fragment_devices
}