package com.admin.libutils.ext

import com.google.gson.Gson

/**
 * Gson Ext
 * create by admin on 2019/12/26 15:30
 */

inline fun <reified T: Any> Gson.fromJson(json: String): T = this.fromJson(json, T::class.javaObjectType)