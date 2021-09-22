package com.wuji.tv.model

import java.io.Serializable

data class PosterBundleModel(
    val token: String,
    val sessionLocal: String,
    val ip: String,
    val deviceId: String,
    val deviceName: String
) : Serializable