package com.wuji.tv.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.admin.libcommon.commonn.FragmentKeyEventListener
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment
import io.sdvn.socket.SDVNApi
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity()/*, CoroutineScope by MainScope()*/ {

    private lateinit var listener: FragmentKeyEventListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun setFragmentKeyEventListener(listener: FragmentKeyEventListener) {
        this.listener = listener
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val mMainNavFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.homeNvFragment)
        val fragment = mMainNavFragment!!.childFragmentManager.primaryNavigationFragment as? BaseFragment
        if(fragment?.dispatchKeyEvent(event) == true){
            return true
        }
        return super.dispatchKeyEvent(event)
    }


    override fun onDestroy() {
        super.onDestroy()
        SDVNApi.getInstance().destroy()
        exitProcess(0)
    }
}
