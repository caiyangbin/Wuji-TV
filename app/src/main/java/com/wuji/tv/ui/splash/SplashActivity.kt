package com.wuji.tv.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wuji.tv.R
import com.wuji.tv.ui.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.doAsync

class SplashActivity : AppCompatActivity() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initView()
        initData()
    }

    private fun initView() {
        viewPager.offscreenPageLimit = 5
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager!!).apply {
            viewPager.adapter = this
        }
        doAsync {
            val datas = ArrayList<Fragment>()
            datas.add(WelcomeFragment())
            datas.add(RegisterFragment())
            datas.add(BindLocalDeviceFragment())
            datas.add(BindRemoteDeviceFragment())
            runOnUiThread {
                viewPagerAdapter.setFragment(datas)
            }
        }

    }


    private fun initData() {
    }


    fun nextPage() {
        viewPager.currentItem += 1
    }

    fun previousPage() {
        viewPager.currentItem -= 1
    }


}
