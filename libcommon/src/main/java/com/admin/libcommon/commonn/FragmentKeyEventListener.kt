package com.admin.libcommon.commonn

import android.view.KeyEvent

/**
 * 用于 fragment 监听 activity dispatchKeyEvent
 * Create by admin on 2020/4/2-14:43
 */
interface FragmentKeyEventListener {
  fun dispatchKeyEvent(event: KeyEvent): Boolean
}