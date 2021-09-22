package com.wuji.tv.ui.adapter

import android.view.View
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_device.view.*
import com.wuji.tv.R
import com.wuji.tv.model.RemoteNetworkModel
import com.wuji.tv.utils.BitmapUtils


class NetworkAdapter(devices: ArrayList<RemoteNetworkModel>?) :
    com.wuji.tv.common.BaseRvAdapter<RemoteNetworkModel>(devices) {

    override fun setData(view: View, data: RemoteNetworkModel?, position: Int) {
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
            view.tvName.text = sdvnDevice?.name
            if(!movie_poster_cover.isNullOrEmpty()){
                BitmapUtils().loadingAndSaveImg(movie_poster_cover,view.ivIcon,
                    "${mContext.filesDir?.path}/images/poster/",
                    session,sdvnDevice!!.vip,
                    sdvnDevice!!.id, mContext,R.mipmap.ic_home_device,R.mipmap.ic_home_device_loading)
            }
        }
    }

    override fun setFocusData(view: View, data: RemoteNetworkModel?, position: Int) {
        data?.apply {
//            view.cardView.setCardBackgroundColor(mContext.getColorEx(R.color.focus_bg))
//            view.tvName.setTextColor(Color.BLACK)
//            view.tvContent.setTextColor(Color.BLACK)
            ViewCompat.animate(view).scaleX(1.15f).scaleY(1.15f).translationZ(1.15f)
                .setDuration(100)
                .start()
        }
    }

    override fun setNoFocusData(view: View, data: RemoteNetworkModel?, position: Int) {
        data?.apply {
//            view.cardView.setCardBackgroundColor(mContext.getColorEx(R.color.normal_bg))
//            view.tvName.setTextColor(Color.WHITE)
//            view.tvContent.setTextColor(Color.WHITE)
            ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(100)
                .start()
        }
    }

    override fun onViewRecycled(holder: BaseHolder) {
        Glide.with(mContext).clear(holder.itemView.ivIcon)
        super.onViewRecycled(holder)
    }

    override fun getItemLayoutID(viewType: Int): Int {
        return R.layout.item_device
    }

}