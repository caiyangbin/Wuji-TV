package com.wuji.tv.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import com.wuji.tv.model.MediaInfoModel
import com.wuji.tv.ui.adapter.ImageViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_picture_viewer.*

class StagePhotoFragment : BaseFragment() {

    companion object{
        const val KEY_BUNDLE_FILE = "key_bundle_file"
    }

    override fun getLayoutID(): Int { return R.layout.fragment_picture_viewer }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!isNavigationViewInit){
            init()
            initEvent()
            isNavigationViewInit = true
        }
    }

    private fun init(){
        val layoutManager = LinearLayoutManager(App.app.applicationContext)
        layoutManager.orientation = RecyclerView.HORIZONTAL

        val file = arguments?.getSerializable(KEY_BUNDLE_FILE) as MediaInfoModel
        file.fanartList?.removeAt(0)
        val adapter = ImageViewPagerAdapter(file,this)
        picturePhotoViewPager.adapter = adapter
        picturePhotoViewPager.setCurrentItem(0,false)
        pictureCircleIndicatorLayout.setCount(file.fanartList!!.size,0)
    }

    private fun initEvent() {
        picturePhotoViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                pictureCircleIndicatorLayout.setCurrentIndex(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}