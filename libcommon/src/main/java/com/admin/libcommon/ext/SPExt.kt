package com.admin.libcommon.ext

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object SPExt {

    const val PREFS_NAME: String = "SP"

    fun <T> notNullSingleValue() = NotNullSingleValueVar<T>()

    fun <T> preference(context: Context, name: String, default: T) =
        Preference(context, name, default)

    class NotNullSingleValueVar<T> : ReadWriteProperty<Any?, T> {
        private var value: T? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return value ?: throw IllegalStateException("${property.name} not initialized")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value =
                    if (this.value == null) value else throw IllegalStateException("${property.name} already initialized")
        }
    }

    class Preference<T>(private val context: Context, private val name: String, private val default: T) : ReadWriteProperty<Any?, T> {
        private val prefs: SharedPreferences by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return findPreference(name, default)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            putPreference(name, value)
        }

        @Suppress("UNCHECKED_CAST")
        private fun findPreference(name: String, default: T): T = with(prefs) {
            val res: Any = when (default) {
                is Long -> getLong(name, default)
                is String -> getString(name, default) as String
                is Int -> getInt(name, default)
                is Boolean -> getBoolean(name, default)
                is Float -> getFloat(name, default)
                else -> throw IllegalArgumentException("This type can be saved into Preferences")
            }
            res as T
        }

        private fun putPreference(name: String, value: T) = with(prefs.edit()) {
            when (value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> throw IllegalArgumentException("This type can't be saved into Preferences")
            }.apply()
        }
    }
}