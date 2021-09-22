package com.wuji.tv.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.admin.libcommon.ext.isNotNullOrEmpty
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import com.wuji.tv.database.AppDatabase
import com.wuji.tv.model.*
import com.wuji.tv.repository.PosterRepository
import com.wuji.tv.ui.adapter.PosterLeftTabAdapter
import com.wuji.tv.ui.adapter.PosterTopTabAdapter
import com.wuji.tv.ui.adapter.PosterVideoAdapter
import com.wuji.tv.utils.*
import com.wuji.tv.widget.MyLinearLayoutManager
import com.wuji.tv.widget.SelfDialog
import kotlinx.android.synthetic.main.fragment_poster.*
import kotlinx.android.synthetic.main.fragment_poster.layoutProgress
import kotlinx.android.synthetic.main.toast.*
import org.jetbrains.anko.support.v4.runOnUiThread
import java.io.File
import kotlin.collections.ArrayList

const val MOVIE_POSTER = "movie-poster"
const val MOVIE_POSTER_MOVIE = "movie"
const val MOVIE_POSTER_TV_SHOW = "tvshow"
const val MOVIE_POSTER_MENU = "menu"
const val MOVIE_POSTER_FOLDER = "folder"
const val MOVIE_POSTER_NFO = "nfo"
const val MOVIE_POSTER_ALL = "all"
const val MOVIE_POSTER_LISTS = "lists"
const val MOVIE_POSTER_DIVIDED = "-"
const val MOVIE_POSTER_WALL = "movie-poster-wall"
const val MOVIE_POSTER_LOGO = "movie-poster-logo"
const val MOVIE_POSTER_BG = "movie-poster-bg"
const val MOVIE_POSTER_COVER = "movie-poster-cover"
const val MOVIE_POSTER_IN = "movie-poster-in"
const val TRAILER = "-trailer"
const val SAMPLE = "-sample"
const val POSTER = "poster"
const val FANART = "fanart"

class PosterFragment : BaseFragment() {
    companion object{
        const val KEY_BUNDLE_DEVICE = "key_bundle_device"
    }

    private var topLeftTabs: ArrayList<TopWithLeftTabModel> = arrayListOf()
    private lateinit var tabTopAdapter: PosterTopTabAdapter
    private var tabTabPosition = 0
    private lateinit var tabLeftTabAdapter: PosterLeftTabAdapter
    //
    private lateinit var posterVideoAdapter: PosterVideoAdapter
    private var posterVideoPosition = 0

    private var onStop = false

    private lateinit var myToast: Toast
    private lateinit var toastTextView: TextView
    private lateinit var session: String
    private var isOwn = false
    private lateinit var sessionLocal: String
    private lateinit var ip: String
    private lateinit var deviceId: String
    private lateinit var deviceName: String

