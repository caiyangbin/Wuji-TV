package com.wuji.tv.repository

import android.content.Context
import android.util.Xml
import com.admin.libcommon.ext.log
import com.admin.libcommon.utils.isImage
import com.admin.libcommon.utils.isVideo
import com.wuji.tv.Api
import com.wuji.tv.App
import com.wuji.tv.R
import com.wuji.tv.database.AppDatabase
import com.wuji.tv.model.*
import com.wuji.tv.ui.fragment.*
import com.wuji.tv.utils.FastBlur
import com.wuji.tv.utils.FileUtils
import com.wuji.tv.utils.GenreUtil
import com.wuji.tv.utils.HttpDownloaderUtil
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import timber.log.Timber
import java.io.File
import java.io.StringReader
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val TAR_META_VERSION_PATH = "/movie-poster/.meta.json"

class PosterRepository : CoroutineScope by CoroutineScope(
    Dispatchers.IO) {
    private val tag = "PosterRepository"
    private val api: Api = App.api!!
    private lateinit var context: Context
    private lateinit var session: String
    private lateinit var deviceId: String
    private lateinit var ip: String
    private val tarFileName = "tar.gz"
    private lateinit var tarUrlStr: String
    private lateinit var tarDirPath: String
    private lateinit var tarFilePath: String
    private val metaFilesName = ".meta.files"
//    private lateinit var metaFileUrlStr: String
//    private lateinit var metaFilesDirPath: String
//    private lateinit var metaFilesPath: String
    //private val tarMetaFilesPath = "${tarDirPath}${TAR_META_FILES_PATH}"

    private lateinit var listener: OnPullTarListener
    private val dispatcher by lazy {
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
            .asCoroutineDispatcher()
    }

//    private val tarFilesPath by lazy { Vector<String>() }
    //    private val nfos by lazy { Vector<String>() }
    private val medias by lazy { HashMap<String,String>() }
//    private val bdmvs by lazy { Vector<String>() }
    private val posters by lazy { HashMap<String,ArrayList<String>>() }
    private val fanarts by lazy { HashMap<String,ArrayList<String>>() }
    private val samples by lazy { HashMap<String,ArrayList<String>>() }
    private val trailers by lazy { HashMap<String,ArrayList<String>>() }

    private val topTabs by lazy { Vector<TopTabModel>() }
    private val leftTabs by lazy { Vector<LeftTabModel>() }

    private val infos by lazy { Vector<MediaInfoModel>() }
    private lateinit var deviceInfo: DeviceInfoModel

//    private var nfoCount = 0
//    private var parsingNfoCount = 0
    private val retryCount = 3
    private var progressStep = 1 // 1下载 -> 2解压 -> 3解析file -> 4解析movie
    private var mProgress = 0

    private var tarVersion: String? = null
    private var scope: CoroutineScope? = null


    interface OnPullTarListener {
        fun onStart()
        fun onProgress(progress: Int)
        fun onSuccess(data: List<TopWithLeftTabModel>, deviceInfoModel: DeviceInfoModel?, isScan: Boolean)
        fun onToFileFragment()
        fun update()
        fun onNoTar()
    }

    private suspend fun runOnUI(block: () -> Unit) = withContext(Dispatchers.Main) {
        block()
    }

    fun test(){
        val mao = HashMap<String,String>()
    }

    fun getPosterData(_listener: OnPullTarListener,_session: String, _id: String, _ip: String, _context: Context) = launch {
        "getPosterData".log(tag)
        scope = this
        listener = _listener
        context = _context
        session = _session
        deviceId = _id
        ip = _ip

        initData()

        if (checkDB(this,false)) {
            if (!isActive) return@launch
            checkTarVersion(deviceId,session,TAR_META_VERSION_PATH,context)
//            runOnUI { listener.update() }
        }
        else{
            if (!isActive) return@launch
            getTarAndParse()
        }

    }

    private fun initData(){
        deviceInfo = AppDatabase.getInstance(context).getDeviceInfoDao().getDeviceInfo(deviceId) ?: DeviceInfoModel(deviceId)
        tarUrlStr = "http://${ip}:9898/file/download?session=${session}&path=/movie-poster/.meta.tar.gz"
        tarDirPath = "${context.filesDir?.path}/tar/${deviceId}"
        tarFilePath = "${tarDirPath}/${tarFileName}"
//        metaFileUrlStr = "http://${ip}:9898/file/download?session=${session}&path=/movie-poster/.meta.files"
//        metaFilesDirPath = "${tarDirPath}/movie-poster"
//        metaFilesPath = "${metaFilesDirPath}/${metaFilesName}"
    }

    private fun clearData(){
        "initData".log(tag)
        medias.clear()
        posters.clear()
        fanarts.clear()
        trailers.clear()
        samples.clear()
        topTabs.clear()
        leftTabs.clear()
        infos.clear()
//        onGetTabDatas = false
    }

    fun getTarAndParse(){
        launch {
            scope = this
            if(!checkIsPosterType()){
                if (isActive) {
                    runOnUI { listener.onToFileFragment() }
                    destroy()
                    return@launch
                }
            }
            if (isActive) {
                runOnUI { listener.onStart() }
                startProgress(this)
            }
            var dir = downAndUnTar()
            if (!isActive) return@launch
            if(dir == null){
                runOnUI { listener.onNoTar() }
                /*if(checkIsPosterType()){
                    if (!isActive) return@launch
                    runOnUI { listener.onNoTar() }
                }
                else {
                    if (isActive) {
                        runOnUI { listener.onToFileFragment() }
                    }
                }*/
                FileUtils.deleteFolderFile(tarDirPath)
                destroy()
                return@launch
            }
            launch {
                clearData()
                parseTar(this,dir)
            }.join()
            FileUtils.deleteFolderFile(tarDirPath)
            if(tarVersion == null){
                tarVersion = getTarVersionStr(session,TAR_META_VERSION_PATH,context)
            }
            deviceInfo.updateTime = tarVersion as String

            insertDB()
            clearData()

            checkDB(this,true)

            mProgress = 100
            runOnUI { listener.onProgress(mProgress) }
        }
    }

    private suspend fun startProgress(scope: CoroutineScope){
        mProgress = 0
        progressStep = 1
        scope.launch {
            while (isActive && mProgress < 98){
                if(mProgress < progressStep*25){
                    mProgress++
                    runOnUI { listener.onProgress(mProgress) }
                }
                delay(500)
            }
        }
    }

    private suspend fun downAndUnTar() : File?{
        val result = HttpDownloaderUtil().downFile(tarUrlStr,tarDirPath,tarFileName)
        if (!isActive) return null
        if(result == 0){
            mProgress = 25
            runOnUI { listener.onProgress(mProgress) }
            progressStep = 2
            val file = File(tarFilePath)
            if(!file.exists() || !file.isFile || file.length()/1024 == 0L){
                return null
            }
            FileUtils.untarGZip(file,tarDirPath)
            if (!isActive) return null
            mProgress = 50
            runOnUI { listener.onProgress(mProgress) }
            progressStep = 3
            return File(tarDirPath)
        }
        return null
    }

    private suspend fun checkIsPosterType(): Boolean {
        var len = 0
        var file = getFileList("/movie-poster") ?: return false
        if(file.result){
            file.data?.files?.apply {
                filter { it.name == "menu" || it.name == "movie"}.onEach{
                    len++
                }
            }
        }
        return len == 2
    }

    private suspend fun getFileList(path: String): BaseResult<FileListResult>? {
        try {
            if(apiTime == -1L){
                apiTime = Date().time/1000
            }
            val params = HashMap<String, Any>()
            params["path"] = path
            params["share_path_type"] = 2
            params["show_hidden"] = 0
            params["ftype"] = ""
            params["order"] = ""
            params["page"] = 0
            params["num"] = 10000
            val body = HashMap<String, Any>()
            body["method"] = "list"
            body["session"] = session
            body["params"] = params

            val res = api.fileList(body)
            apiTime = -1L
            return res

        } catch (e: Exception) {
            if (!isActive) return null
            val time = Date().time/1000
            if (e is SocketTimeoutException && time - apiTime < 10){
                return getFileList(path)
            }
            else{
                apiTime = -1L
                return null
            }
        }
        return null
    }

    private suspend fun parseTar(scope: CoroutineScope, tarDir: File) {
        "parseTar   ${tarDir.name}  ${tarDir.exists()}  ${tarDir.isDirectory}".log(tag)
        if (tarDir.exists() && tarDir.isDirectory){
            tarDir.listFiles().filter { MOVIE_POSTER == it.name }.onEach { file ->
                file.listFiles().apply {
//                    "parseTar ${it.name}".log(tag)
                    filter{it.isFile && it.name == metaFilesName}.onEach {
                        parseMetaFile(it)
                        mProgress = 75
                        runOnUI { listener.onProgress(mProgress) }
                        progressStep = 4
                    }

                    filter{it.isFile && it.name.startsWith(MOVIE_POSTER_IN,true)}.onEach  {
                        scope.launch {
                            paraInTxt(it,deviceInfo)
                        }
                    }

                    filter{it.isDirectory && it.name.equals(MOVIE_POSTER_MENU,true)}.onEach {
                        scope.launch { paraOneMenuFile(it) }
                    }

                    filter{it.isDirectory && it.name.equals(MOVIE_POSTER_MOVIE,true)}.onEach {
                        scope.launch { parseMovie(scope,it) }
                    }
                }
            }
        }
        else{
            if (isActive) {
                scope.launch { listener.onToFileFragment() }
                cancel()
            }
        }
    }

    private fun parseMetaFile(file: File) {
        "parseMetaFile  ${file.name}:${file.length()}".log(tag)

        val pathStr = FileUtils.loadFromFile(file)
        if (!isActive) return
        if(pathStr != null){

            pathStr.split("\n").forEach {
                if (!isActive) return
                if(it.contains("/") && !it.endsWith(".thumb")){
                    if(it.contains(".") ){
                        if( it.contains("/${MOVIE_POSTER}/${MOVIE_POSTER_BG}",true)) {
                            deviceInfo.movie_poster_bg = it
//                            "parseMetaFile $it".log(tag)
                        }
                        else if (it.contains("/${MOVIE_POSTER}/${MOVIE_POSTER_COVER}",true)) {
                            deviceInfo.movie_poster_cover = it
//                            "parseMetaFile $it".log(tag)
                        }
                        else if (it.contains("/${MOVIE_POSTER}/${MOVIE_POSTER_LOGO}",true)) {
                            deviceInfo.movie_poster_logo = it
//                            "parseMetaFile $it".log(tag)
                        }
                        else if (it.contains("/${MOVIE_POSTER}/${MOVIE_POSTER_WALL}",true)) {
                            deviceInfo.movie_poster_wall = it
//                            "parseMetaFile $it".log(tag)
                        }
                        else if (it.startsWith("/movie-poster/movie") && !it.contains("bdmv",true)) {
//                            "parseMetaFile $it".log(tag)
                            parseMetaFileMovie(it)
                        }
                        /*else if(it.startsWith("/movie-poster/menu")){

                        }*/
                    }
                    else if(it.startsWith("/movie-poster/movie") && it.endsWith("bdmv",true)){
//                        "parseMetaFile $it".log(tag)
                        val dirPath = it.substring(0, it.lastIndexOf("/"))
                        medias[dirPath] = it
                    }
                }
            }

        }
        else{
            if (isActive) {
                scope?.launch { listener.onToFileFragment() }
                cancel()
            }
        }
    }

    private fun paraInTxt(file: File, deviceInfo: DeviceInfoModel) {
        val txtStr = FileUtils.loadFromFile(file)
        if (!isActive) return
        if (txtStr != null) {
            val list = txtStr.split("\r\n")
            if (list.size >= 4) {
                deviceInfo.name = list[0]
                deviceInfo.plot = list[1]
                //deviceInfo.updateTime = list[2]
                deviceInfo.type = list[3]
                //currentGetDataDevice?.newNumber = list[4]
            }
        }
    }

    private fun parseMetaFileMovie(filePath: String){
        val dirPath = filePath.substring(0, filePath.lastIndexOf("/"))
        if(isVideo(filePath) && filePath.contains("trailer", true)) {
            if (trailers[dirPath] == null){
                trailers[dirPath] = arrayListOf(filePath)
            }
            else {
                trailers[dirPath]?.add(filePath)
            }
        }
        else if(isVideo(filePath) && filePath.contains("sample", true)) {
            if (samples[dirPath] == null){
                samples[dirPath] = arrayListOf(filePath)
            }
            else {
                samples[dirPath]?.add(filePath)
            }
        }
        else if(isImage(filePath) && filePath.contains("fanart")) {
            if (fanarts[dirPath] == null){
                fanarts[dirPath] = arrayListOf(filePath)
            }
            else {
                fanarts[dirPath]?.add(filePath)
            }
        }
        else if(isImage(filePath) && filePath.substring(0, filePath.lastIndexOf("."))
                .endsWith("poster",true)) {
            if (posters[dirPath] == null){
                posters[dirPath] = arrayListOf(filePath)
            }
            else {
                posters[dirPath]?.add(filePath)
            }
        }
        else if(isVideo(filePath)) {
            medias[dirPath] = filePath
        }
    }

    private fun paraOneMenuFile(file: File) {
        if (file.exists() && file.isDirectory){
            file.listFiles().forEach {
                try {
                    val name = it.name.toLowerCase(Locale.ROOT)
                    when {
                        name.contains("nfo") -> {
                            // 如果包含nfo，解析文件夹名字规则
                            val info = name.split("-")
                            val condition = info[4].split("&")
                            val list = ArrayList<String>()
                            list.addAll(condition)
                            val topTabModel = TopTabModel(TYPE_NFO, info[0].toInt(), info[1], info[3], list)
                            topTabModel.topId = "${topTabModel.name}--${topTabModel.index}"
                            topTabModel.deviceId = deviceId
                            topTabs.add(topTabModel)
                            paraSecondMenu(
                                it,
                                topTabModel.topId,
                                "${topTabModel.deviceId}#${topTabModel.name}#${topTabModel.index}"
                            )
                        }
                        name.contains("all") -> {
                            // 如果包含all，说明是全部分类
                            val info = name.split("-")
                            val topTabModel = TopTabModel(TYPE_ALL, info[0].toInt(), info[1])
                            topTabModel.topId = "${topTabModel.name}--${topTabModel.index}"
                            topTabModel.deviceId = deviceId
                            topTabs.add(topTabModel)
                            paraSecondMenu(
                                it,
                                topTabModel.topId,
                                "${topTabModel.deviceId}#${topTabModel.name}#${topTabModel.index}"
                            )
                        }
                        name.contains("folder") -> {
                            // 如果包含folder，即获取下级目录的lists.txt文件内容
                            paraListsTxt(it)
                        }
                        else -> Unit
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        else{
            if (isActive) {
                scope?.launch { listener.onToFileFragment() }
                cancel()
            }
        }
    }

    private fun paraListsTxt(file: File) {
        val fileList = file.listFiles()
        fileList.filter { it.name.equals("lists.txt",true) }.onEach { it ->
            val listsStr = FileUtils.loadFromFile(it)
            listsStr?.apply {
                val split = this.split("\n")
                val names = ArrayList<String>()
                split.forEach { n ->
                    names.add(n.trimStart().trimEnd())
                }
//                names.addAll(split)
                val info = file.name.split("-")
                val topTabModel = TopTabModel(
                    TYPE_FOLDER, info[0].toInt(), info[1], folderMovieList = names
                )
                topTabModel.topId = "${topTabModel.name}--${topTabModel.index}"
                topTabModel.deviceId = deviceId
                topTabs.add(topTabModel)
                paraSecondMenu(
                    file,
                    topTabModel.topId,
                    "${topTabModel.deviceId}#${topTabModel.name}#${topTabModel.index}"
                )
            }
        }
    }

    private fun paraSecondMenu(file: File, topId: String, index: String) {
        try {
            val fileList = file.listFiles()
            fileList?.forEach {
                val name = it.name.toLowerCase(Locale.ROOT)
                when {
                    name.contains("nfo") -> {
                        val info = name.split("-")
                        val condition = info[4].split("&")
                        val list = ArrayList<String>()
                        list.addAll(condition)
                        val leftTabModel =
                            LeftTabModel(TYPE_NFO, info[0].toInt(), info[1], info[3], list)
                        leftTabModel.leftTopId = topId
                        leftTabModel.topNameIndexAndLeftIndex = "$index#${leftTabModel.index}"
                        leftTabModel.deviceId = deviceId
                        leftTabs.add(leftTabModel)
                    }
                    name.contains("all") -> {
                        val info = name.split("-")
                        val leftTabModel = LeftTabModel(TYPE_ALL, info[0].toInt(), info[1])
                        leftTabModel.leftTopId = topId
                        leftTabModel.topNameIndexAndLeftIndex = "$index#${leftTabModel.index}"
                        leftTabModel.deviceId = deviceId
                        leftTabs.add(leftTabModel)
                    }
                    name.contains("folder") -> {
                        paraSecondListsTxt(it, topId, index)
                    }
                    else -> Unit
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun paraSecondListsTxt(file: File, topId: String, index: String) {
        val fileList = file.listFiles()
        fileList.filter { it.name.equals("lists.txt",true) }.onEach {
            val listsStr = FileUtils.loadFromFile(it)
            listsStr?.apply {
                val split = this.split("\n")
                val names = ArrayList<String>()
                split.forEach { n ->
                    names.add(n.trimStart().trimEnd())
                }
                val info = file.name.split("-")
                val leftTabModel = LeftTabModel(
                    TYPE_FOLDER, info[0].toInt(), info[1], folderMovieList = names
                )
                leftTabModel.leftTopId = topId
                leftTabModel.topNameIndexAndLeftIndex = "$index#${leftTabModel.index}"
                leftTabModel.deviceId = deviceId
                leftTabs.add(leftTabModel)
            }
        }
    }

    private fun parseMovie(scope: CoroutineScope, file: File) {
        if (!isActive) return
        if (file.exists() && file.isDirectory){
            file.listFiles().apply {
                filter { isDir(it) }.onEach {
                    scope.launch { parseMovie(scope,it) }
                }
                filter { !it.isDirectory && it.name.endsWith(".nfo",true) }
                    .onEach {
                        parseNfo(it)
                    }
            }
        }
        else{
            if (isActive) {
                scope.launch { listener.onToFileFragment() }
                cancel()
            }
        }
    }

    private fun parseNfo(file: File) {
        val nfoStr = FileUtils.loadFromFile(file)
        nfoStr?.apply {
            val info = MediaInfoModel()
            info.session = session
            //info.localSession = sessionLocal
            info.ip = ip
            info.deviceId = deviceId
            val nameNoExtension = file.path.substring(0, file.path.lastIndexOf("/"))
                .replace(tarDirPath,"")
            info.path = medias[nameNoExtension] ?: ""
            info.fanartList = fanarts[nameNoExtension] ?: arrayListOf()
            info.posterList = posters[nameNoExtension] ?: arrayListOf()
            info.sampleList = samples[nameNoExtension] ?: arrayListOf()
            info.trailerList = trailers[nameNoExtension] ?: arrayListOf()
            if(info.path.isNotEmpty()
                && !info.fanartList.isNullOrEmpty()
                && !info.posterList.isNullOrEmpty()){

                infos.add(info)
                readXmlByPull(this, info)
            }
        }
    }
//    private val map by lazy { HashMap<String,String>() }

    private fun isBdmv(path: String): Boolean {
        if (path.contains(".")) return false
//        Timber.i("$tag  isBdmv")
        return path.endsWith("bdmv",true)
                || path.endsWith("bdmv/",true)
                || path.endsWith("video_ts",true)
                || path.endsWith("video_ts/",true)
                || path.endsWith("bdav",true)
                || path.endsWith("bdav/",true)
                || path.endsWith("certificate",true)
                || path.endsWith("certificate/",true)
    }

    private fun isDir(file: File): Boolean {
//        Timber.i("$tag  isDir")
        val name = file.name.toLowerCase(Locale.ROOT)
        return file.isDirectory && !(name == "bdmv" || name == "video_ts" || name == "bdav" || name == "certificate")
    }

    private fun readXmlByPull(d: String, info: MediaInfoModel) {
        var xmlData = d.replace("\ufeff", "")
        val listA = xmlData.split("<")
        for (errFlag in listA) {
            if (errFlag.contains("/>")) {
                xmlData =
                    if (errFlag.endsWith("\r\n")) xmlData?.replace("<$errFlag\r\n", "")
                    else xmlData?.replace("<$errFlag", "")
            }
        }
        try {

            var parser = Xml.newPullParser()
            parser.setInput(StringReader(xmlData))
            var eventType = parser.eventType
            var name = ""
            var height = ""
            var rating = ""
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    //START_TAG取值
                    XmlPullParser.START_TAG -> {
                        val parserName = parser.name
                        try {
                            if (parserName != "movie"
                                && parserName != "tvshow"
                                && parserName != "episodedetails"
                                && parserName != "set"
                                && parserName != "actor"
                                && parserName != "video"
                                && parserName != "rating"
                            ) {
                                var value = parser.nextText()
                                when (parserName) {
                                    "title" -> info.title = value
                                    "showtitle" -> info.showTitle = value
                                    "premiered" -> info.premiered = value
                                    "year" -> info.year = value
                                    "runtime" -> info.runtime = value
                                    "country" -> {
                                        info.country?.apply {
                                            add(value)
                                        } ?: run {
                                            val country = ArrayList<String>()
                                            country.add(value)
                                            info.country = country
                                        }
                                    }
                                    "genre" -> {
                                        if (value != null) {
                                            info.genre?.apply {
                                                add(GenreUtil().getGenre(value))
                                            } ?: run {
                                                val genre = ArrayList<String>()
                                                genre.add(GenreUtil().getGenre(value))
                                                info.genre = genre
                                            }
                                        }
                                    }
                                    "director" -> info.director = value
                                    "plot" -> info.plot = value
                                    "original_filename" -> info.originalFileName = value
                                    "name" -> name = value
                                    "height" -> height = value
                                    "value" -> rating = value
                                    "id" -> if (value.isNotEmpty()) info.movieId = value
                                }
                            }
                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        try {
                            when (parser.name) {
                                "actor" -> {
                                    if (name.isNotEmpty()) {
                                        info.actor?.apply {
                                            //暂时控制最多显示3个主演
                                            if (size < 3) {
                                                add(name)
                                            }
                                        } ?: run {
                                            val actor = ArrayList<String>()
                                            actor.add(name)
                                            info.actor = actor
                                        }
                                    }
                                }
                                "set" -> {
                                    if (name.isNotEmpty()) {
                                        info.set = name
                                    }
                                }
                                "video" -> {
                                    if (height.isNotEmpty()) {
                                        info.nfoVideo = height
                                    }
                                }
                                "rating" -> {
                                    if (rating.isNotEmpty()) {
                                        if (rating.toFloat() > info.rating) {
                                            info.rating = rating.toFloat()
                                        }
                                    }
                                }
                            }
                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
//            e.printStackTrace()
        }
        val set = FastBlur.subRangeString(xmlData, "<set>", "</set>").replace("\r\n", "")
        if (!set.contains("<")) {
            info.set = set
        }
    }

    private fun insertDB() {
        AppDatabase.getInstance(context).getTopTabDao().delete(deviceId)
        AppDatabase.getInstance(context).getLeftTabDao().delete(deviceId)
        AppDatabase.getInstance(context).getMovieListDao().delete(deviceId)
        AppDatabase.getInstance(context).getTopTabDao().insertList(topTabs)
        AppDatabase.getInstance(context).getLeftTabDao().insertList(leftTabs)
        AppDatabase.getInstance(context).getDeviceInfoDao().insert(deviceInfo)
        AppDatabase.getInstance(context).getMovieListDao().insertList(infos)

    }

    private suspend fun checkDB(scope: CoroutineScope, isScan: Boolean): Boolean {
        val tabs =
            AppDatabase.getInstance(context).getTopWithLeftTabDao()
                .getTopWithLeftTabsWithDeviceId(deviceId)

        val mediaInfo =
            AppDatabase.getInstance(context).getMovieListDao()
                .getMediasWithDeviceId(deviceId)

        val deviceInfoModel =
            AppDatabase.getInstance(context).getDeviceInfoDao()
                .getDeviceInfo(deviceId)

        if (tabs.isNotEmpty() && mediaInfo.isNotEmpty()) {
            getTabDatas(scope, tabs,deviceInfoModel,isScan)
            return true
        } else {
            return false
        }
    }

    private fun getTabDatas(scope: CoroutineScope, tabs: List<TopWithLeftTabModel>,
                            deviceInfoModel: DeviceInfoModel?,isScan: Boolean) {
        scope.launch {
            tabs.forEachIndexed {indexed,it ->
                when (it.topTabModel.folderType) {
                    TYPE_FOLDER -> {
                        if (it.topTabModel.folderMovieList != null) {
                            val datas = ArrayList<MediaInfoModel>()
                            val temp = AppDatabase.getInstance(context).getMovieListDao()
                                .getMediasWithTitle(deviceId,it.topTabModel.folderMovieList)
                            datas.addAll(temp)
                            it.topTabModel.posterData = datas
                            it.leftTabs.forEach { leftTabs ->
                                getLeftTabsDatas(leftTabs, datas)
                            }
                        }
                    }
                    TYPE_ALL -> {
                        val datas = ArrayList<MediaInfoModel>()
                        val temp = AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithDeviceId(deviceId)
                        datas.addAll(temp)
                        it.topTabModel.posterData = datas
                        it.leftTabs.forEach { leftTabs ->
                            getLeftTabsDatas(leftTabs, datas)
                        }
                    }
                    TYPE_NFO -> {
                        if (it.topTabModel.movieTypeFilter != null && it.topTabModel.movieConditionFilter != null) {
                            val datas = getNfoTypeData(
                                it.topTabModel.movieTypeFilter,
                                it.topTabModel.movieConditionFilter
                            )
                            it.topTabModel.posterData = datas
                            it.leftTabs.forEach { leftTabs ->
                                getLeftTabsDatas(leftTabs, datas)
                            }
                        }
                    }
                }
                if (indexed == 0){
                    if (isActive) {
                        runOnUI { listener.onSuccess(tabs,deviceInfoModel,isScan) }
                    }
                }
            }
            if (isActive) {
                runOnUI { listener.onSuccess(tabs,deviceInfoModel,isScan) }
            }
//            onGetTabDatas = false
        }
    }

    private fun getLeftTabsDatas(leftTab: LeftTabModel, topDatas: ArrayList<MediaInfoModel>) {
        val tempTopDatas = ArrayList<MediaInfoModel>()
        tempTopDatas.addAll(topDatas)

        when (leftTab.folderType) {
            TYPE_FOLDER -> {
                if (leftTab.folderMovieList != null) {
                    val leftDatas = ArrayList<MediaInfoModel>()
                    val leftTemp =
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithTitle(deviceId,leftTab.folderMovieList)
                    tempTopDatas.retainAll(leftTemp)
                    leftDatas.addAll(tempTopDatas)
                    leftTab.posterData = leftDatas
                }
            }
            TYPE_NFO -> {
                if (leftTab.movieTypeFilter != null && leftTab.movieConditionFilter != null) {
                    val leftDatas = ArrayList<MediaInfoModel>()
                    val leftTemp = getNfoTypeData(
                        leftTab.movieTypeFilter,
                        leftTab.movieConditionFilter
                    )
                    tempTopDatas.retainAll(leftTemp)
                    leftDatas.addAll(tempTopDatas)
                    leftTab.posterData = leftDatas
                }
            }
            TYPE_ALL -> {
                val leftDatas = ArrayList<MediaInfoModel>()
                leftDatas.addAll(tempTopDatas)
                leftTab.posterData = leftDatas
            }
        }
    }

    private fun getNfoTypeData(
        movieTypeFilter: String,
        movieConditionFilter: ArrayList<String>
    ): ArrayList<MediaInfoModel> {
        val result = ArrayList<MediaInfoModel>()
        when (movieTypeFilter) {
            "title" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithTitle(deviceId,movieConditionFilter)
                )
            }
            "showtitle" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithShowTitle(deviceId,movieConditionFilter)
                )
            }
            "premiered" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithPremiered(deviceId,movieConditionFilter)
                )
            }
            "year" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithYear(deviceId,movieConditionFilter)
                )
            }
            "director" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithDirector(deviceId,movieConditionFilter)
                )
            }
            "video" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithVideo(deviceId,movieConditionFilter)
                )
            }
            "plot" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithPlot(deviceId,movieConditionFilter)
                )
            }
            "original_filename" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithOriginalFilename(deviceId,movieConditionFilter)
                )
            }
            "ratings" -> {
                movieConditionFilter.forEach {
                    result.addAll(
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithRating(deviceId,it.toFloat())
                    )
                }
            }
            "rating" -> {
                movieConditionFilter.forEach {
                    result.addAll(
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithRating(deviceId,it.toFloat())
                    )
                }
            }
            "id" -> {
                result.addAll(
                    AppDatabase.getInstance(context).getMovieListDao()
                        .getMediasWithMovieId(deviceId,movieConditionFilter)
                )
            }
            "genre" -> {
                movieConditionFilter.forEach {
                    result.addAll(
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithGenre(deviceId,it)
                    )
                }
            }
            "country" -> {
                movieConditionFilter.forEach {
                    result.addAll(
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithCountry(deviceId,it)
                    )
                }
            }
            "actor" -> {
                movieConditionFilter.forEach {
                    result.addAll(
                        AppDatabase.getInstance(context).getMovieListDao()
                            .getMediasWithActor(deviceId,it)
                    )
                }
            }
        }
        val hashSet: LinkedHashSet<MediaInfoModel> = LinkedHashSet(result)
        return ArrayList(hashSet)
    }


    private fun checkTarVersion(deviceId: String, session:String, path: String, context: Context) {
        scope?.launch {
            try {
                val res = getTarVersion(session,path,context)
                if (!isActive) return@launch
                res?.let { result ->
                    if (result.result){
                        if(result.data != null){
                            val deviceInfoModel = AppDatabase.getInstance(context).getDeviceInfoDao().getDeviceInfo(deviceId)
                            if(deviceInfoModel == null
                                || deviceInfoModel.updateTime.isEmpty()
                                || deviceInfoModel.updateTime != result.data.context){

                                tarVersion = result.data.context
                                Timber.i("$tag  db:${deviceInfoModel?.updateTime}  res:${result.data.context}")
                                runOnUI { listener.update() }
                            }
                        }
                    }
                    else{
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun getTarVersionStr(session:String, path: String,context: Context): String {
        try {
            val res = getTarVersion(session,path,context)
            res?.data?.context?.let { return it }
        } catch (e: Exception) {
        }
        return "0"
    }

    private suspend fun getTarVersion(session:String, path: String,context: Context): BaseResult<TxtResult>? {
        try {
            val params = HashMap<String, Any>()
            params["path"] = path
            params["cmd"] = "readtxt"
            val body = HashMap<String, Any>()
            body["method"] = "manage"
            body["session"] = session
            body["params"] = params

            return api.readTxt(body)
        } catch (e: Exception) {
        }
        return null
    }


    fun updateTar(session: String, listener: OnUpdateListener, context: Context){
        launch {
            scope = this
            val res = updateTarHttp(session)
            if (!isActive) return@launch
            res?.let { result ->
                if (result.result){
                    runOnUI {
                        listener.onUpdate(
                            result.result,
                            1,
                            context.getString(R.string.rebuild_success))
                    }
                }
                else{
                    runOnUI {
                        listener.onUpdate(
                            result.result,
                            result.error?.code ?: -1,
                            result.error?.msg ?: context.getString(R.string.fail))
                    }
                }
            }
        }
    }
    interface OnUpdateListener {
        fun onUpdate(isSuccess: Boolean, code: Int, msg: String)
    }

    private suspend fun updateTarHttp(session: String): BaseResult<FileListResult>? {
        try {
            val params = HashMap<String, Any>()
            params["cmd"] = "package"
            params["path"] = "/movie-poster"
            params["share_path_type"] = 2
            params["des_path_type"] = 2
            params["todir"] = "/movie-poster"
            params["patterns"] = arrayOf(".nfo",".txt")
            val body = HashMap<String, Any>()
            body["method"] = "manage"
            body["session"] = session
            body["params"] = params

            return api.fileList(body)

        } catch (e: Exception) {
        }
        return null
    }

    fun access(token: String,isLocal: Boolean,listener: OnAccessListener,context: Context){
        launch {
            scope = this
            try {
                if(apiTime == -1L){
                    apiTime = Date().time/1000
                }
                val params = HashMap<String, Any>()
                params["token"] = token
                val body = HashMap<String, Any>()
                body["method"] = "access"
                body["session"] = ""
                body["params"] = params

                val res = if(isLocal) App.localApi?.access(body) else App.api?.access(body)
                if (!isActive) return@launch
                apiTime = -1L
                res.let { result ->
                    if (result != null) {
                        runOnUI {
                            if (result.result){
                                listener.onAccess(
                                    result.result,
                                    0,
                                    "",
                                    result.data)
                            } else{
                                listener.onAccess(
                                    result.result,
                                    result.error?.code ?: -1,
                                    result.error?.msg ?: context.getString(R.string.fail),
                                    null)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (!isActive) return@launch
                val time = Date().time/1000
                if (e is SocketTimeoutException && time - apiTime < 10){
                    scope?.cancel()
                    access(token,isLocal,listener,context)
                }
                else{
                    e.printStackTrace()
                    apiTime = -1L
                    runOnUI {listener.onAccess(false, -1, e.toString(),null)}
                }
            }
        }
    }
    private var apiTime = -1L
    interface OnAccessListener {
        fun onAccess(success: Boolean, code: Int, msg: String,data: AccessResult?)
    }

    fun getFileManage(path: String,session: String, listener: OnFileManageListener) {
        launch {
            scope = this
            try {
                val params = HashMap<String, Any>()
                params["path"] = path
                params["share_path_type"] = 2
                params["show_hidden"] = 0
                params["cmd"] = "attributes"
                val body = HashMap<String, Any>()
                body["method"] = "manage"
                body["session"] = session
                body["params"] = params

                val res = api.fileInfo(body)
                if (isActive) {
                    runOnUI { listener.onFileManage(res.data) }
                }

            } catch (e: Exception) {
                e.toString().log(tag)
            }
        }
    }
    interface OnFileManageListener {
        fun onFileManage(data: FileInfoResult?)
    }

    fun destroy(): Boolean {
        val onGetTabDatas = scope?.isActive ?: false
        scope?.cancel()
        return onGetTabDatas
    }

}