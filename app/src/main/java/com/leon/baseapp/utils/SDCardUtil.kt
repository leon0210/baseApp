package com.leon.baseapp.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.TextUtils
import android.text.format.Formatter
import com.leon.baseapp.base.BaseApplication
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Author: 千里
 * Date: 2020/7/3 15:49
 * Description:
 */
object SDCardUtil {
    /**
     * 检查SD卡是否存在
     */
    fun checkSdCard(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取手机SD卡总空间
     * 版本号大于18
     */
    private val sdCardTotalSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (checkSdCard()) {
                    val path = Environment.getExternalStorageDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val blockCountLong = mStatFs.blockCountLong
                    blockSizeLong * blockCountLong
                } else 0
            } else -1
        }

    /**
     * 获取SDka可用空间
     */
    private val sdCardAvailableSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (checkSdCard()) {
                    val path = Environment.getExternalStorageDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val availableBlocksLong = mStatFs.availableBlocksLong
                    blockSizeLong * availableBlocksLong
                } else 0
            } else -1
        }

    /**
     * 获取手机存储总空间
     */
    val phoneTotalSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return if (!checkSdCard()) {
                    val path = Environment.getDataDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val blockCountLong = mStatFs.blockCountLong
                    blockSizeLong * blockCountLong
                } else {
                    sdCardTotalSize
                }
            } else -1
        }

    /**
     * 获取手机存储可用空间
     */
    val phoneAvailableSize: Long
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (!checkSdCard()) {
                    val path = Environment.getDataDirectory()
                    val mStatFs = StatFs(path.path)
                    val blockSizeLong = mStatFs.blockSizeLong
                    val availableBlocksLong = mStatFs.availableBlocksLong
                    blockSizeLong * availableBlocksLong
                } else sdCardAvailableSize
            } else -1
        }
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val totalSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            totalSize = statFs.blockCountLong
        } else {
            blockSize = statFs.blockSize.toLong()
            totalSize = statFs.blockCount.toLong()
        }
        return blockSize * totalSize
    }

    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val availableSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            availableSize = statFs.availableBlocksLong
        } else {
            blockSize = statFs.blockSize.toLong()
            availableSize = statFs.availableBlocks.toLong()
        }
        return blockSize * availableSize
    }

    fun getSDCardInfo(): List<SDCardInfo>? {
        val paths: MutableList<SDCardInfo> = ArrayList()
        val sm =
            BaseApplication.instance.getSystemService(Context.STORAGE_SERVICE) as StorageManager ?: return paths
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageVolumes = sm.storageVolumes
            try {
                val getPathMethod = StorageVolume::class.java.getMethod("getPath")
                for (storageVolume in storageVolumes) {
                    val isRemovable = storageVolume.isRemovable
                    val state = storageVolume.state
                    val path = getPathMethod.invoke(storageVolume) as String
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            try {
                val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                val getPathMethod = storageVolumeClazz.getMethod("getPath")
                val isRemovableMethod = storageVolumeClazz.getMethod("isRemovable")
                val getVolumeStateMethod =
                    StorageManager::class.java.getMethod("getVolumeState", String::class.java)
                val getVolumeListMethod = StorageManager::class.java.getMethod("getVolumeList")
                val result = getVolumeListMethod.invoke(sm)
                val length = Array.getLength(result)
                for (i in 0 until length) {
                    val storageVolumeElement = Array.get(result, i)
                    val path = getPathMethod.invoke(storageVolumeElement) as String
                    val isRemovable = isRemovableMethod.invoke(storageVolumeElement) as Boolean
                    val state = getVolumeStateMethod.invoke(sm, path) as String
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return paths
    }

    class SDCardInfo internal constructor(val path: String, val state: String, val isRemovable: Boolean) {
        private val totalSize: Long = getFsTotalSize(path)
        private val availableSize: Long = getFsAvailableSize(path)
        override fun toString(): String {
            return "SDCardInfo {" +
                    "path = " + path +
                    ", state = " + state +
                    ", isRemovable = " + isRemovable +
                    ", totalSize = " + Formatter.formatFileSize(BaseApplication.instance, totalSize) +
                    ", availableSize = " + Formatter.formatFileSize(BaseApplication.instance, availableSize) +
                    '}'
        }
    }

}