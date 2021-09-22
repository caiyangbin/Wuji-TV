package com.wuji.tv


class Constants {
    companion object {

        //app update uri
        const val APP_UPDATE_URI = "https://www.baidu.com/"

        //ListStatus
        const val DEVICES_FOCUS = "DEVICES_FOCUS"
        const val FILES_FIRST_FOCUS = "FILES_FIRST_FOCUS"
        const val FILES_BACK_FOCUS = "FILES_BACK_FOCUS"

        //ItemType
        const val ITEM_VIEW_TYPE_TITLE = 1
        const val ITEM_VIEW_TYPE_NORMAL = 0

        //LiveEventKey
        const val CHANGE_LOADING_STATUS = "change_loading_status"

        // live data value key
        const val GET_DEVICE = "get_device"
        const val GET_TOKEN = "get_token"
        const val ACCESS = "access"
        const val ACCESS_LOCAL = "access_local"
        const val GET_REMOTE_FILE_LIST = "get_remote_file_list"
        const val TXT_RESULT = "txt_result"
        const val DOWNLOAD_RESULT = "download_result"
        const val ERROR = "error"
    }
}
