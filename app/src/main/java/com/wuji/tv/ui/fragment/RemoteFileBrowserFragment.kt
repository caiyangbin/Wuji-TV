package com.wuji.tv.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.common.navigationDecoration
import com.wuji.tv.common.posterDecoration
import com.wuji.tv.model.MyFile
import com.wuji.tv.model.NavigationModel
import com.wuji.tv.model.RvPositionModel
import com.wuji.tv.ui.adapter.DeviceNavigationAdapter
import com.wuji.tv.ui.adapter.FilesGridAdapter
import com.wuji.tv.ui.adapter.FilesPosterAdapter
import com.wuji.tv.ui.adapter.RemoteFileListAdapter
import com.wuji.tv.viewmodels.DevicesViewModel
import com.wuji.tv.widget.SelfDialog
import kotlinx.android.synthetic.main.fragment_file_browser.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.ext.android.inject
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


const val FLAG_COME_POSTER_FRAGMENT = 0
const val FLAG_COME_DETAIL_FRAGMENT = 1

class RemoteFileBrowserFragment : com.wuji.tv.common.BaseFragment() {

    private val viewModel by inject<DevicesViewModel>()
    private var deviceName: String = ""
    private var session: String = ""
    private var sessionLocal: String = ""
    private var downloadPath: String = ""
    private var bt_ticket: String = ""
    private var comeState: Int = FLAG_COME_POSTER_FRAGMENT

    private lateinit var navigationAdapter: DeviceNavigationAdapter
    private lateinit var navigationLayoutManager: LinearLayoutManager

    private lateinit var adapterForListRemote: RemoteFileListAdapter
    private lateinit var layoutManagerForList: LinearLayoutManager

    private lateinit var adapterForGrid: FilesGridAdapter
    private lateinit var layoutManagerForGrid: GridLayoutManager

    private lateinit var adapterForPoster: FilesPosterAdapter
    private lateinit var layoutManagerForPoster: GridLayoutManager


//    private var currentPath: String = "/"
    private var currentNavigationPosition = 0
    private var currentShowListType = -1

