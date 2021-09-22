package com.admin.libcommon.ext

/**
 * @author admin
 * @date 2019/5/15-09:29
 */


fun <T> ArrayList<T>?.isNotNullOrEmpty(): Boolean {
  return this != null && this.size > 0
}