    private lateinit var posterRepository: PosterRepository
    private lateinit var token: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isNavigationViewInit || savedInstanceState != null){
            initView()
            initLiveData()
            initEvent()
            initData()
            isNavigationViewInit = true
        }
    }

    override fun onStart() {
        super.onStart()
        if(topLeftTabs.isEmpty()){
            Handler().postDelayed(Runnable {
                if (topLeftTabs.isEmpty()) {
                    if (!onStop) {
                        val w = leftTabRecyclerView.width
                        scrollViewPoster.scrollTo(w+20, 0)
                    }
                }
            }, 50)
        }
        if (onStop) {
            onStop = false
            val a = posterTabRecyclerView.hasFocus()
            val b = videoRecyclerView.hasFocus()
            if(!a && !b){
                videoRecyclerView.layoutManager?.findViewByPosition(posterVideoAdapter.getLastFocusPosition())?.requestFocus()
            }
            tabFocusLost()
            if (isActive) getSession(token,App.app.applicationContext)
        }
    }

    private var isActive = false
    override fun onStop() {
        super.onStop()
        onStop = true
        isActive = posterRepository.destroy()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private var leftTabPosition = 0
    private fun initEvent() {
        tabTopAdapter.setOnFocusChangeListener(object :
            PosterTopTabAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                "top onFocusChange".log("PosterFragment")
                onTabChange(position)
                tabTopAdapter.setLastFocusPosition(position)
            }

        })
        tabLeftTabAdapter.setOnFocusChangeListener(object :
            PosterLeftTabAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                leftTabOnItemClick(position)
                tabLeftTabAdapter.setLastFocusPosition(position)
                posterVideoAdapter.setLastFocusPosition(0)
            }

        })
        posterVideoAdapter.setOnFocusChangeListener(object :
            PosterVideoAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                posterVideoAdapter.setLastFocusPosition(position)
                toastTextView.text = "${position + 1}/${posterVideoAdapter.data.size}"
                myToast.show()
                posterVideoPosition = position
                "videoRecyclerView onFocusChange : $position".log("PosterFragment")
//                focusErr = false
            }
        })
        videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    "videoRecyclerView  SCROLL_STATE_IDLE".log("PosterFragment")
                }
            }
        })

        posterVideoAdapter.setOnItemClickListener{ _, _, position ->
            val file = posterVideoAdapter.getItem(position)
            file.session = session
            file.ip = ip
            file.localSession = sessionLocal
            val bundle = Bundle()
            bundle.putSerializable(
                DetailFragment.KEY_BUNDLE_FILE, file
            )
            if (file.set.isNotEmpty()){
                if (file.videoList.isNotNullOrEmpty()){
                    findNavController().navigate(R.id.listFragment, bundle)
                }
                else {
                    Thread{
                        showLoading()
                        val list = AppDatabase.getInstance(App.app.applicationContext).getMovieListDao().getMediasWithSet(deviceId,file.set)
                        hideLoading()
                        if (list.isNotNullOrEmpty() && list.size > 1){
                            val arrayList = arrayListOf<MediaInfoModel>()
                            arrayList.addAll(list)
                            arrayList.forEach { it.session = session }
                            file.videoList = arrayList
                            findNavController().navigate(R.id.listFragment, bundle)
                        }
                        else{
                            findNavController().navigate(R.id.detailFragment, bundle)
                        }
                    }.start()
                }
            }
            else{
                findNavController().navigate(R.id.detailFragment, bundle)
            }

        }

    }

    private fun initView() {
        leftTabNameTextView.typeface = Typeface.createFromAsset(resources.assets, "fonts/tab.TTF")
        tabTopAdapter = PosterTopTabAdapter()
        tabTopAdapter.setHasStableIds(true)
        posterTabRecyclerView.adapter = tabTopAdapter
        val linearLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        posterTabRecyclerView.layoutManager = linearLayoutManager
        videoRecyclerView.layoutParams.width = resources.displayMetrics.widthPixels
        tabLeftTabAdapter = PosterLeftTabAdapter()
        leftTabRecyclerView.adapter = tabLeftTabAdapter
        val leftTabLinearLayoutManager = MyLinearLayoutManager(App.app.applicationContext)
        leftTabLinearLayoutManager.orientation = RecyclerView.VERTICAL
        leftTabRecyclerView.layoutManager = leftTabLinearLayoutManager
        posterVideoAdapter = PosterVideoAdapter()
        posterVideoAdapter.setHasStableIds(true)
        videoRecyclerView.adapter = posterVideoAdapter
        videoRecyclerView.layoutManager = GridLayoutManager(App.app.applicationContext, 6)
        val toastView = layoutInflater.inflate(R.layout.toast, toastLayout)
        toastTextView = toastView.findViewById(R.id.toastTextView)
        myToast = Toast(App.app.applicationContext)
        myToast.duration = Toast.LENGTH_SHORT
        myToast.setGravity(Gravity.BOTTOM or Gravity.RIGHT, 0, 0)
        myToast.view = toastView
    }

    private fun initData() {
        val device : PosterBundleModel = arguments?.getSerializable(KEY_BUNDLE_DEVICE) as PosterBundleModel
        sessionLocal = device.sessionLocal
        token = device.token
        ip = device.ip
        deviceId = device.deviceId
        deviceName = device.deviceName
        posterRepository = PosterRepository()
        getSession(token,App.app.applicationContext)
    }

    private var isShowLoading = false
    private fun getSession(token: String, context: Context){
        showLoading()
        isShowLoading = true
        posterRepository.access(token, false,
            object : PosterRepository.OnAccessListener {
                override fun onAccess(
                    success: Boolean,
                    code: Int,
                    msg: String,
                    data: AccessResult?
                ) {
                    if (success) {
                        data?.let {
                            session = it.session
                            isOwn = it.user.admin == 0
                            posterVideoAdapter.setSession(session,ip)
                            posterRepository.getPosterData(
                                onPullTarListener,
                                session,
                                deviceId,
                                ip,
                                App.app.applicationContext
                            )
                        }
                    } else {
                        findNavController().navigateUp()
                    }
                }

            }, context
        )
    }

    private val onPullTarListener = object : PosterRepository.OnPullTarListener{
        override fun onStart() {
            isShowLoading = false
            hideLoading()
            layoutProgress.visibility = View.VISIBLE
        }

        override fun onProgress(progress: Int) {
            if(progress < 0 || progress > 100) return
            progressBar.progress = progress
        }

        override fun onSuccess(data: List<TopWithLeftTabModel>, deviceInfoModel: DeviceInfoModel?, isScan: Boolean) {
            isShowLoading = false
            hideLoading()
            tvLoading.visibility = View.GONE
            noDataHintTextView.text = resources.getString(R.string.no_poster_data_hint)
            refreshUi(data, deviceInfoModel)
        }

        override fun onToFileFragment() {
            isShowLoading = false
            hideLoading()
            goToFileFragment()
        }

        override fun update() {
            showUpdateDialog()
        }

        override fun onNoTar() {
            isShowLoading = false
            hideLoading()
            if (!isOwn){
                goToFileFragment()
                return
            }
            showUpdateTarDialog()
        }

    }

    override fun hideLoading() {
        if (isShowLoading) return
        super.hideLoading()
    }

    private fun goToFileFragment() {
        findNavController().navigate(R.id.fileBrowserFragment, Bundle().apply {
            putString("deviceName", deviceName)
            putString("session", session)
            putString("session_local", sessionLocal)
            putInt("comeState", FLAG_COME_POSTER_FRAGMENT)
        })
    }

    private fun showUpdateDialog() {
        val selfDialog = SelfDialog(context)
        selfDialog.setTitle(getString(R.string.update))
        selfDialog.setMessage(getString(R.string.update_poster_hint))
        selfDialog.setCenterGravity(Gravity.CENTER)
        selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
            selfDialog.dismiss()
        }
        selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
            selfDialog.dismiss()
            showLoading()
            posterRepository.getTarAndParse()
        }
        selfDialog.show()
    }

    private fun refreshUi(data: List<TopWithLeftTabModel>, deviceInfoModel: DeviceInfoModel?){
        if(!onStop){
            layoutProgress.visibility = View.GONE
            if (data.isNotNullOrEmpty()){
                val size = topLeftTabs.size
                topLeftTabs.clear()
                topLeftTabs.addAll(data)
                tabTopAdapter.setNewInstance(topLeftTabs)
                if (size == 0){
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        btnSearch.isFocusable = true
                        btnHistory.isFocusable = true
                        btnFavorite.isFocusable = true
                        btnMy.isFocusable = true
                        btnVip.isFocusable = true
                    } else {
                        btnSearch.focusable = View.FOCUSABLE
                        btnHistory.focusable = View.FOCUSABLE
                        btnFavorite.focusable = View.FOCUSABLE
                        btnMy.focusable = View.FOCUSABLE
                        btnVip.focusable = View.FOCUSABLE
                    }
                }
                else if(posterVideoAdapter.itemCount == 0){
                    var data =
                        if(topLeftTabs[tabTabPosition].leftTabs.isNullOrEmpty()){
                            topLeftTabs[tabTabPosition].topTabModel.posterData
                        }
                        else {
                            topLeftTabs[tabTabPosition].leftTabs[tabLeftTabAdapter.getLastFocusPosition()].posterData
                        }
                    if (!data.isNullOrEmpty()){
                        noDataHintTextView.visibility = View.GONE
                        posterVideoAdapter.setNewInstance(data)
                    }
                }
            }
            setBackground(deviceInfoModel)
            checkDeviceImgAndDownload(deviceInfoModel)
        }
    }

    private fun initLiveData() {
        //【viewModel】的【LiveData】，更耗时，遍历所有文件请求过多时非常明显！！！
    }

    private fun setBackground(deviceInfoModel: DeviceInfoModel?){
        if(onStop || deviceInfoModel == null) return
        runOnUiThread {
            BitmapUtils().loadingAndSaveImg(
                deviceInfoModel.movie_poster_wall,
                posterBgImage,
                "${App.app.applicationContext.filesDir?.path}/images/device/$deviceId",
                session, ip, deviceId, App.app.applicationContext,
                R.mipmap.bg_poster,
                isThumbnail = false
            )
            BitmapUtils().loadingAndSaveImg(
                deviceInfoModel.movie_poster_logo,
                logoImage,
                "${App.app.applicationContext.filesDir?.path}/images/device/$deviceId",
                session, ip, deviceId,
                App.app.applicationContext,
                isThumbnail = false
            )
        }
    }

    private fun checkDeviceImgAndDownload(deviceInfoModel: DeviceInfoModel?){
        if(onStop || deviceInfoModel == null) return

        val ftPathBg = "${deviceId}${deviceInfoModel.movie_poster_bg}".replace(
            "/",
            "-"
        )
        val file = "${App.app.applicationContext.filesDir?.path}/images/device/${deviceId}"
        if(!deviceInfoModel.movie_poster_bg.isNullOrEmpty()
            && !File(file, ftPathBg).exists()) {
            BitmapUtils().downloadImg(
                App.app.applicationContext, deviceInfoModel.movie_poster_bg,
                file,
                session, ip,
                deviceId, null
            )
        }

        val ftPathLogo = "${deviceId}${deviceInfoModel.movie_poster_logo}".replace(
            "/",
            "-"
        )
        if(!deviceInfoModel.movie_poster_logo.isNullOrEmpty()
            && !File(file, ftPathLogo).exists()){
            BitmapUtils().downloadImg(
                App.app.applicationContext, deviceInfoModel.movie_poster_logo,
                file,
                session, ip, deviceId, null
            )
        }

        val ftPathCover = "${deviceId}${deviceInfoModel.movie_poster_cover}".replace(
            "/",
            "-"
        )
        if(!deviceInfoModel.movie_poster_cover.isNullOrEmpty()
            && !File(file, ftPathCover).exists()){
            BitmapUtils().downloadImg(
                App.app.applicationContext, deviceInfoModel.movie_poster_cover,
                file,
                session, ip, deviceId, null
            )
        }
    }

    private fun onTabChange(position: Int) {
        if(topLeftTabs.isNotEmpty()){
            tabTabPosition =
                if(position >= tabTopAdapter.data.size)
                    0
                else
                    position

            leftTabNameTextView.text = topLeftTabs[tabTabPosition].topTabModel.name
            tabLeftTabAdapter.setNewInstance(topLeftTabs[tabTabPosition].leftTabs.toMutableList())
            if(!leftTabRecyclerView.hasFocus()){
                tabLeftTabAdapter.setLastFocusPosition(0)
                tabLeftTabAdapter.setSelectedPosition(-1)
            }
            if(topLeftTabs[tabTabPosition].leftTabs.isEmpty()){
                scrollViewPoster.smoothScrollTo(leftTabRecyclerView.width+20, 0)
                noDataHintTextView.layoutParams.width = resources.displayMetrics.widthPixels
                leftPoster.visibility = View.GONE
            }else if(!videoRecyclerView.hasFocus()){
                scrollViewPoster.smoothScrollTo(0, 0)
                noDataHintTextView.layoutParams.width = resources.displayMetrics.widthPixels-leftTabRecyclerView.width
                leftPoster.visibility = View.GONE
            }else{
                leftPoster.visibility = View.VISIBLE
            }

            var data =
                if(topLeftTabs[tabTabPosition].leftTabs.isNullOrEmpty()){
                    topLeftTabs[tabTabPosition].topTabModel.posterData
                }
                else {
                    topLeftTabs[tabTabPosition].leftTabs[0].posterData
                }
            noDataHintTextView.visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
            posterVideoAdapter.setLastFocusPosition(0)
            posterVideoAdapter.setNewInstance(data)
        }
    }

    private fun leftTabOnItemClick(position: Int) {
        var data = topLeftTabs[tabTabPosition].leftTabs[position].posterData
        noDataHintTextView.visibility = if (data?.isEmpty() != false) View.VISIBLE else View.GONE
        posterVideoAdapter.setNewInstance(data)
        //tabs[posterTabPosition].tabTwoPosition = position
    }

    private fun tabFocusLost(){
        ViewCompat.animate(posterTabRecyclerView).alpha(0f)
            .setDuration(200).setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                }

                override fun onAnimationEnd(view: View?) {
                    posterTabRecyclerView.visibility = View.GONE
                    layoutTopTabUp.visibility = View.GONE
                }

                override fun onAnimationCancel(view: View?) {
                }
            })
            .start()
    }

    private fun tabFocusGain(){
        layoutTopTabUp.visibility = View.VISIBLE
        posterTabRecyclerView.visibility = View.VISIBLE
        leftTabNameLayout.visibility = View.GONE
        ViewCompat.animate(posterTabRecyclerView).alpha(1f)
            .setDuration(200).setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                }

                override fun onAnimationEnd(view: View?) {
                    posterTabRecyclerView.layoutManager?.findViewByPosition(tabTopAdapter.getLastFocusPosition())?.requestFocus()
                }

                override fun onAnimationCancel(view: View?) {
                }
            })
            .start()
    }

    private fun showUpdateTarDialog(){
        val selfDialog = SelfDialog(context)
        selfDialog.setTitle(getString(R.string.rebuild))
        selfDialog.setMessage(getString(R.string.rebuild_hint))
        selfDialog.setCenterGravity(Gravity.CENTER)
        selfDialog.setNoOnclickListener(getString(R.string.cancel)) {
            selfDialog.dismiss()
        }
        selfDialog.setYesOnclickListener(getString(R.string.confirm)) {
            selfDialog.dismiss()
            showLoading()
            posterRepository.updateTar(session,object : PosterRepository.OnUpdateListener{
                override fun onUpdate(isSuccess: Boolean, code: Int, msg: String) {
                    hideLoading()
                    if (isSuccess){
                        showUpdateDialog()
                    }
                }
            },App.app.applicationContext)
        }
        selfDialog.show()
    }

    private var keyCode = -1
    private var action = -1
    @SuppressLint("WrongConstant")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        action = event.action
        keyCode = event.keyCode
        if(event.action == KeyEvent.ACTION_UP){
            return false
        }
        when(event.keyCode){
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (leftTabRecyclerView.hasFocus()
                    && tabLeftTabAdapter.getLastFocusPosition() == 0) {
                    tabFocusGain()
                    return true
                }
                else if(videoRecyclerView.hasFocus()
                    && topLeftTabs[tabTabPosition].leftTabs.isEmpty()
                    && posterVideoAdapter.getLastFocusPosition() < 6){
                    tabFocusGain()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (posterTabRecyclerView.hasFocus()) {
                    if (tabTabPosition < topLeftTabs.size && topLeftTabs[tabTabPosition].leftTabs.isNotEmpty()) {
                        leftTabNameLayout.visibility = View.VISIBLE
                        leftTabRecyclerView.layoutManager?.findViewByPosition(tabLeftTabAdapter.getLastFocusPosition())?.apply { requestFocus() }
                        tabFocusLost()
                        return true
                    }
                    else if (posterVideoAdapter.data.isEmpty()) {
                        return true
                    }
                    else {
                        videoRecyclerView.layoutManager?.findViewByPosition(posterVideoAdapter.getLastFocusPosition())?.requestFocus()
                        tabFocusLost()
                        return true
                    }
                }
                else if (videoRecyclerView.hasFocus()) {
                    var lastFocusPosition = posterVideoAdapter.getLastFocusPosition()
                    if(videoRecyclerView.scrollState != 0
                        || (lastFocusPosition+6)/6 == (posterVideoAdapter.itemCount+5)/6) {
                        return true
                    }
                }
                else if(btnSearch.hasFocus() || btnHistory.hasFocus()
                    || btnFavorite.hasFocus() || btnMy.hasFocus()
                    || btnVip.hasFocus()){
                    posterTabRecyclerView.layoutManager?.findViewByPosition(tabTopAdapter.getLastFocusPosition())?.requestFocus()
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (posterTabRecyclerView.hasFocus() && layoutTopTabUp.visibility == View.GONE) {
                    tabFocusGain()
                }
                else if (posterTabRecyclerView.hasFocus() && tabTabPosition == 0) {
                    return true
                }
                else if(btnSearch.hasFocus()){
                    return true
                }
                else if (videoRecyclerView.hasFocus()
                    && (posterVideoPosition == 0 || posterVideoPosition % 6 == 0)) {
                    if (topLeftTabs[tabTabPosition].leftTabs.isEmpty()){
                        return true
                    }
                    else{
                        leftPoster.visibility = View.GONE
                        videoRecyclerView.scrollToPosition(0)
                        scrollViewPoster.smoothScrollTo(0, 0)
                        leftTabNameLayout.visibility = View.VISIBLE
                        leftTabRecyclerView.layoutManager?.findViewByPosition(tabLeftTabAdapter.getLastFocusPosition())?.apply { requestFocus() }
                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (leftTabRecyclerView.hasFocus()
                    && posterVideoAdapter.data.isNotEmpty()) {
                    leftPoster.visibility = View.VISIBLE
                    scrollViewPoster.smoothScrollTo(leftTabRecyclerView.width+20, 0)
                    videoRecyclerView.layoutManager?.findViewByPosition(posterVideoAdapter.getLastFocusPosition())?.requestFocus()
                    return true
                }
                else if (videoRecyclerView.hasFocus()
                    && posterVideoAdapter.getLastFocusPosition() == posterVideoAdapter.itemCount-1) {
                    return true
                }
                else if(btnVip.hasFocus()){
                    return true
                }
            }
            KeyEvent.KEYCODE_BACK -> {
                if (videoRecyclerView.hasFocus()) {
                    videoRecyclerView.scrollToPosition(0)
                    posterVideoAdapter.setLastFocusPosition(0)
                    posterTabRecyclerView.layoutManager?.findViewByPosition(tabTopAdapter.getLastFocusPosition())?.requestFocus()
                    return true
                }
            }
            KeyEvent.KEYCODE_MENU -> {
                if (isOwn){
                    showUpdateTarDialog()
                }
            }
        }
        return false
    }
    override fun getApplicationContext(): Context {
        return App.app.applicationContext
    }
    override fun getLayoutID(): Int = R.layout.fragment_poster
}
