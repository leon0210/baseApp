package com.leon.baseapp.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import com.leon.baseapp.base.BaseApplication
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Author: 千里
 * Date: 2020/6/30 11:00
 * Description:
 */

object AppUtil {
    fun isIntentAvailable(intent: Intent?): Boolean {
        return BaseApplication.instance
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size > 0
    }

    /**
     * 获取当前进程名
     */
    fun getProcessName(pid: Int): String {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader!!.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim({ it <= ' ' })
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader!!.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return ""
    }
}