package com.wuji.tv.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.admin.libcommon.base.BaseViewModel
import com.admin.libcommon.ext.log
import com.wuji.tv.model.FilesData
import com.wuji.tv.model.FilesResult
import com.wuji.tv.presenter.PosterPresenter
import com.wuji.tv.repository.PosterRepository
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.StringCallback
import okhttp3.Call
import okhttp3.MediaType
import java.lang.Exception

class PosterViewModel(
    private val repository: PosterRepository,
    private val presenter: PosterPresenter,
    private var size: Int = 0
) : BaseViewModel() {

    val getFileLiveData by lazy {
        MutableLiveData<FilesData>()
    }


    fun getFile(session:String, ip:String, path:String = "", share_path_type:Int = 2,
                        show_hidden:Int = 0, ftype:String = "", order:String = "",
                        page:Int = 0, num:Int = 100){
        size++
        val params = HashMap<String, Any>()
        params["path"] = path
        params["share_path_type"] = share_path_type
        params["show_hidden"] = show_hidden
        params["ftype"] = ftype
        params["order"] = order
        params["page"] = page
        params["num"] = num
        val body = HashMap<String, Any>()
        body["method"] = "list"
        body["session"] = session
        body["params"] = params

        OkHttpUtils.postString().url("http://$ip/file")
            .content(Gson().toJson(body))
            .mediaType(MediaType.parse("application/json; charset=utf-8"))
            .build()
            .execute(object : StringCallback(){
                override fun onError(call: Call?, e: Exception?, id: Int) {
                    e.toString().log("PosterFragment")
                    size--
//                    getAllFiles(session,ip,share_path_type = share_path_type)
                    getFileLiveData.postValue(FilesData(session = session,ip = ip,files = arrayListOf(),isAll = size==0))
                }

                override fun onResponse(response: String?, id: Int) {
//                    response?.log("PosterFragment")
                    var filesData:FilesData = FilesData()
                    var result: FilesResult = Gson().fromJson(response, FilesResult::class.java)
                    if(result.result && result.data.total > 0){
                        filesData = result.data
                        /*if(result.data.pages > result.data.page + 1){
                            getFile(session,ip,path,share_path_type,show_hidden,ftype,order,result.data.page+1,num)
                        }*/
                    }
                    size--
                    filesData.session = session
                    filesData.ip = ip
                    filesData.path = path
                    filesData.isAll = size==0
                    getFileLiveData.postValue(filesData)
                }

            })
    }

}
