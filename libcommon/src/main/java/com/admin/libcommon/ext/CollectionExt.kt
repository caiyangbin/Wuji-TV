package com.admin.libcommon.ext

/**
 * Collection相关的ext
 * create by admin on 2019/12/26 10:59
 */

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}



