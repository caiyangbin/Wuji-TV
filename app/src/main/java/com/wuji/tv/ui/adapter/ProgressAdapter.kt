package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.text.format.Formatter
import android.view.View
import com.admin.libcommon.utils.isAudio
import com.admin.libcommon.utils.isISOVideo
import com.admin.libcommon.utils.isImage
import com.admin.libcommon.utils.isVideo
import kotlinx.android.synthetic.main.item_download_list_layout.view.*
import com.wuji.tv.R
import com.wuji.tv.model.Download
import com.wuji.tv.model.ProgressModel
import kotlinx.android.synthetic.main.item_download_list_layout.view.itemRoot
import kotlinx.android.synthetic.main.item_download_list_layout.view.ivIcon
import kotlinx.android.synthetic.main.item_download_list_layout.view.tvName


class ProgressAdapter(devices: ArrayList<Download>?) :
    com.wuji.tv.common.BaseRvAdapter<Download>(devices) {

    private val progressMap = HashMap<Int,Boolean>()

    fun getProgressIs100(position: Int) : Boolean{
        return progressMap[position] ?: false
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position)
        } else {
            setProgress(holder, payloads[0],position)
        }
    }

    private fun setProgress(holder: BaseHolder, any: Any, position: Int) {
        val data = any as ProgressModel
        with(holder.itemView) {
            val progress = ((data.currentSize.toDouble() / data.totalSize)*100).toInt()
            progressMap[position] = progress == 100
                progressBar.progress = progress
            var size = Formatter.formatFileSize(context, kotlin.math.abs(data.speed))
            if(size.contains("MB")){
                size = Formatter.formatFileSize(context, kotlin.math.abs(data.speed*8))
                size = size.replace("B","b")
            }
            tvTotalSizeAndSpeed.text =
                    "${size}/s   " +
                    "已下载：${Formatter.formatFileSize(context, kotlin.math.abs(data.currentSize))}   " +
                    "总大小：${Formatter.formatFileSize(context, kotlin.math.abs(data.totalSize))}"
            progressBarText.text = "$progress"
        }
    }


    override fun setData(view: View, data: Download?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.bg_app_unselected)
            tvName.text = data!!.name
            progressBar.setProgressColor(Color.parseColor("#457DFF"))
            if(!data.name.contains(".")){
                ivIcon.setImageResource(R.mipmap.dir)
            }
            else if(isImage(data.name)){
                ivIcon.setImageResource(R.mipmap.image_file)
            }
            else if(isVideo(data.name) || isISOVideo(data.name)){
                ivIcon.setImageResource(R.mipmap.video_file)
            }
            else if(isAudio(data.name)){
                ivIcon.setImageResource(R.mipmap.music_file)
            }
            else{
                ivIcon.setImageResource(R.mipmap.else_file)
            }
        }
    }


    override fun setFocusData(view: View, data: Download?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.bg_tab_left_selected)
            progressBar.setProgressColor(Color.parseColor("#ffffff"))
        }
    }

    override fun setNoFocusData(view: View, data: Download?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.bg_app_unselected)
            progressBar.setProgressColor(Color.parseColor("#457DFF"))
        }
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_download_list_layout

}