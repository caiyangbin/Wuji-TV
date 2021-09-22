package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.admin.libcommon.ext.dp2px
import kotlinx.android.synthetic.main.item_device.view.*
import com.wuji.tv.R
import com.wuji.tv.model.RemoteDeviceModel
import com.wuji.tv.utils.BitmapUtils
import io.sdvn.socket.SDVNApi
import io.weline.devhelper.DevTypeHelper
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.padding


class DeviceAdapter(devices: ArrayList<RemoteDeviceModel>?,layoutType: Int = 0) :
    com.wuji.tv.common.BaseRvAdapter<RemoteDeviceModel>(devices) {

    var mLastSelectedSn= -1
    private var mLastFocusPosition = 0
    private val layoutType = layoutType
    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    fun setOnFocusChangeListener(onClickListener: OnFocusChangeListener){
        mOnFocusChangeListener = onClickListener
    }

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    fun getLastFocusPosition(): Int {
        return mLastFocusPosition
    }

    fun setLastFocusPosition(position: Int) {
        mLastFocusPosition = position
    }

    override fun setData(view: View, data: RemoteDeviceModel?, position: Int) {
        data?.apply {
            if(data.newNumber.isNullOrEmpty()){
                view.tvNewNumber.visibility = View.GONE
            }
            else {
                var num = 0
                num = data.newNumber.toInt()
                if(num > 0){
                    view.tvNewNumber.visibility = View.VISIBLE
                }
                else{
                    view.tvNewNumber.visibility = View.GONE
                }
            }
            view.tvNewNumber.text = data.newNumber
            if(layoutType == 2){
                view.ivIcon.setBackgroundResource(R.mipmap.ic_home_device_circle)
            }
            if(layoutType == 1){
                val layoutParamsTvName = view.tvName.layoutParams
                layoutParamsTvName.width = mContext.dp2px(134)
                view.tvName.setPadding(0,mContext.dp2px(15),0,0)
                val layoutParams = view.ivIconLayout.layoutParams
                layoutParams.width = mContext.dp2px(138)
                layoutParams.height = mContext.dp2px(138)
                val layoutBoxParams = view.ivIconBoxLayout.layoutParams
                layoutBoxParams.width = mContext.dp2px(138)
                layoutBoxParams.height = mContext.dp2px(138)
                view.ivIconLayout.setBackgroundResource(R.drawable.selector_manage_bg)
                view.ivIconBoxLayout.visibility = View.GONE
                view.ivIcon.padding = mContext.dp2px(30)
                view.ivIcon.backgroundColor = Color.parseColor("#00000000")
                view.ivIcon.setImageResource(data.managerResId)
            }
            else {
                if(!movie_poster_cover.isNullOrEmpty()){
                    BitmapUtils().loadingAndSaveImg(movie_poster_cover,view.ivIcon,
                        "${mContext.filesDir?.path}/images/device/${sdvnDevice!!.id}/",
                        session,sdvnDevice!!.vip,
                        sdvnDevice!!.id, mContext,R.mipmap.ic_home_device,R.mipmap.ic_home_device_loading)
                }
                else{
                    view.ivIcon.setImageResource(R.mipmap.ic_home_device)
                }
                if (!DevTypeHelper.isNas(sdvnDevice!!.devClass)
                    && !DevTypeHelper.isNasByFeature(sdvnDevice!!.feature)
                    && sdvnDevice!!.isSelectable) {
                    val selectedSn = SDVNApi.getInstance().selectedSn(sdvnDevice!!.id)
                    view.ivSn.visibility = if (selectedSn) View.VISIBLE else View.GONE
                    mLastSelectedSn = if (selectedSn) position else -1
                }
            }
            val name = if(data.name.isNullOrEmpty()) data.sdvnDevice?.name!! else data.name
            view.tvName.text = name
        }
    }

    override fun setFocusData(view: View, data: RemoteDeviceModel?, position: Int) {
        mOnFocusChangeListener?.onFocusChange(position)
        if(layoutType != 1){
            data?.apply {
//                view.tvName.setTextColor(Color.parseColor("#ffffff"))
//                view.ivIconBoxLayout.setBackgroundResource(R.drawable.bg_device_selected)
                ViewCompat.animate(view).scaleX(1.15f).scaleY(1.15f).translationZ(1.15f)
                    .setDuration(200)
                    .start()
//                val animations: Animation = AnimationUtils.loadAnimation(App.app.applicationContext, R.anim.scale_amplify)
//                view.clearAnimation()
//                view.startAnimation(animations)
            }
        }
    }

    override fun setNoFocusData(view: View, data: RemoteDeviceModel?, position: Int) {
        if(layoutType != 1){
            data?.apply {
//                view.tvName.setTextColor(Color.parseColor("#88ffffff"))
//                view.ivIconBoxLayout.setBackgroundResource(R.drawable.bg_video_unselected)
                ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f)
                    .setDuration(1)
                    .start()
            }
        }
    }

    override fun onViewRecycled(holder: BaseHolder) {
        Glide.with(mContext).clear(holder.itemView.ivIcon)
        //holder.itemView.ivIcon.setImageResource(R.mipmap.ic_home_device_loading)
        super.onViewRecycled(holder)
    }

    override fun getItemLayoutID(viewType: Int): Int {
        return R.layout.item_device
    }

}