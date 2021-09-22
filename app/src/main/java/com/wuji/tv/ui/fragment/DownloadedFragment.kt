package com.wuji.tv.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.utils.isAudio
import com.admin.libcommon.utils.isFolder
import com.admin.libcommon.utils.isImage
import com.admin.libcommon.utils.isVideo
import com.wuji.tv.R
import com.wuji.tv.model.RvPositionModel
import com.wuji.tv.ui.adapter.LocalFileListAdapter
import com.wuji.tv.viewmodels.FileManagerViewModel
import kotlinx.android.synthetic.main.fragment_downloaded.*
import org.koin.android.ext.android.inject
import java.io.File
import java.util.*


class DownloadedFragment : com.wuji.tv.common.BaseFragment() {


    private val viewModel by inject<FileManagerViewModel>()

    private lateinit var listAdapter: LocalFileListAdapter
    private lateinit var listLayoutManager: LinearLayoutManager

    private var currentPath: String = "/"

    private val filesPositionStack = Stack<RvPositionModel>()
    private var filesLastOffset = 0
    private var filesLastPosition = 0
    private var popStack: RvPositionModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        MainFragment.isRootFragment = false
        initView()
        initData()
        initEvent()
    }

    private fun initView() {

        listAdapter = LocalFileListAdapter(null)
        listLayoutManager = LinearLayoutManager(activity)
        rvDownloaded.adapter = listAdapter
        rvDownloaded.layoutManager = listLayoutManager

    }

    private fun initData() {

        viewModel.fileListLiveData.observe(this, Observer {
            rvDownloaded.show()
            tvError.remove()
            listAdapter.setDatasAndRefresh(it)
            popStack?.apply {
                listLayoutManager.scrollToPositionWithOffset(
                    offsetPosition, offset
                )
                rvDownloaded.post {
                    listLayoutManager.findViewByPosition(position)?.apply {
                        requestFocusFromTouch()
                        requestFocus()
                    }
                }
                popStack = null
            } ?: run {
                listLayoutManager.scrollToPositionWithOffset(0, 0)
                rvDownloaded.post {
                    listLayoutManager.findViewByPosition(0)?.apply {
                        requestFocusFromTouch()
                        requestFocus()
                    }
                }
            }
            hideLoading()
        })

        viewModel.liveData.observe(this, Observer {
            when (it.dataType) {
                com.wuji.tv.Constants.ERROR -> {
                    val exception = it.data as Exception
                    rvDownloaded.remove()
                    tvError.show()
                    tvError.text = exception.message
                }
            }
        })

        showLoading()
        filesPositionStack.push(RvPositionModel(0, filesLastPosition, filesLastOffset, currentPath))
        viewModel.getDownloadedFiles("/")
    }

    private fun initEvent() {
        listAdapter.setOnItemClickListener { view, position, myFile ->
            if (position == 0) {
                toParent()
                return@setOnItemClickListener
            }
            when (myFile?.type) {
                "dir" -> {
                    val path = isFolder(myFile.path)
                    if (path != null) {
                        Intent().apply {
                            putExtra("video.play.nocontro", false)
                            putExtra("video.vidonme.play.file", path)
                            putExtra("video.vidonme.original.play.file", "${myFile.path}/")
                            putExtra("vidoe.mode", 1)
                            putExtra("name", myFile.name)
                            putExtra("video.play.name", myFile.name)
                            startActivity(this)
                        }
                    } else {
                        toDir(myFile.path, position)
                    }
                }
                "file" -> {
                    when {
                        isVideo(myFile.name) -> Intent().apply {
                            putExtra("video.play.nocontro", false)
                            putExtra("video.vidonme.play.file", myFile.path)
                            putExtra("video.vidonme.original.play.file", myFile.path)
                            putExtra("vidoe.mode", 1)
                            putExtra("name", myFile.name)
                            putExtra("video.play.name", myFile.name)
                            startActivity(this)
                        }
                        isImage(myFile.name) -> Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.fromFile(File(myFile.path)), "image/*")
                            startActivity(this)
                        }
                        isAudio(myFile.name) -> Intent().apply {
                            action = Intent.ACTION_VIEW
                            setDataAndType(Uri.fromFile(File(myFile.path)), "audio/*")
                            startActivity(this)
                        }
                    }
                }
            }
        }


        rvDownloaded.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                getPositionAndOffset()
            }
        })


        listAdapter.setOnItemKeyListener { view, keyCode, keyEvent, data, position ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_UP -> {
//                        if (position < 1) {
//                            rvNavigation.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
//                            view.post {
//                                navigationLayoutManager.findViewByPosition(currentNavigationPosition)
//                                    ?.requestFocus()
//                            }
//                            return@setOnItemKeyListener true
//                        }
                    }
                    KeyEvent.KEYCODE_BACK -> {
                        if (filesPositionStack.size <= 1) {
                            return@setOnItemKeyListener false
                        }
                        toParent()
                        return@setOnItemKeyListener true
                    }
                    KeyEvent.KEYCODE_MENU -> {
                    }
                }
            }
            return@setOnItemKeyListener false
        }


        listAdapter.setOnItemSelectListener { view, i, myFile ->
            //            "myFile=${myFile?.size}".log()
            if (i > 6) {
                rvDownloaded.smoothScrollBy(view.x.toInt(), (view.y - 400).toInt())
            }
        }
    }


    private fun getPositionAndOffset() {
        val topView = listLayoutManager.getChildAt(0)
        if (topView != null) {
            filesLastOffset = topView.top
            filesLastPosition = listLayoutManager.getPosition(topView)
        }
    }

    private fun toDir(path: String, position: Int) {
        showLoading()
        filesPositionStack.push(
            RvPositionModel(
                position,
                filesLastPosition,
                filesLastOffset,
                path
            )
        )
        currentPath = path
        viewModel.getDownloadedFiles(currentPath)
    }

    private fun toParent() {
        showLoading()
        if (filesPositionStack.size <= 1) {
            hideLoading()
            return
        }
        popStack = filesPositionStack.pop()
        currentPath = filesPositionStack.peek().path
        viewModel.getDownloadedFiles(currentPath)
    }


    override fun onDestroy() {
        super.onDestroy()
        LiveEventBus.get("on_device_request").post("")
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    override fun getLayoutID(): Int = R.layout.fragment_downloaded
}