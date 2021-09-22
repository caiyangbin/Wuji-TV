package com.admin.libcommon.ext

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import java.text.SimpleDateFormat


/**
 * 通用扩展
 * Create at 2018/9/7--10:05 by admin
 */


/**
 * 获取系统属性
 */
@SuppressLint("PrivateApi")
fun getProperty(key: String, defaultValue: String): String {
    var value: String = defaultValue
    try {
        val clazz = Class.forName("android.os.SystemProperties")
        val get = clazz.getMethod("get", String::class.java, String::class.java)
        value = get.invoke(clazz, key, value) as String
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return value
    }
}


/**
 * 获取系统属性
 */
@SuppressLint("PrivateApi")
fun getPropertyInt(key: String, defaultValue: Int): Int {
    var value: Int = defaultValue
    try {
        val clazz = Class.forName("android.os.SystemProperties")
        val get = clazz.getMethod("getInt", String::class.java, Int::class.java)
        value = get.invoke(clazz, key, value) as Int
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return value
    }
}


/**
 * 设置系统属性
 */
@SuppressLint("PrivateApi")
fun setProperty(key: String, value: String) {
    try {
        val clazz = Class.forName("android.os.SystemProperties")
        val set = clazz.getMethod("set", String::class.java, String::class.java)
        set.invoke(clazz, key, value)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 获取当前时间
 */
@SuppressLint("SimpleDateFormat")
fun geCuurentTimeString(): String {
    val time = System.currentTimeMillis()
    //设置日期格式
    val df = SimpleDateFormat("MM-dd HH:mm:ss:SSS")
    return df.format(time)
}

/**获取蓝牙实际连接状态*/
fun getBlueToothConnectedStatus(): Boolean {
    var isConnected = false
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    // 蓝牙适配器是否存在，即是否发生了错误
    if (bluetoothAdapter == null) {
        // error
        return false
    } else if (bluetoothAdapter.isEnabled) {
        // 可操控蓝牙设备，如带播放暂停功能的蓝牙耳机
        val a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP)
        // 蓝牙头戴式耳机，支持语音输入输出
        val headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)
        // 蓝牙穿戴式设备
        val health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH)
        // 查看是否蓝牙是否连接到三种设备的一种，以此来判断是否处于连接状态还是打开并没有连接的状态
        var flag = -1
        when {
            a2dp == BluetoothProfile.STATE_CONNECTED -> flag = a2dp
            headset == BluetoothProfile.STATE_CONNECTED -> flag = headset
            health == BluetoothProfile.STATE_CONNECTED -> flag = health
        }
        // 说明连接上了三种设备的一种
        if (flag != -1) {
            isConnected = true
        }
    }
    return isConnected
}









