package com.admin.libcommon.ext

import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.storage.StorageManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import java.lang.reflect.InvocationTargetException


/**
 * 获取String字符串
 */
fun Context.getStringEx(resId: Int): String = this.resources.getString(resId)

/**
 * 获取Color颜色值
 */
fun Context.getColorEx(resId: Int): Int = this.resources.getColor(resId)

/**
 * 获取Drawable id值
 */
fun Context.getDrwableEx(resId: Int): Drawable = resources.getDrawable(resId)


//----------屏幕尺寸----------
inline val Context.displayWidth
  get() = resources.displayMetrics.widthPixels

inline val Context.displayHeight
  get() = resources.displayMetrics.heightPixels

fun Context.inflateLayout(
  @LayoutRes layoutResId: Int, parent: ViewGroup? = null,
  attachToRoot: Boolean = false
) = LayoutInflater.from(this).inflate(layoutResId, parent, attachToRoot)


//----------尺寸转换----------
fun Context.dp2px(dpValue: Int): Int {
  val scale = resources.displayMetrics.density
  return (dpValue * scale + 0.5f).toInt()
}

fun Context.px2dp(pxValue: Float): Int {
  val scale = resources.displayMetrics.density
  return (pxValue / scale + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
  val scale = resources.displayMetrics.scaledDensity
  return (spValue * scale + 0.5f).toInt()
}

fun Context.px2sp(pxValue: Float): Int {
  val scale = resources.displayMetrics.scaledDensity
  return (pxValue / scale + 0.5f).toInt()
}

fun Context.wifiStatus(): Boolean {
  val conn = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI) ?: return false
  return networkInfo.isConnected
}

fun Context.ethernetStatus(): Boolean {
  val conn = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
  return networkInfo.isConnected
}

fun Context.blueToothStatus(): Boolean {
  val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
  bluetoothManager.adapter ?: return false
  return bluetoothManager.adapter.isEnabled
}

fun Context.storageManager(): StorageManager {
  return this.getSystemService(Context.STORAGE_SERVICE) as StorageManager
}


/**是否有外接usb*/
fun Context.usbStatus(): Boolean {
  var isHaveSDCard = false
  val storageManager = this.getSystemService(Context.STORAGE_SERVICE) as StorageManager
  try {
    val methodVolumeList = StorageManager::class.java.getMethod("getVolumeList")
    methodVolumeList.isAccessible = true
    val list = methodVolumeList.invoke(storageManager) as Array<*>?
    list?.forEach {
      val isRemovable = it?.javaClass?.getMethod("isRemovable")?.invoke(it) as Boolean
      //是否可卸载，内置SD卡无法卸载，外置SD卡可以卸载，据此可判断是否存在外置SD卡
      if (isRemovable) {
        isHaveSDCard = true
        return@forEach
//                val status = it.javaClass.getMethod("getState").invoke(it)
//                if (status != null) {
//                    if (status == Environment.MEDIA_MOUNTED) {
//                        isHaveSDCard = true
//                        return@forEach
//                    }
//                }
      }
    }
  } catch (e: NoSuchMethodException) {
    e.printStackTrace()
  } catch (e: IllegalAccessException) {
    e.printStackTrace()
  } catch (e: InvocationTargetException) {
    e.printStackTrace()
  }
  return isHaveSDCard
}

fun Context.hasInstallApplication(pkgName: String?): Boolean {
  if (TextUtils.isEmpty(pkgName)) {
    return false
  }
  try {
    val applicationInfo = this.packageManager.getApplicationInfo(packageName, 0)
    if (applicationInfo != null) {
      return true
    }
  } catch (e: Exception) {
  }
  return false
}