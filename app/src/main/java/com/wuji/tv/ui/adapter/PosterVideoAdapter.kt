package com.wuji.tv.ui.adapter

import android.graphics.Typeface
import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.model.MediaInfoModel
import com.wuji.tv.utils.BitmapUtils
import kotlinx.android.synthetic.main.item_poster_video.view.*


class PosterVideoAdapter() : BaseDelegateMultiAdapter<MediaInfoModel, BaseViewHolder>() {

    private lateinit var ip: String
    private lateinit var session: String
    private var mLastFocusPosition = 0
    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener
    }

    fun getLastFocusPosition(): Int {
        return mLastFocusPosition
    }

    fun setLastFocusPosition(position: Int) {
        mLastFocusPosition = position
    }

    fun setSession(_session: String, _ip: String){
        session = _session
        ip = _ip
    }

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<MediaInfoModel>() {
            override fun getItemType(data: List<MediaInfoModel>, position: Int): Int {
                return LAYOUT_TYPE_GRID
            }
        })
        getMultiTypeDelegate()?.apply {
            addItemType(LAYOUT_TYPE_GRID, R.layout.item_poster_video)
            //addItemType(LAYOUT_TYPE_LINEAR, R.layout.item_poster_video_linear)
        }
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object{
        const val LAYOUT_TYPE_GRID = 1
        const val LAYOUT_TYPE_LINEAR = 2
    }

    override fun convert(holder: BaseViewHolder, item: MediaInfoModel) {
//        when(item.layoutType){
//            LAYOUT_TYPE_GRID ->{
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    holder.itemView.isFocusable = true
                } else {
                    holder.itemView.focusable = View.FOCUSABLE
                }
                holder.itemView.videoNameTextView.text = item.title
        if(item.rating > 0){
            holder.itemView.videoRating.text = item.rating.toString()
            holder.itemView.videoRating.visibility = View.VISIBLE
        }

//                    if(isNotEmpty(item.map[NFO_SET]))
//                        item.map[NFO_SET]
//                    else
//                        item.map[NFO_TITLE]
        item.posterList?.let {
            if (it.isNotEmpty()){
                BitmapUtils().loadingAndSaveImg(
                    it[0],
                    holder.itemView.videoImage,
                    "${App.app.applicationContext.filesDir?.path}/images/poster/${item.deviceId}",
                    session,ip,item.deviceId, App.app.applicationContext,
                    loadingImgId = R.mipmap.ic_poster_device_loading)
            }
        }
                holder.itemView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        mOnFocusChangeListener?.onFocusChange(getItemPosition(item))
//                        holder.itemView.videoNameTextView.singleLine = false
//                        holder.itemView.videoNameTextView.lines = 2
//                        holder.itemView.videoNameTextView.horizontalPadding = 5
//                        holder.itemView.videoNameTextView.verticalPadding = 5
                        ViewCompat.animate(v).scaleX(1.1f).scaleY(1.1f).translationZ(1.1f)
                            .setDuration(200).setListener(object : ViewPropertyAnimatorListener {
                                override fun onAnimationStart(view: View?) {
                                    "dispatchKeyEvent  setFocusData  onAnimationStart".log("DeviceFragment")
                                }

                                override fun onAnimationEnd(view: View?) {
                                    "dispatchKeyEvent  setFocusData  onAnimationEnd".log("DeviceFragment")
                                }

                                override fun onAnimationCancel(view: View?) {
                                    "dispatchKeyEvent  setFocusData  onAnimationCancel".log("DeviceFragment")
                                }
                            })
                            .start()
//                        val animations: Animation = AnimationUtils.loadAnimation(App.app.applicationContext, R.anim.scale_amplify)
//                        v.clearAnimation()
//                        v.startAnimation(animations)
                        holder.itemView.videoNameTextView.setTypeface(Typeface.DEFAULT,Typeface.BOLD)
                        holder.itemView.ivPlay.setImageResource(R.mipmap.ic_poster_play)
                    }
                    else {
                        ViewCompat.animate(v).scaleX(1f).scaleY(1f).translationZ(1f)
                            .setDuration(1).setListener(object : ViewPropertyAnimatorListener{
                                override fun onAnimationStart(view: View?) {
                                    "dispatchKeyEvent  setNoFocusData  onAnimationStart".log("DeviceFragment")
                                }

                                override fun onAnimationEnd(view: View?) {
                                    "dispatchKeyEvent  setNoFocusData  onAnimationEnd".log("DeviceFragment")
                                }

                                override fun onAnimationCancel(view: View?) {
                                    "dispatchKeyEvent  setNoFocusData  onAnimationCancel".log("DeviceFragment")
                                }
                            })
                            .start()
//                        val animations: Animation = AnimationUtils.loadAnimation(App.app.applicationContext, R.anim.scale_shrink)
//                        v.clearAnimation()
//                        v.startAnimation(animations)
//                        holder.itemView.videoNameTextView.singleLine = true
//                        holder.itemView.videoNameTextView.horizontalPadding = 20
//                        holder.itemView.videoNameTextView.verticalPadding = 20
                        holder.itemView.videoNameTextView.setTypeface(Typeface.DEFAULT,Typeface.NORMAL)
                        holder.itemView.ivPlay.setImageResource(R.drawable.bg_video_unselected)
                    }
//                }
//            }
//            LAYOUT_TYPE_LINEAR -> {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    holder.itemView.isFocusable = true
//                } else {
//                    holder.itemView.focusable = View.FOCUSABLE
//                }
//                holder.itemView.videoNameTextView.text = item.title
//            }
        }

    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        Glide.with(App.app.applicationContext).clear(holder.itemView.videoImage)
        super.onViewRecycled(holder)
    }

}