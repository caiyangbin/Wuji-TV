package com.wuji.tv.model

import android.graphics.drawable.Drawable

data class AppsModel(
    var title: String,
    var content: String? = null,
    var icon: Drawable? = null
)