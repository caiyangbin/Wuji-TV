package com.wuji.tv.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    //   BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT -- 只加载当前显示的fragment

    private var fragments = ArrayList<Fragment>()


    fun setFragment(fragments: ArrayList<Fragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

}