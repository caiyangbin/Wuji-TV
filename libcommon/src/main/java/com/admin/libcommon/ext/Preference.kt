package com.admin.libcommon.ext

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class Preference<T>(val context: Context, val name: String, private val default: T) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("SP", Context.MODE_PRIVATE)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        getSharedPreferences(name)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        putSharedPreferences(name, value)


    private fun putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Float -> putFloat(name, value)
            is Long -> putLong(name, value)
            is Boolean -> putBoolean(name, value)
            is String -> putString(name, value)
            else -> throw IllegalArgumentException("SharedPreference can't be save this type")
        }.apply()
    }

    private fun getSharedPreferences(name: String): T =
        when (default) {
            is Int -> prefs.getInt(name, default)
            is Float -> prefs.getFloat(name, default)
            is Long -> prefs.getLong(name, default)
            is Boolean -> prefs.getBoolean(name, default)
            is String -> prefs.getString(name, default)
            else -> throw IllegalArgumentException("SharedPreference can't be get this type")
        } as T
}
