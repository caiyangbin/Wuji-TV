package com.wuji.tv.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import com.wuji.tv.model.MediaInfoModel
import com.wuji.tv.utils.BitmapUtils


class ImageViewPagerAdapter(_media: MediaInfoModel, context: BaseFragment): PagerAdapter() {

    private val media = _media
    private val mImageUrls: ArrayList<String> = media.fanartList!!
    private val fragment: BaseFragment = context

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = fragment.layoutInflater.inflate(R.layout.item_stage_photo,null)
            .findViewById<ImageView>(R.id.imgStagePhoto)
        fragment.context?.let {
            BitmapUtils().loadingAndSaveImg(
                mImageUrls[position],
                photoView,
                "${it.filesDir?.path}/images/poster/",
                media.session,media.ip,media.deviceId, it,
                loadingImgId = R.mipmap.ic_home_device_loading,
                isThumbnail = false)
        }
        /*photoView.setOnClickListener {
            fragment.findNavController().navigateUp()
        }*/
        container.addView(photoView)
        return photoView
    }

    override fun getCount(): Int {
        return mImageUrls.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE;
    }
}