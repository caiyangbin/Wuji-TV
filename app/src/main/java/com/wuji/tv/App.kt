package com.wuji.tv

import android.app.Application
import com.jeremyliao.liveeventbus.LiveEventBus
import com.admin.libcommon.net.*
import com.admin.libcommonui.CommonUIConfig
import com.wuji.tv.di.appModule
import com.wuji.tv.widget.CreateApi
import com.tencent.bugly.crashreport.CrashReport
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.https.HttpsUtils
import com.zhy.http.okhttp.log.LoggerInterceptor
import io.sdvn.socket.ResultListener
import io.sdvn.socket.SDVNApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class App : Application() {

    companion object {
        lateinit var app: App
        var api: Api? = null
        var apiLong: Api? = null
        var localApi: Api? = null
        var downloadApi: DownloadApi? = null
        var downloadDbApi: DownloadApi? = null
        var createApi: CreateApi? = null
        var createDbApi: CreateApi? = null
        var ip: String? = null
        var id: String? = null
        var token: String? = null
        var domain: String? = null
        var networkId: String? = null
        var isCircle = false
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        CommonUIConfig.Builder().initContext(this).build()
        initDi()
        initLiveEventBus()
        initSdvnSocket()
        initLocalRetrofit()

        val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .build()
                return@Interceptor chain.proceed(newRequest)
            })
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .addInterceptor(LoggerInterceptor("API"))
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .build()


        OkHttpUtils.initClient(okHttpClient)

        CrashReport.initCrashReport(applicationContext, "3cb0c6df87", false)
    }

    private fun initDi() {
        startKoin {
            androidContext(this@App)
            logger(AndroidLogger())
            modules(
                listOf(
                    appModule
                )
            )
        }
    }

    private fun initLiveEventBus() {
        LiveEventBus.config().supportBroadcast(this).lifecycleObserverAlwaysActive(true)
            .autoClear(true)
    }

    private fun initSdvnSocket() {
        SDVNApi.getInstance().init(this, object : ResultListener {
            override fun onSuccess() {
                SDVNApi.getInstance().setAutoLogin(true)
            }

            override fun onError(result: Int, msg: String?) {
            }
        })
    }

    fun setSdvnDeviceId(id: String?) {
        Companion.id = id
    }

    fun initRemoteRetrofit(baseUrl: String?) {
        if (baseUrl == null) {
            return
        }
        if (api != null) {
            api = null
        }
        if (createApi != null) {
            createApi = null
        }
        ip = baseUrl
        val url = getBaseUrl(baseUrl, "9899")
        val createUrl = getBaseUrl(baseUrl, "9899")
        val createDbUrl = getBaseUrl(baseUrl, "9899")

        api = getRetrofit(url).create(Api::class.java)
        apiLong = Retrofit.Builder()
            .baseUrl(createDbUrl)
            .client(getOkHttpClient(timeOut = 10))
            .addConverterFactory(ReqStringResGsonConverterFactory.create(dateGson))
            .build().create(Api::class.java)
        createApi = Retrofit.Builder()
            .baseUrl(createUrl)
            .client(getOkHttpClient())
            .addConverterFactory(ReqStringResGsonConverterFactory.create(dateGson))
            .build().create(CreateApi::class.java)
        createDbApi = Retrofit.Builder()
            .baseUrl(createDbUrl)
            .client(getOkHttpClient(timeOut = 10))
            .addConverterFactory(ReqStringResGsonConverterFactory.create(dateGson))
            .build().create(CreateApi::class.java)
    }

    private fun initLocalRetrofit() {

        val localUrl = getBaseUrl("localhost", "9899")
        val downloadUrl = getBaseUrl("localhost", "9899")
        val downloadDbUrl = getBaseUrl("localhost", "9899")

        localApi =
            getRetrofit(localUrl).create(Api::class.java)

        downloadApi = Retrofit.Builder()
            .baseUrl(downloadUrl)
            .client(getOkHttpClient())
            .addConverterFactory(ReqStringResGsonConverterFactory.create(dateGson))
            .build().create(DownloadApi::class.java)
        downloadDbApi = Retrofit.Builder()
            .baseUrl(downloadDbUrl)
            .client(getOkHttpClient(timeOut = 60))
            .addConverterFactory(ReqStringResGsonConverterFactory.create(dateGson))
            .build().create(DownloadApi::class.java)
    }
}