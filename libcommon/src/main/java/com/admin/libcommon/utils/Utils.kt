package com.admin.libcommon.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.util.*

@SuppressLint("NewApi")
object Utils {

  fun getSDcardPath(): String? {
    val file = Environment.getExternalStorageDirectory()
    return file?.absolutePath
  }

  fun getExternalCacheDir(context: Context, path: String): File? {
    var appCacheDir: File? = null
    val externalStorageState = try {
      Environment.getExternalStorageState()
    } catch (e: NullPointerException) {
      ""
    }

    if (Environment.MEDIA_MOUNTED == externalStorageState) {
      val dataDir = File(File(Environment.getExternalStorageDirectory(), "Android"), "data")
      appCacheDir = File(File(dataDir, context.packageName), path)
      if (!appCacheDir.exists()) {
        if (appCacheDir.mkdirs()) {
          try {
            File(appCacheDir, ".nomedia").createNewFile()
          } catch (e: IOException) {
            e.printStackTrace()
          }

        } else {
          appCacheDir = null
        }
      }
    }
    if (appCacheDir == null) {
      appCacheDir = context.cacheDir
      if (appCacheDir != null) {
        appCacheDir = File(appCacheDir, path)
      }
    }
    return appCacheDir
  }

  fun readHDBitMap(
    context: Context,
    imageFile: File,
    screenWidth: Int,
    screentHeight: Int
  ): Bitmap? {
    val opt: BitmapFactory.Options
    var result: Bitmap? = null
    try {
      // 获取资源图片
      opt = BitmapFactory.Options()
      var inputStream: InputStream = FileInputStream(imageFile)
      opt.inJustDecodeBounds = true // 设为 false
      BitmapFactory.decodeStream(inputStream, null, opt)
      val width = opt.outWidth
      val height = opt.outHeight
      var sampleSize = 1
      if (height > screentHeight || width > screenWidth) {
        val heightRatio = Math.round(height.toFloat() / screentHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / screenWidth.toFloat())
        sampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (screenWidth * screentHeight).toFloat()

        while (totalPixels / (sampleSize * sampleSize) > totalReqPixelsCap) {
          sampleSize++
        }
      }
      opt.inSampleSize = Math.max(sampleSize, 1)
      opt.inJustDecodeBounds = false
      opt.inPurgeable = true
      opt.inInputShareable = true
      inputStream.close()
      inputStream = FileInputStream(imageFile)
      result = BitmapFactory.decodeStream(inputStream, null, opt)
      inputStream.close()
      return result
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  private val AUTO_LOGO = "Autologo"

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
   fun getExSdCardPath(
    path: String, filterPaths: List<String>,
    storageManager: StorageManager
  ): List<File> {
    val storageDir = File(path)
    val sdFiles = ArrayList<File>()
    val files = storageDir.listFiles { dir, filename ->
      var filename = filename
      filename = filename.toLowerCase(Locale.getDefault())
      if ((filename.contains("ext") || filename.contains("sd") || filename.contains("card")
            || filename.contains("udisk") || filename.contains("usb")) && filename != "sdcard"
      ) {
        true
      } else false
    }
    if (files != null && files.size > 0) {
      for (i in files.indices) {
        val f = files[i]
        val fs = f.list()
        val fPath = f.absolutePath
        try {
          val version = Build.VERSION.SDK_INT
          if (version >= 19) {
            val stateStr = Environment.getExternalStorageState(File(fPath))
            if (!filterPaths.contains(fPath) && stateStr == Environment.MEDIA_MOUNTED) {
              sdFiles.add(f)
            }
          } else {
            if (f.exists() && f.canExecute() && !filterPaths.contains(fPath) && fs != null
              && fs.size > 0
            ) {
              sdFiles.add(f)
            }
          }

        } catch (e: Exception) {
        }

      }
    }
    return sdFiles
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  fun getExtSDcardPaths(activity: Context): List<File>? {
    var extfiles: List<File>? = null
    val storageManager = activity.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
    extfiles = getExtSdcardPathsForStorageManager(sdcardPath, storageManager)

    if (extfiles == null || extfiles.size == 0) {
      // 当前
      if (sdcardPath == "/storage/emulated/0") {
        val filterPaths = ArrayList<String>()
        filterPaths.add(sdcardPath)
        filterPaths.add("/storage/sdcard0")
        extfiles = getExSdCardPath("/storage", filterPaths, storageManager)
      } else {
        val filterPaths = ArrayList<String>()
        filterPaths.add(sdcardPath)
        val index = sdcardPath.lastIndexOf("/")
        if (index > 0) {
          extfiles = getExSdCardPath(sdcardPath.substring(0, index), filterPaths, storageManager)
        }
      }
      if (extfiles == null || extfiles.size == 0) {
        val sdFiles = ArrayList<File>()
        val file = File("/mnt/")
        val mntFiles = file.listFiles { dir, filename ->
          var filename = filename
          filename = filename.toLowerCase(Locale.getDefault())
          (filename.contains("ext") || filename.contains("sd") || filename.contains("card")
              || filename.contains("udisk") || filename.contains("usb")) && filename != "sdcard"
        }
        if (mntFiles != null && mntFiles.size > 0) {
          for (i in mntFiles.indices) {
            val f = mntFiles[i]
            val fs = f.list()
            val fPath = f.absolutePath
            try {
              val version = Build.VERSION.SDK_INT
              if (version >= 19) {
                val stateStr = Environment.getExternalStorageState(File(fPath))
                if (!"/mnt/sdcard".equals(
                    fPath,
                    ignoreCase = true
                  ) && stateStr == Environment.MEDIA_MOUNTED
                ) {
                  sdFiles.add(f)
                }
              } else {
                if (f.exists() && f.canExecute() && !"/mnt/sdcard".equals(
                    fPath,
                    ignoreCase = true
                  ) && fs != null
                  && fs.size > 0
                ) {
                  sdFiles.add(f)
                }
              }
            } catch (e: Exception) {

            }

          }
          extfiles = sdFiles
        }
      }
    }
    return extfiles
  }

   fun getExtSdcardPathsForStorageManager(
    sdcardPath: String,
    storageManager: StorageManager
  ): List<File>? {
    var extsdcards: MutableList<File>? = null
    try {
      val paramClasses = arrayOf<Class<*>>()
      val getVolumePathsMethod =
        StorageManager::class.java.getMethod("getVolumePaths", *paramClasses)
      val getVolumeState =
        StorageManager::class.java.getMethod("getVolumeState", String::class.java)
      getVolumeState.isAccessible = true
      getVolumePathsMethod.isAccessible = true
      val params = arrayOf<Any>()
      val invoke = getVolumePathsMethod.invoke(storageManager, *params)
      extsdcards = ArrayList()
      for (i in 0 until (invoke as Array<String>).size) {
        val path = invoke[i]
        val mount = getVolumeState.invoke(storageManager, path) as String
        if (sdcardPath != path && Environment.MEDIA_MOUNTED == mount) {
          extsdcards.add(File(path))
        }
      }
    } catch (e1: NoSuchMethodException) {
      e1.printStackTrace()
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    }

    return extsdcards
  }


  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  fun getFileByAutoLogo(context: Context, fileName: String): File? {
    if (TextUtils.isEmpty(fileName)) {
      return null
    }
    val localFiles = getExtSDcardPaths(context) // 获取外置device的list
    // usb
    val usbs = ArrayList<String>()
    // 外置sdcard
    var extSdcard: String? = null
    // sata
    val satas = ArrayList<String>()
    for (i in localFiles!!.indices) {
      val serverFile = localFiles?.get(i)
      val name = serverFile.name
      val path = serverFile.absolutePath
      if (name == "sdcard1" || name == "mmcblk1p1") {// SD卡
        extSdcard = path
      } else if (name.matches("^udisk[0-9]*".toRegex()) || name.matches("^sd[a-z][0-9]*".toRegex())) {
        if (isSata(path)) {// 硬盘
          satas.add(path)
        } else {// USB存储
          usbs.add(path)
        }
      }
    }

    //根据文件名来查找文件
    if (usbs != null && usbs.size > 0) {
      for (j in usbs.indices) {
        val imageFile = File(usbs[j] + "/" + AUTO_LOGO, fileName)
        val autoDir = File(usbs[j] + "/" + AUTO_LOGO)
        if (autoDir.exists()) {
          return if (imageFile.exists()) {
            imageFile
          } else null
        }
      }
    }

    if (!TextUtils.isEmpty(extSdcard)) {
      val imageFile = File("$extSdcard/$AUTO_LOGO", fileName)
      val autoDir = File("$extSdcard/$AUTO_LOGO")
      if (autoDir.exists()) {
        return if (imageFile.exists()) {
          imageFile
        } else null
      }
    }

    if (satas != null && satas.size > 0) {
      for (j in satas.indices) {
        val imageFile = File(satas[j] + "/" + AUTO_LOGO, fileName)

        val autoDir = File(satas[j] + "/" + AUTO_LOGO)
        if (autoDir.exists()) {
          return if (imageFile.exists()) {
            imageFile
          } else null
        }
      }
    }

    return null
  }

   fun isSata(path: String): Boolean {
    var path = path
    if (TextUtils.isEmpty(path)) {
      return false
    }
    if (path.startsWith("/")) {
      path = path.substring(path.indexOf("/") + 1)
    }
    if (path.indexOf("/") == path.lastIndexOf("/")) {
      path = path.substring(path.indexOf("/") + 1)
    } else {
      path = path.substring(path.indexOf("/") + 1, path.lastIndexOf("/"))
    }
    val type = ProcessUtil.do_exec("busybox readlink -fnv /sys/block/$path")
    if (!TextUtils.isEmpty(type)) {
      val index = type.indexOf("ahci")
      if (index >= 0) {
        return true
      }
    }
    return false
  }

  /**
   * Get the value for the given key.
   *
   * @return an empty string if the key isn't found
   * @throws IllegalArgumentException if the key exceeds 32 characters
   */
   fun get(key: String): String {
    var result = ""
    try {
      val c = Class.forName("android.os.SystemProperties")

      val get = c.getMethod("get", String::class.java)
      result = get.invoke(c, key) as String

    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    } catch (e: NoSuchMethodException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    }

    return result
  }

  /**
   * Set the value for the given key.
   *
   * @throws IllegalArgumentException if the key exceeds 32 characters
   * @throws IllegalArgumentException if the value exceeds 92 characters
   */
   fun set(key: String, value: String) {
    try {
      val c = Class.forName("android.os.SystemProperties")
      val set = c.getMethod("set", String::class.java, String::class.java)
      set.invoke(c, key, value)
    } catch (e: ClassNotFoundException) {
      e.printStackTrace()
    } catch (e: NoSuchMethodException) {
      e.printStackTrace()
    } catch (e: IllegalAccessException) {
      e.printStackTrace()
    } catch (e: IllegalArgumentException) {
      e.printStackTrace()
    } catch (e: InvocationTargetException) {
      e.printStackTrace()
    }
  }
}