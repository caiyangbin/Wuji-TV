package com.admin.libcommonui

import android.content.Context
import android.graphics.Typeface

/**
 * Create by admin on 2020/1/4-14:32
 */
class CommonUIConfig(builder: Builder) {

    companion object {
        lateinit var sTypeface: Typeface
    }

    init {
        sTypeface = Typeface.createFromAsset(builder.context.assets, "fonts/PingFang Regular.ttf")
    }

    class Builder {

        internal lateinit var context: Context

        fun build(): CommonUIConfig {
            return CommonUIConfig(this)
        }

        fun initContext(context: Context): Builder {
            this.context = context
            return this
        }
    }
}