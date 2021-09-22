package com.wuji.tv.ui.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.R
import com.wuji.tv.model.Files
import com.wuji.tv.utils.BitmapUtils
import kotlinx.android.synthetic.main.item_stage_photo.view.*

class StagePhotoAdapter : BaseQuickAdapter<Files, BaseViewHolder>(R.layout.item_stage_photo) {

    override fun convert(holder: BaseViewHolder, item: Files) {
        BitmapUtils().loadingAndSaveImg(
            item.path,
            holder.itemView.imgStagePhoto,
            "${context.filesDir?.path}/images/poster/",
            item.session,item.ip,item.ip,context,
            loadingImgId = R.mipmap.ic_home_device_loading)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        Glide.with(context).clear(holder.itemView.imgStagePhoto)
        super.onViewRecycled(holder)
    }
}