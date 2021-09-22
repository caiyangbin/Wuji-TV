package com.wuji.tv.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.admin.libcommon.ext.log
import com.admin.libcommon.utils.isISOVideo
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import com.wuji.tv.model.*
import com.wuji.tv.repository.PosterRepository
import com.wuji.tv.ui.adapter.DetailTabAdapter
import com.wuji.tv.ui.adapter.DetailTvSeriesAdapter
import com.wuji.tv.utils.BitmapUtils
import com.wuji.tv.utils.FileUtils.isNotEmpty
import com.wuji.tv.utils.play
import com.wuji.tv.viewmodels.DetailViewModel
import com.wuji.tv.widget.MyLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_detail.*
import okhttp3.ResponseBody
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Response

class DetailFragment : BaseFragment() {

    companion object{
        const val KEY_BUNDLE_FILE = "key_bundle_file"
    }

    private val viewModel by inject<DetailViewModel>()
    private lateinit var detailTabAdapter: DetailTabAdapter
    private lateinit var detailTvSeriesAdapter: DetailTvSeriesAdapter
    private lateinit var mToken: String
    private var toFragmentState = -1

    private lateinit var file : MediaInfoModel
    private var onStop = false
    private var bgImage: Bitmap? = null
    private var coverImage: Bitmap? = null
    private var posterRepository: PosterRepository? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(!isNavigationViewInit){
            initView()
            initLiveData()
            initEvent()
            initData()
            isNavigationViewInit = true
        }
    }

    override fun onStart() {
        super.onStart()
        if(onStop){
            if(bgImage != null){
                val fanartPath =
                    if(file.fanartList == null || file.fanartList!!.isEmpty())
                        file.posterList?.get(0)
                    else
                        file.fanartList!![0]
                val ftPath = "${file.deviceId}${fanartPath}".replace("/","-")
                BitmapUtils().saveBitmap( "${context?.filesDir?.path}/images/poster/${file.deviceId}",ftPath, bgImage!!)
                detailBgImage.setImageBitmap(bgImage)
            }
            if(coverImage != null){
                val btPath = "${file.deviceId}${file.posterList!![0]}".replace("/","-")
                BitmapUtils().saveBitmap( "${context?.filesDir?.path}/images/poster/${file.deviceId}",btPath,coverImage!!)
                detailImage.setImageBitmap(coverImage)
            }
            when(toFragmentState){
                1 -> downloadBtn.requestFocus()
                2 -> stagePhotoBtn.requestFocus()
            }
        }
        onStop = false
        toFragmentState = -1
    }

    override fun onStop() {
        super.onStop()
        onStop = true
        posterRepository?.destroy()
    }

    private fun initEvent() {
        playBtn.setOnClickListener{
            play(file.session,file.ip,file.path,file.title,context!!)
        }
//        detailTvSeriesAdapter.setOnItemClickListener {_, _, position ->
//            context?.let {
//                play(file.fileList[position].session,
//                    file.fileList[position].ip,
//                    file.fileList[position].path,
//                    file.fileList[position].name, it)
//            }
//        }
        downloadBtn.setOnClickListener{
            "downloadBtn  App.isCircle:${App.isCircle}".log("DetailFragment")
            if(App.isCircle){
                showLoading()
                Thread{
                    val rootPath = file.path.substring(0,file.path.lastIndexOf("/"))
                    val pathSplitList = rootPath.substring(1).split("/")
                    val name = pathSplitList[pathSplitList.lastIndex]
                    val body = HashMap<String, Any>()
                    body["bt_type"] = arrayOf(4)
                    body["name"] = name
                    body["page"] = 1
                    body["sort_by"] = 1
                    body["page_number"] = -1
                    val listResult = App.createDbApi?.listDb(body)
                    listResult?.enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            hideLoading()
                            if(response.isSuccessful){
                                val body = response.body()?.string() ?: ""
                                if(body.contains(name)){
                                    val bodySplit = body.split(name)
                                    val startIndexOf = bodySplit[0].lastIndexOf("{")
                                    val endIndexOf = bodySplit[1].indexOf("}")
                                    val btItemStr = "${bodySplit[0].substring(startIndexOf)}${bodySplit[1].substring(0,endIndexOf+1)}"
                                    if(btItemStr.isNotEmpty()){
                                        "downloadBtn  bt_ticket:${btItemStr}".log("RemoteFileBrowserFragment")
                                        val btItem = Gson().fromJson(btItemStr,Item :: class.java)
                                        if (!btItem.bt_ticket.isNullOrEmpty()){
                                            "downloadBtn  bt_ticket:${btItem.bt_ticket}".log("RemoteFileBrowserFragment")
                                            toFragmentState = 1
                                            findNavController().navigate(R.id.fileBrowserFragment, Bundle().apply {
                                                putInt("comeState", FLAG_COME_DETAIL_FRAGMENT)
                                                putString("deviceName", "��ǰ�豸")
                                                putString("session", file.session)
                                                putString("session_local", file.localSession)
                                                putString("download_path", rootPath)
                                                putString("bt_ticket", btItem.bt_ticket)
                                            })
                                            return
                                        }
                                    }
                                }
                            }
                            /*runOnUiThread {
                                toast("��Ǹ��û���ҵ����ӣ�����Դ�޷����ء�")
                            }*/
                            findNavController().navigate(R.id.fileBrowserFragment, Bundle().apply {
                                putInt("comeState", FLAG_COME_DETAIL_FRAGMENT)
                                putString("deviceName", "��ǰ�豸")
                                putString("session", file.session)
                                putString("session_local", file.localSession)
                                putString("download_path", rootPath)
                            })
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            t.message?.log("DetailFragment")
                            hideLoading()
                            runOnUiThread {
                                toast("�����쳣�������ԣ�")
                            }
                        }

                    })
                }.start()
            }
            else{
                toFragmentState = 1
                val rootPath = file.path.substring(0,file.path.lastIndexOf("/"))
                findNavController().navigate(R.id.fileBrowserFragment, Bundle().apply {
                    putInt("comeState", FLAG_COME_DETAIL_FRAGMENT)
                    putString("deviceName", "��ǰ�豸")
                    putString("session", file.session)
                    putString("session_local", file.localSession)
                    putString("download_path", rootPath)
                })
            }
        }
        stagePhotoBtn.setOnClickListener{
            toFragmentState = 2
            val bundle = Bundle()
            bundle.putSerializable(StagePhotoFragment.KEY_BUNDLE_FILE, file)
            findNavController().navigate(R.id.stagePhotoFragment, bundle)
        }
    }

    private fun initLiveData() {
//        tvSeriesRecyclerView.setOnFocusChangeListener(object : ScaleRecyclerView.OnFocusChangeListener{
//            override fun onFocusChange(position: Int) {
//                pathDetailTextView.text = file.fileList[position].path
//            }
//
//        })
    }

    private fun initView() {
        detailTabAdapter = DetailTabAdapter()
        tabRecyclerView.adapter = detailTabAdapter
        val linearLayoutManager = activity?.let { MyLinearLayoutManager(it) }
        linearLayoutManager?.orientation = RecyclerView.HORIZONTAL
        tabRecyclerView.layoutManager = linearLayoutManager

        detailTvSeriesAdapter = DetailTvSeriesAdapter()
        tvSeriesRecyclerView.adapter = detailTvSeriesAdapter
        val layoutManager = activity?.let { MyLinearLayoutManager(it) }
        layoutManager?.orientation = RecyclerView.HORIZONTAL
        tvSeriesRecyclerView.layoutManager = layoutManager

    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        "initData  App.networkId:${App.networkId}".log("DetailFragment")
        file = arguments?.getSerializable(KEY_BUNDLE_FILE) as MediaInfoModel

        if(file.rating > 0){
            videoRating.text = file.rating.toString()
            videoRating.visibility = View.VISIBLE
        }



        val fanartPath =
            if(file.fanartList == null || file.fanartList!!.isEmpty())
                file.posterList!![0]
            else
                file.fanartList!![0]

        context?.let {
            BitmapUtils().loadingAndSaveImg(
                fanartPath,detailBgImage,
                "${it.filesDir?.path}/images/poster/${file.deviceId}",
                file.session,
                file.ip,
                file.deviceId,
                it,
                R.drawable.bg_video_unselected,-1,
                isThumbnail = false)
            BitmapUtils().loadingAndSaveImg(
                file.posterList!![0],detailImage,
                "${it.filesDir?.path}/images/poster/${file.deviceId}",
                file.session,
                file.ip,
                file.deviceId,
                it,
                -1,-1,
                isThumbnail = false)
        }

        showLoading()
        posterRepository = PosterRepository()
        posterRepository?.getFileManage(file.path,file.session,object : PosterRepository.OnFileManageListener{
            override fun onFileManage(data: FileInfoResult?) {
                hideLoading()
                data?.apply {
                    file.mediaSize = size
                }
                showInfo()
            }
        })

    }

    private fun showInfo(){
        titleDetailTextView.text = file.title

        val tabs by lazy { ArrayList<String>() }
        if(isNotEmpty(file.premiered)) file.premiered.let { tabs.add(it) }
        if(isNotEmpty(file.runtime) /*&& file.fileList.isEmpty()*/) tabs.add("${file.runtime} ${getString(R.string.min)}")
        tabs.add(Formatter.formatFileSize(context,file.mediaSize))
        if(file.country != null && file.country!!.isNotEmpty()) tabs.addAll(file.country!!)
        if(file.genre != null && file.genre!!.isNotEmpty()) tabs.addAll(file.genre!!)
        detailTabAdapter.setNewInstance(tabs)

        if(file.fanartList?.isNotEmpty() == true && file.fanartList!!.size > 1){
            stagePhotoBtn.visibility = View.VISIBLE
        }
        if(file.trailerList?.isNotEmpty() == true){
            trailerBtn.visibility = View.VISIBLE
        }
        if(file.sampleList?.isNotEmpty() == true){
            sampleBtn.visibility = View.VISIBLE
        }

        if(!file.path.contains(".") || isISOVideo(file.path)){
            playBtn.visibility = View.GONE
            tvSeriesRecyclerView.visibility = View.GONE
            requestFocus(downloadBtn)
        }
        else {
            tvSeriesRecyclerView.visibility = View.GONE
            requestFocus(playBtn)
        }

        var protagonist = ""
        if(file.actor != null){
            for(index in file.actor!!.indices){
                if(index == 0){
                    protagonist = file.actor!![index]
                }
                else {
                    protagonist += "/"
                    protagonist += file.actor!![index]
                }
            }
        }
        directorTextView.text = "${getString(R.string.director)}${file.director}"
        protagonistTextView.text = "${getString(R.string.protagonist)}${protagonist}"
        plotTextView.text = "${getString(R.string.plot)}${file.plot}"
    }

    private fun requestFocus(view: View){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            view.isFocusable = true
        } else {
            view.focusable = View.FOCUSABLE
        }
        view.requestFocus()
    }
    override fun getApplicationContext(): Context {
        return App.app.applicationContext
    }
    override fun getLayoutID(): Int = R.layout.fragment_detail
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when(event.keyCode){
            KeyEvent.KEYCODE_DPAD_LEFT ->{
                if(downloadBtn.isFocused || plotScrollView.isFocused){
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_UP ->{
                if(!plotScrollView.isFocused){
                    return true
                }
                else if(plotScrollView.hasFocus()){
                    requestFocus(if(playBtn.visibility == View.VISIBLE) playBtn else downloadBtn)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN ->{
                /*if(plotScrollView.isFocused){
                    val view = plotScrollView.getChildAt(0)
                    if(view.measuredHeight == plotScrollView.scrollY + plotScrollView.height){
                        return true
                    }
                }*/
            }
        }
        return false
    }
}