package com.wuji.tv.ui.adapter

import android.graphics.Typeface
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.model.MediaInfoModel
import com.wuji.tv.utils.BitmapUtils
import kotlinx.android.synthetic.main.item_list.view.videoImage
import kotlinx.android.synthetic.main.item_list.view.videoNameTextView


class ListAdapter : BaseQuickAdapter<MediaInfoModel, BaseViewHolder>(R.layout.item_list) {

    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    fun setOnFocusChangeListener(onClickListener: OnFocusChangeListener){
        mOnFocusChangeListener = onClickListener
    }

    override fun convert(holder: BaseViewHolder, item: MediaInfoModel) {
        holder.itemView.videoNameTextView.text = item.title
        /*Glide
            .with(context)
            .load("http://${item.ip}:9898/file/download?session=${item.session}&path=${item.path_pic_poster}")
            .centerCrop()
//            .placeholder(R.mipmap.icon1)
            .into(holder.itemView.videoImage)*/
        BitmapUtils().loadingAndSaveImg(
            item.posterList?.get(0) ?: "",
            holder.itemView.videoImage,
            "${App.app.applicationContext.filesDir?.path}/images/poster",
            item.session,item.ip,item.deviceId, App.app.applicationContext,
            loadingImgId = R.mipmap.ic_poster_device_loading,isThumbnail = false)
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
//                holder.itemView.videoNameTextView.singleLine = false
//                holder.itemView.videoNameTextView.lines = 2
//                holder.itemView.videoNameTextView.textSize = mContext.resources.getDimensionPixelSize(R.dimen.px26).toFloat()
//                holder.itemView.videoNameTextView.horizontalPadding = 5
//                holder.itemView.videoNameTextView.verticalPadding = 5
                val animations: Animation = AnimationUtils.loadAnimation(App.app.applicationContext, R.anim.scale_amplify)
                v.clearAnimation()
                v.startAnimation(animations)
                mOnFocusChangeListener?.onFocusChange(getItemPosition(item))
                holder.itemView.videoNameTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            }
            else {
                val animations: Animation = AnimationUtils.loadAnimation(App.app.applicationContext, R.anim.scale_shrink)
                v.clearAnimation()
                v.startAnimation(animations)
//                holder.itemView.videoNameTextView.singleLine = true
//                holder.itemView.videoNameTextView.textSize = mContext.resources.getDimensionPixelSize(R.dimen.px28).toFloat()
//                holder.itemView.videoNameTextView.horizontalPadding = 20
//                holder.itemView.videoNameTextView.verticalPadding = 20
                holder.itemView.videoNameTextView.setTypeface(Typeface.DEFAULT,Typeface.NORMAL)
            }
        }

    }

}