    private val filesPositionStack = Stack<RvPositionModel>()
    private var filesLastOffset = 0
    private var filesLastPosition = 0
    private var filesCurrentPosition = 0
    private var popStack: RvPositionModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(comeState == FLAG_COME_POSTER_FRAGMENT && !downloadPath.isNullOrEmpty()){
            getFileList(downloadPath)
            downloadPath = ""
        }
        else{
//            MainFragment.isRootFragment = false
            initView()
            initData()
            initEvent()
        }
    }

    private fun initView() {

        navigationAdapter = DeviceNavigationAdapter(null)
        navigationLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvNavigation.adapter = navigationAdapter
        rvNavigation.layoutManager = navigationLayoutManager
        rvNavigation.addItemDecoration(navigationDecoration)
        rvNavigation.remove()

        adapterForListRemote = RemoteFileListAdapter(null)
        layoutManagerForList = LinearLayoutManager(activity)
        rvFiles.adapter = adapterForListRemote
        rvFiles.layoutManager = layoutManagerForList

        adapterForGrid = FilesGridAdapter(null)
        layoutManagerForGrid = GridLayoutManager(activity, 5)
        rvFilesForGrid.adapter = adapterForGrid
        rvFilesForGrid.layoutManager = layoutManagerForGrid

        adapterForPoster = FilesPosterAdapter(null)
        layoutManagerForPoster = GridLayoutManager(activity, 6)
        rvFilesForPoster.adapter = adapterForPoster
        rvFilesForPoster.layoutManager = layoutManagerForPoster
        rvFilesForPoster.addItemDecoration(posterDecoration)

        rvFiles.show()
        rvFilesForGrid.remove()
        rvFilesForPoster.remove()

    }

    private fun initData() {
        ArrayList<NavigationModel>().apply {
            add(NavigationModel("列表"))
            add(NavigationModel("海报"))
            add(NavigationModel("九宫格"))
            navigationAdapter.setDatasAndRefresh(this)
        }

        viewModel.downloadLiveData.observe(this, Observer {
            if (it) {
                toast("开始下载")
            } else {
                toast("启动下载失败")
            }
        })

        viewModel.liveData.observe(this, Observer {
            when (it.dataType) {
                com.wuji.tv.Constants.GET_REMOTE_FILE_LIST -> {
                    val mList = it.data as ArrayList<MyFile>
                    val list = arrayListOf<MyFile>()
                    rvFiles.show()
                    tvError.remove()
                    if(downloadPath.isNotEmpty()){
                        for (l in mList){
                            if (l.ftype == "dir"){
                                list.add(l)
                            }
                        }
                    }
                    else{
                        list.addAll(mList)
                    }
                    if(adapterForListRemote.itemCount == 0 && list.size <= 1){
                        rvFiles.remove()
                        tvError.show()
                        tvError.text = getString(R.string.meiyouyinpan)
                        return@Observer
                    }
                    adapterForListRemote.setDatasAndRefresh(list)
                    popStack?.apply {
                        layoutManagerForList.scrollToPositionWithOffset(offsetPosition, offset)
                        rvFiles.post {
                            layoutManagerForList.findViewByPosition(position)?.apply {
                                requestFocusFromTouch()
                                requestFocus()
                            }
                        }
                        popStack = null
                    } ?: run {
                        layoutManagerForList.scrollToPositionWithOffset(0, 0)
                        rvFiles.post {
                            layoutManagerForList.findViewByPosition(0)?.apply {
                                requestFocusFromTouch()
                                requestFocus()
                            }
                        }
                    }
                }
                com.wuji.tv.Constants.DOWNLOAD_RESULT -> {
                    val isSuccess = it.data as String
                    if (isSuccess == "0") {
                        toast("开始下载")
                        //goTODownloadingFragment()
                        if(comeState == FLAG_COME_DETAIL_FRAGMENT){
                            findNavController().popBackStack(R.id.detailFragment,false)
                        }
                    } else {
                        toast(isSuccess)
                    }
                }
                com.wuji.tv.Constants.ERROR -> {
                    rvFiles.remove()
                    tvError.show()
                    tvError.text = "暂无数据"
                }
            }
        })

        deviceName = arguments?.getString("deviceName") ?: ""
        session = arguments?.getString("session")!!
        sessionLocal = arguments?.getString("session_local") ?: ""
        downloadPath = arguments?.getString("download_path") ?: ""
        bt_ticket = arguments?.getString("bt_ticket") ?: ""
        comeState = arguments?.getInt("comeState")!!
        "download  downloadPath:${downloadPath}".log("RemoteFileBrowserFragment")
        "download  bt_ticket:${bt_ticket}".log("RemoteFileBrowserFragment")
        filesPositionStack.push(RvPositionModel(0, filesLastPosition, filesLastOffset, "/"))
        getFileList("/")
    }

    private fun goTODownloadingFragment(){
        val goBackState =
            if(comeState == FLAG_COME_DETAIL_FRAGMENT)
                DOWNLOADING_FRAGMENT_GO_BACK_DETAIL
            else
                DOWNLOADING_FRAGMENT_GO_BACK
        if(App.isCircle){
            findNavController().navigate(R.id.downloadingFragment,Bundle().apply {
                putInt("goBackState",goBackState)
                putString("networkId", App.networkId)
            })
        }
        else{
            findNavController().navigate(R.id.downloadingFragment,Bundle().apply {
                putInt("goBackState",goBackState)
            })
        }
    }

    private fun initEvent() {
        adapterForListRemote.setOnItemClickListener { _, position, myFile ->
            if (position == 0) {
                toParent()
                return@setOnItemClickListener
            }
            "myFile=${myFile?.ftype}--${myFile?.path}".log()
            when (myFile?.ftype) {
                "video" -> {
                    val encode = URLEncoder.encode(myFile.path, "utf-8")
                    val url =
                        "http://${App.ip}:9898/file/download?session=$session&path=${encode}&share_path_type=2"
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(url), "video/*")
                        startActivity(this)
                    }
                }
                "dir" -> {
                    toDir(myFile.path, position)
                }
                "pic" -> {
                    val encode = URLEncoder.encode(myFile.path, "utf-8")
                    val url =
                        "http://${App.ip}:9898/file/download?session=$session&path=${encode}&share_path_type=2"
                    "url=$url".trim().log()
                    Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(url), "image/*")
                        startActivity(this)
                    }
                }
                "txt" -> {
                }
                "audio" -> {
                }
                "file" -> {
                }
            }
        }

        adapterForGrid.setOnItemClickListener { view, position, myFile ->

            if ("video" == myFile?.ftype) {
                val encode = URLEncoder.encode(myFile.path, "utf-8")
                val url =
                    "http://${App.ip}:9898/file/download?session=$session&path=${encode}&share_path_type=2"
                "url=$url".trim().log()
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(url), "video/*")
                    startActivity(this)
                }

                return@setOnItemClickListener
            }
            if (position == 0) {
                toParent()
            } else {
                toDir(myFile!!.path, position)
            }
        }

        rvFiles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                getPositionAndOffset()
            }
        })
        navigationAdapter.setOnItemSelectListener { _, position, _ ->
            currentShowListType = position
            when (position) {
                0 -> {
                    rvFiles.show()
                    rvFilesForGrid.remove()
                    rvFilesForPoster.remove()
                }
                1 -> {
                    rvFiles.remove()
                    rvFilesForGrid.remove()
                    rvFilesForPoster.show()
                }
                2 -> {
                    rvFiles.remove()
                    rvFilesForGrid.show()
                    rvFilesForPoster.remove()
                }
            }
        }

        navigationAdapter.setOnItemKeyListener { view, keyCode, keyEvent, _, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (position == 0) {
                            return@setOnItemKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (position == navigationAdapter.itemCount - 1) {
                            return@setOnItemKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        currentNavigationPosition = position
                        view.post {
                            view.setBackgroundResource(R.drawable.shape_round_corner_item_navigation_selected_bg)
                        }
                        rvNavigation.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    }
                }
            }
            return@setOnItemKeyListener false
        }

        adapterForGrid.setOnItemKeyListener { view, keyCode, keyEvent, _, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (position < 5) {
                            rvNavigation.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                            view.post {
                                navigationLayoutManager.findViewByPosition(currentNavigationPosition)
                                    ?.requestFocus()
                            }
                            return@setOnItemKeyListener true
                        }
                    }
                }
            }
            return@setOnItemKeyListener false
        }

        adapterForPoster.setOnItemKeyListener { view, keyCode, keyEvent, _, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (position < 6) {
                            rvNavigation.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                            view.post {
                                navigationLayoutManager.findViewByPosition(currentNavigationPosition)
                                    ?.requestFocus()
                            }
                            return@setOnItemKeyListener true
                        }
                    }
                }
            }
            return@setOnItemKeyListener false
        }

        adapterForListRemote.setOnItemKeyListener { view, keyCode, keyEvent, data, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (position < 1) {
                            rvNavigation.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
                            view.post {
                                navigationLayoutManager.findViewByPosition(currentNavigationPosition)
                                    ?.requestFocus()
                            }
                            return@setOnItemKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        if (filesPositionStack.size <= 1) {
                            if(comeState == FLAG_COME_POSTER_FRAGMENT){
                                return@setOnItemKeyListener findNavController().popBackStack(R.id.posterFragment,true)
                            }
                            return@setOnItemKeyListener false
                        }
                        toParent()
                        return@setOnItemKeyListener true
                    }
                    KeyEvent.KEYCODE_MENU -> {
                        if (position != 0) {
                            val selfDialog = SelfDialog(context)
                            selfDialog.setMessage(if (downloadPath.isNotEmpty()) "是否下载到指定文件夹？" else "是否下载？")
                            selfDialog.setCenterGravity(Gravity.CENTER)
                            selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
                                selfDialog.dismiss()
                            }
                            selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
                                if (downloadPath.isNotEmpty()) {
                                    App.token?.let {
                                        if (data != null) {
                                            viewModel.download(
                                                it,
                                                arrayOf(downloadPath),
                                                data.path,
                                                bt_ticket,
                                                App.domain,
                                                App.networkId!!,
                                                App.app.applicationContext,
                                                if (App.isCircle) App.id else null
                                            )
                                        }
                                    }
                                } else {
                                    //选择存储文件夹
                                    downloadPath = data?.path ?: ""
                                    getFileList("/")
                                }
                                selfDialog.dismiss()
                            }
                            selfDialog.show()
                            return@setOnItemKeyListener true
                        }
                    }
                }
            }
            return@setOnItemKeyListener false
        }


        adapterForListRemote.setOnItemSelectListener { view, i, myFile ->
            if (i > 6) {
                rvFiles.smoothScrollBy(view.x.toInt(), (view.y - 400).toInt())
            }
        }

        adapterForPoster.setOnItemSelectListener { view, i, _ ->
            if (i > 5) {
                rvFilesForPoster.smoothScrollBy(view.x.toInt(), (view.y - 300).toInt())
            }
        }

        adapterForGrid.setOnItemSelectListener { view, i, _ ->
            if (i > 4) {
                rvFilesForGrid.smoothScrollBy(view.x.toInt(), (view.y - 360).toInt())
            }
        }

    }

    private fun getFileList(currentPath: String){
        if(downloadPath.isNotEmpty()){
            tvDeviceName.text = "选择存储位置"
            tvTitleDown.visibility = View.VISIBLE
            viewModel.getLocalFileList(currentPath, sessionLocal)
        }
        else{
            "getFileList  session:${session}  $downloadPath".log("RemoteFileBrowserFragment")
            tvDeviceName.text = deviceName
            tvTitleDown.visibility = View.GONE
            viewModel.getFileList(currentPath, session)
        }
    }


    private fun getPositionAndOffset() {
        val topView = layoutManagerForList.getChildAt(0)
        if (topView != null) {
            filesLastOffset = topView.top
            filesLastPosition = layoutManagerForList.getPosition(topView)
        }
    }

    private fun toDir(path: String, position: Int) {
        filesPositionStack.push(RvPositionModel(position, filesLastPosition, filesLastOffset, path))
        getFileList(path)
    }

    private fun toParent() {
        if (filesPositionStack.size <= 1) {
            return
        }
        popStack = filesPositionStack.pop()
        getFileList(filesPositionStack.peek().path)
    }


    override fun onDestroy() {
        super.onDestroy()
        LiveEventBus.get("on_device_request").post("")
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if(event.action == KeyEvent.ACTION_UP){
            return false
        }
        return false
    }

    override fun getLayoutID(): Int = R.layout.fragment_file_browser
}