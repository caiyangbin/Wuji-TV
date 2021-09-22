package com.wuji.tv.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import com.wuji.tv.model.MediaInfoModel
import com.wuji.tv.ui.adapter.ListAdapter
import com.wuji.tv.utils.BitmapUtils
import com.wuji.tv.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.ext.android.inject

class ListFragment : BaseFragment() {

    private lateinit var file : MediaInfoModel

    companion object{
        const val KEY_BUNDLE_FILE = "key_bundle_file"
    }

    private val viewModel by inject<ListViewModel>()
    private lateinit var listAdapter : ListAdapter

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

    private fun initEvent() {
        listAdapter.setOnItemClickListener{ _, _, position ->
            val bundle = Bundle()
            val f = listAdapter.getItem(position)
            f.session = file.session
            bundle.putSerializable(DetailFragment.KEY_BUNDLE_FILE, f)
            findNavController().navigate(R.id.detailFragment, bundle)
        }
        listAdapter.setOnFocusChangeListener(object : ListAdapter.OnFocusChangeListener {
            override fun onFocusChange(position: Int) {
                initBg(position)
            }

        })
    }

    private fun initLiveData() {

    }

    private fun initView() {
        listAdapter = ListAdapter()
        listRecyclerView.adapter = listAdapter
        val layoutManager = LinearLayoutManager(App.app.applicationContext)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        listRecyclerView.layoutManager = layoutManager

    }


    override fun getLayoutID(): Int = R.layout.fragment_list
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        file = arguments?.getSerializable(KEY_BUNDLE_FILE) as MediaInfoModel

        titleTextView.text = if(file.path.endsWith("tvshow",true)) file.title else file.set
        listAdapter.setNewInstance(if(file.path.endsWith("tvshow",true)) arrayListOf(file) else file.videoList)
    }

    private fun initBg(position: Int){
        val fanartPath = file.videoList?.get(position)?.fanartList?.get(0)
        BitmapUtils().loadingAndSaveImg(
            fanartPath,
            listBgImage,
            "${App.app.applicationContext.filesDir?.path}/images/poster/",
            file.session,file.ip,file.deviceId, App.app.applicationContext,
            errorImgId = R.drawable.bg_video_unselected,loadingImgId = -1,isThumbnail = false)
    }

}