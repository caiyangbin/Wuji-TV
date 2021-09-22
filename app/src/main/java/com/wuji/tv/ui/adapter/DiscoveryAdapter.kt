package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_discovery.view.*
import com.wuji.tv.R
import com.wuji.tv.model.DiscoveryModel


class DiscoveryAdapter(apps: ArrayList<DiscoveryModel>?) :
    com.wuji.tv.common.BaseRvAdapter<DiscoveryModel>(apps) {
    override fun setData(view: View, data: DiscoveryModel?, position: Int) {
        view.tvName.setTextColor(Color.WHITE)
        view.tvContent.setTextColor(Color.WHITE)
        view.tvTime.setTextColor(Color.GRAY)
        view.tvName.text = data?.name
        view.tvContent.text = data?.content
        view.tvTime.text = data?.timeString

        when(position){
            0->{
                Glide.with(mContext).load(R.mipmap.bonita1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.bonita2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.bonita3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.bonita4).into(view.ivPoster3)
            }
            1->{
                Glide.with(mContext).load(R.mipmap.gary1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.gary2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.gary3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.gary4).into(view.ivPoster3)
            }
            2->{

                Glide.with(mContext).load(R.mipmap.linner1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.linner2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.linner3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.linner4).into(view.ivPoster3)
            }
            3->{

                Glide.with(mContext).load(R.mipmap.peter1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.peter2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.peter3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.peter4).into(view.ivPoster3)
            }

            4->{

                Glide.with(mContext).load(R.mipmap.rose1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.rose2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.rose3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.rose4).into(view.ivPoster3)
            }
            5->{

                Glide.with(mContext).load(R.mipmap.hill1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.hill2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.hill3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.hill4).into(view.ivPoster3)
            }
            6->{

                Glide.with(mContext).load(R.mipmap.alex1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.alex2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.alex3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.alex4).into(view.ivPoster3)
            }
            7->{

                Glide.with(mContext).load(R.mipmap.jason1).into(view.ivIcon)
                Glide.with(mContext).load(R.mipmap.jason2).into(view.ivPoster1)
                Glide.with(mContext).load(R.mipmap.jason3).into(view.ivPoster2)
                Glide.with(mContext).load(R.mipmap.jason4).into(view.ivPoster3)
            }
        }

    }

    override fun setFocusData(view: View, data: DiscoveryModel?, position: Int) {
        view.tvName.setTextColor(Color.BLACK)
        view.tvContent.setTextColor(Color.BLACK)
        view.tvTime.setTextColor(Color.GRAY)
        view.setBackgroundColor(Color.WHITE)
    }

    override fun setNoFocusData(view: View, data: DiscoveryModel?, position: Int) {
        view.tvName.setTextColor(Color.WHITE)
        view.tvContent.setTextColor(Color.WHITE)
        view.tvTime.setTextColor(Color.GRAY)
        view.setBackgroundColor(Color.parseColor("#171616"))
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_discovery
}