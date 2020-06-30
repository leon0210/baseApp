package com.leon.baseapp.utils.permission

import android.Manifest.permission
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求实体类
 */
object Permission {
    /** 应用安装权限（需要 8.0 及以上）  */
    @RequiresApi(Build.VERSION_CODES.O)
    const val REQUEST_INSTALL_PACKAGES = permission.REQUEST_INSTALL_PACKAGES

    /** 悬浮窗权限（需要 6.0 及以上）  */
    const val SYSTEM_ALERT_WINDOW = permission.SYSTEM_ALERT_WINDOW

    /** 读取日程提醒  */
    const val READ_CALENDAR = permission.READ_CALENDAR

    /** 写入日程提醒  */
    const val WRITE_CALENDAR = permission.WRITE_CALENDAR

    /** 拍照权限  */
    const val CAMERA = permission.CAMERA

    /** 读取联系人  */
    const val READ_CONTACTS = permission.READ_CONTACTS

    /** 写入联系人  */
    const val WRITE_CONTACTS = permission.WRITE_CONTACTS

    /** 访问账户列表  */
    const val GET_ACCOUNTS = permission.GET_ACCOUNTS

    /** 获取精确位置  */
    const val ACCESS_FINE_LOCATION = permission.ACCESS_FINE_LOCATION

    /** 获取粗略位置  */
    const val ACCESS_COARSE_LOCATION = permission.ACCESS_COARSE_LOCATION

    /** 录音权限  */
    const val RECORD_AUDIO = permission.RECORD_AUDIO

    /** 读取电话状态  */
    const val READ_PHONE_STATE = permission.READ_PHONE_STATE

    /** 拨打电话  */
    const val CALL_PHONE = permission.CALL_PHONE

    /** 读取通话记录  */
    const val READ_CALL_LOG = permission.READ_CALL_LOG

    /** 写入通话记录  */
    const val WRITE_CALL_LOG = permission.WRITE_CALL_LOG

    /** 添加语音邮件  */
    const val ADD_VOICEMAIL = permission.ADD_VOICEMAIL

    /** 使用SIP视频  */
    const val USE_SIP = permission.USE_SIP

    /** 处理拨出电话  */
    const val PROCESS_OUTGOING_CALLS = permission.PROCESS_OUTGOING_CALLS

    /** 8.0危险权限：允许您的应用通过编程方式接听呼入电话。要在您的应用中处理呼入电话，您可以使用 acceptRingingCall() 函数  */
    @RequiresApi(Build.VERSION_CODES.O)
    const val ANSWER_PHONE_CALLS = permission.ANSWER_PHONE_CALLS

    /** 8.0危险权限：权限允许您的应用读取设备中存储的电话号码  */
    @RequiresApi(Build.VERSION_CODES.O)
    const val READ_PHONE_NUMBERS = permission.READ_PHONE_NUMBERS

    /** 传感器  */
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    const val BODY_SENSORS = permission.BODY_SENSORS

    /** 发送短信  */
    const val SEND_SMS = permission.SEND_SMS

    /** 接收短信  */
    const val RECEIVE_SMS = permission.RECEIVE_SMS

    /** 读取短信  */
    const val READ_SMS = permission.READ_SMS

    /** 接收 WAP PUSH 信息  */
    const val RECEIVE_WAP_PUSH = permission.RECEIVE_WAP_PUSH

    /** 接收彩信  */
    const val RECEIVE_MMS = permission.RECEIVE_MMS

    /** 读取外部存储  */
    const val READ_EXTERNAL_STORAGE = permission.READ_EXTERNAL_STORAGE

    /** 写入外部存储  */
    const val WRITE_EXTERNAL_STORAGE = permission.WRITE_EXTERNAL_STORAGE

    object Group {
        /** 日历  */
        val CALENDAR = arrayOf(
            READ_CALENDAR,
            WRITE_CALENDAR
        )

        /** 联系人  */
        val CONTACTS = arrayOf(
            READ_CONTACTS,
            WRITE_CONTACTS,
            GET_ACCOUNTS
        )

        /** 位置  */
        val LOCATION = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )

        /** 存储  */
        val STORAGE = arrayOf(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
        )
    }
}