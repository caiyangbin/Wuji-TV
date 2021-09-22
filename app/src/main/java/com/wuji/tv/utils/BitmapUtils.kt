package com.wuji.tv.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class BitmapUtils {

    fun loadingAndSaveImg(
        imagePath: String?, view: ImageView, filesDirPath: String,
        session: String, ip: String, id: String, context: Context,
        errorImgId: Int = -1, loadingImgId: Int = -1,isThumbnail: Boolean = true,roundingRadius: Int = 0
    )
    {
        if(!FileUtils.isNotEmpty(imagePath)) {
            if(errorImgId != -1){
                view.setImageResource(errorImgId)
            }
            return
        }
        if(loadingImgId != -1){
            view.setImageResource(loadingImgId)
        }
        val ftPath = "${id}${imagePath}".replace("/", "-")
        if(File(filesDirPath, ftPath).exists())
        {
            if(roundingRadius > 0){
                Glide
                    .with(context)
                    .load(File(filesDirPath, ftPath))
                    .skipMemoryCache(true)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
                    .into(view)
            }
            else{
                Glide
                    .with(context)
                    .load(File(filesDirPath, ftPath))
                    .skipMemoryCache(true)
                    .into(view)
            }
        }
        else
        {
            if(session.isEmpty()){
                if(errorImgId != -1){
                    view.setImageResource(errorImgId)
                }
                return
            }
            val url =
                if (isThumbnail)
                    "http://${ip}/file/thumbnail?session=${session}&path=${imagePath}&size=max"
                else
                    "http://${ip}/file/download?session=${session}&path=${imagePath}"
            if(roundingRadius > 0){
                Glide
                    .with(context)
                    .asDrawable()
                    .load(url)
                    .skipMemoryCache(true)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
                    .listener(MyRequestListener(ftPath, filesDirPath,view,errorImgId,context))
                    .into(view)
            }
            else{
                Glide
                    .with(context)
                    .asDrawable()
                    .load(url)
                    .skipMemoryCache(true)
                    .listener(MyRequestListener(ftPath, filesDirPath,view,errorImgId,context))
                    .into(view)
            }
        }
    }
    class MyRequestListener(ftPath: String, filesDirPath: String, view: ImageView,
                            errorImgId: Int = -1, context: Context) : RequestListener<Drawable> {

        val ftPath = ftPath
        val filesDirPath = filesDirPath
        val errorImgId = errorImgId
        val view = view
        val context = context

        override fun onLoadFailed(
            e: GlideException?, model: Any?,
            target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
            if(errorImgId != -1){
                view.setImageResource(errorImgId)
            }
            return false
        }

        override fun onResourceReady(
            resource: Drawable?, model: Any?, target: Target<Drawable>?,
            dataSource: DataSource?, isFirstResource: Boolean
        ): Boolean {
            if (resource != null) {
                BitmapUtils().saveDrawable(filesDirPath, ftPath, resource)
            }
            return false
        }
    }

    fun loadingAndSaveImgAddAnimate(
        imagePath: String?, view: ImageView,viewUp: ImageView, filesDirPath: String,
        session: String, ip: String, id: String, context: Context,
        errorImgId: Int = -1, loadingImgId: Int = -1,isThumbnail: Boolean = true
    )
    {
        if(!FileUtils.isNotEmpty(imagePath)) {
            if(errorImgId != -1){
                setImageResourceAddAnimate(view,viewUp,errorImgId)
            }
            return
        }
        if(loadingImgId != -1){
            view.setImageResource(loadingImgId)
        }
        val ftPath = "${id}${imagePath}".replace("/", "-")
        if(File(filesDirPath, ftPath).exists())
        {
            Glide
                .with(context)
                .load(File(filesDirPath, ftPath))
                .skipMemoryCache(true)
                .listener(MyRequestListenerAddAnimate(ftPath, filesDirPath,view,viewUp,errorImgId,context,false))
                .into(viewUp)
        }
        else
        {
            if(session.isEmpty()){
                if(errorImgId != -1){
                    setImageResourceAddAnimate(view,viewUp,errorImgId)
                }
                return
            }
            val url =
                if (isThumbnail)
                    "http://${ip}/file/thumbnail?session=${session}&path=${imagePath}&size=max"
                else
                    "http://${ip}/file/download?session=${session}&path=${imagePath}"
            Glide
                .with(context)
                .asDrawable()
                .load(url)
                .skipMemoryCache(true)
                .listener(MyRequestListenerAddAnimate(ftPath, filesDirPath,view,viewUp,errorImgId,context,true))
                .into(viewUp)
        }
    }
    class MyRequestListenerAddAnimate(ftPath: String, filesDirPath: String, view: ImageView,viewUp: ImageView,
                            errorImgId: Int = -1, context: Context,isLoading: Boolean) : RequestListener<Drawable> {

        val ftPath = ftPath
        val filesDirPath = filesDirPath
        val errorImgId = errorImgId
        val view = view
        val viewUp = viewUp
        val context = context
        val isLoading = isLoading

        override fun onLoadFailed(
            e: GlideException?, model: Any?,
            target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
            if(errorImgId != -1){
                BitmapUtils().setImageResourceAddAnimate(view,viewUp,errorImgId)
            }
            return false
        }

        override fun onResourceReady(
            resource: Drawable?, model: Any?, target: Target<Drawable>?,
            dataSource: DataSource?, isFirstResource: Boolean
        ): Boolean {
            if (resource != null) {
                if (isLoading){
                    BitmapUtils().saveDrawable(filesDirPath, ftPath, resource)
                }
                BitmapUtils().setImageAddAnimate(view,viewUp,resource)
            }
            return false
        }
    }

    private fun setImageResourceAddAnimate(imgView: ImageView,imgViewUp: ImageView,errorImgId: Int){
        imgViewUp.setImageResource(errorImgId)
        ViewCompat.animate(imgViewUp).scaleX(1f).scaleY(1f).translationZ(1f)
            .setDuration(150).setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                }

                override fun onAnimationEnd(view: View?) {
                    imgView.setImageResource(errorImgId)
                    ViewCompat.animate(imgViewUp).scaleX(0f).scaleY(0f).translationZ(0f)
                        .setDuration(1).setListener(object : ViewPropertyAnimatorListener {
                            override fun onAnimationStart(view: View?) {
                            }

                            override fun onAnimationEnd(view: View?) {
                            }

                            override fun onAnimationCancel(view: View?) {
                            }
                        })
                        .start()
                }

                override fun onAnimationCancel(view: View?) {
                }
            })
            .start()
    }

    private fun setImageAddAnimate(imgView: ImageView,imgViewUp: ImageView,img: Drawable){
        ViewCompat.animate(imgViewUp).scaleX(1f).scaleY(1f).translationZ(1f)
            .setDuration(150).setListener(object : ViewPropertyAnimatorListener {
                override fun onAnimationStart(view: View?) {
                }

                override fun onAnimationEnd(view: View?) {
                    imgView.setImageDrawable(img)
                    ViewCompat.animate(imgViewUp).scaleX(0f).scaleY(0f).translationZ(0f)
                        .setDuration(1).setListener(object : ViewPropertyAnimatorListener {
                            override fun onAnimationStart(view: View?) {
                            }

                            override fun onAnimationEnd(view: View?) {
                            }

                            override fun onAnimationCancel(view: View?) {
                            }
                        })
                        .start()
                }

                override fun onAnimationCancel(view: View?) {
                }
            })
            .start()
    }

    interface DownloadListener{
        fun downloadListener(isSuccess: Boolean)
    }

    fun downloadImg(
        context: Context, imagePath: String, filesDirPath: String,
        session: String, ip: String, id: String, downloadListener: DownloadListener?,
        isThumbnail: Boolean = false
    )
    {
        val ftPath = "${id}${imagePath}".replace("/", "-")
        if(File(filesDirPath, ftPath).exists()) {
            downloadListener?.downloadListener(true)
        }
        else {
            val url =
                if (isThumbnail)
                    "http://${ip}/file/thumbnail?session=${session}&size=max&path=${imagePath}"
                else
                    "http://${ip}/file/download?session=${session}&path=${imagePath}"
            Glide
                .with(context)
                .asDrawable()
                .load(url)
                .skipMemoryCache(true)
                .into(MySimpleTarget(ftPath,filesDirPath,downloadListener))
        }
    }
    class MySimpleTarget(ftPath: String, filesDirPath: String,downloadListener: DownloadListener?) :SimpleTarget<Drawable>(){
        val ftPath = ftPath
        val filesDirPath = filesDirPath
        val downloadListener: DownloadListener? = downloadListener
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            BitmapUtils().saveDrawable(filesDirPath, ftPath, resource)
            downloadListener?.downloadListener(true)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            downloadListener?.downloadListener(false)
            super.onLoadFailed(errorDrawable)
        }
    }

    class MyLocalRequestListener(ftPath: String, filesDirPath: String,downloadListener: DownloadListener?) : RequestListener<Drawable> {

        val ftPath = ftPath
        val filesDirPath = filesDirPath
        val downloadListener: DownloadListener? = downloadListener

        override fun onLoadFailed(
            e: GlideException?, model: Any?,
            target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
            downloadListener?.downloadListener(false)
            return false
        }

        override fun onResourceReady(
            resource: Drawable?, model: Any?, target: Target<Drawable>?,
            dataSource: DataSource?, isFirstResource: Boolean
        ): Boolean {
            if (resource != null) {
                BitmapUtils().saveDrawable(filesDirPath, ftPath, resource)
                downloadListener?.downloadListener(true)
            }
            else {
                downloadListener?.downloadListener(false)
            }
            return false
        }
    }

    fun saveBitmap(targetPath: String, bitmapPath: String, bm: Bitmap) {
        val dir = File(targetPath)
        if (!dir.exists())
            dir.mkdirs()
        val saveFile = File(targetPath, bitmapPath)
        saveFile.createNewFile()
        val saveImgOut = FileOutputStream(saveFile)
        try {
            if(bitmapPath.endsWith("png", true)){
                bm.compress(Bitmap.CompressFormat.PNG, 100, saveImgOut)
            }
            else{
                bm.compress(Bitmap.CompressFormat.JPEG, 100, saveImgOut)
            }
            saveImgOut.flush()
            saveImgOut.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
            saveImgOut.flush()
            saveImgOut.close()
        }
    }

    fun saveDrawable(targetPath: String, bitmapPath: String, drawable: Drawable) {
        var bm = drawableToBitamp(drawable)
        if (bm != null) {
            saveBitmap(targetPath,bitmapPath,bm)
            bm = null
        }
    }

    fun drawableToBitamp(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }
    fun deleteBitmap(targetPath: String, bitmapPath: String) {
        val dir = File(targetPath)
        if (!dir.exists())
            return
        val saveFile = File(targetPath, bitmapPath)
        if(saveFile.isFile){
            saveFile.delete()
        }
    }

    private fun clearImageDiskCache(context: Context){
        try{
            if(Looper.myLooper() == Looper.getMainLooper()){
                Thread{
                    Glide.get(context).clearDiskCache()
                }.start()
            }else{
                Glide.get(context).clearDiskCache();
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun clearImageMemoryCache(context: Context){
        try{
            if(Looper.myLooper() == Looper.getMainLooper()){
                Glide.get(context).clearMemory();
            }
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().isEmpty()) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val imageExternalCatchDir ="${context.externalCacheDir}${ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR}"
        deleteFolderFile(imageExternalCatchDir, true)
    }


    fun roundDrawableByBitmap(
        bitmap: Bitmap,
        outWidth: Int,
        outHeight: Int,
        radius: Int,
        resources:Resources
    ): Drawable? {

        val widthScale = outWidth * 1.0f / bitmap.width
        val heightScale = outHeight * 1.0f / bitmap.height
        val matrix = Matrix()
        matrix.setScale(widthScale, heightScale)
        val newBt = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, newBt)
        dr.cornerRadius = radius.toFloat()
        dr.setAntiAlias(true)
        return dr
    }

    fun roundDrawableByDrawable(
        drawable: Drawable,
        outWidth: Int,
        outHeight: Int,
        radius: Int,
        resources:Resources
    ): Drawable? {
        val bitmap = drawableToBitamp(drawable) ?: return null
        if (bitmap.width == 0 || bitmap.height == 0) return drawable
        val widthScale = outWidth * 1.0f / bitmap.width
        val heightScale = outHeight * 1.0f / bitmap.height
        val matrix = Matrix()
        matrix.setScale(widthScale, heightScale)
        val newBt = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, newBt)
        dr.cornerRadius = radius.toFloat()
        dr.setAntiAlias(true)
        return dr
    }

    fun roundDrawableByResource(
        resId: Int,
        outWidth: Int,
        outHeight: Int,
        radius: Int,
        context:Context
    ): Drawable? {
        val drawable = ContextCompat.getDrawable(context,resId) ?: return null
        val bitmap = drawableToBitamp(drawable) ?: return null
        val widthScale = outWidth * 1.0f / bitmap.width
        val heightScale = outHeight * 1.0f / bitmap.height
        val matrix = Matrix()
        matrix.setScale(widthScale, heightScale)
        val newBt = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val dr: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, newBt)
        dr.cornerRadius = radius.toFloat()
        dr.setAntiAlias(true)
        return dr
    }
}