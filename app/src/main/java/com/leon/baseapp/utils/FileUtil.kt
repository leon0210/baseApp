package com.leon.baseapp.utils

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import com.leon.baseapp.base.BaseApplication
import org.litepal.util.LogUtil
import java.io.*
import java.nio.channels.FileChannel

/**
 * Author: 千里
 * Date: 2020/7/3 15:41
 * Description:
 */
object FileUtil {
    private const val sBufferSize = 524288

    /**
     * author: 千里
     * date: 2020/7/3 15:42
     * describe:获取文件绝对路径
     */
    fun getAbsolutePath(file: File?): String? {
        return if (file == null) "" else file.absolutePath
    }

    /**
     *  内部存储
     *  /data/data/package/files
     */
    fun getInternalAppFiles(): File = BaseApplication.instance.filesDir

    /**
     * 内部存储
     *  /cache
     */
    fun getDownloadCachePath(): File = Environment.getDownloadCacheDirectory()

    /**
     * 内部存储
     *  /data
     */
    fun getDataDirectoryPath(): File = Environment.getDataDirectory()

    /**
     * 内部存储
     *  data/data/包名/cache
     *  卸载应用后会自动删除
     */
    fun getInternalAppCachePath(): File = BaseApplication.instance.cacheDir

    /**
     * 外部存储
     * 获取应用cache目录 /storage/emulated/0/Android/data/包名/cache
     * 卸载应用后会自动删除
     */
    fun getExternalAppCachePath(): File? {
        if (!SDCardUtil.checkSdCard()) return null
        return BaseApplication.instance.externalCacheDir ?: return null
    }

    /**
     * 外部存储
     * 获取应用目录 /storage/emulated/0/Android/data/包名
     */
    fun getExternalAppDataPath(): File? {
        if (!SDCardUtil.checkSdCard()) return null
        return BaseApplication.instance.externalCacheDir?.parentFile
    }


    /**
     * 外部存储
     * @return the path of /storage/emulated/0/Android/data/package/files
     * @return the path of /storage/emulated/0/Android/data/package/files/type
     * *                [Environment.DIRECTORY_MUSIC],
     * *                [Environment.DIRECTORY_PODCASTS],
     * *                [Environment.DIRECTORY_RINGTONES],
     * *                [Environment.DIRECTORY_ALARMS],
     * *                [Environment.DIRECTORY_NOTIFICATIONS],
     * *                [Environment.DIRECTORY_PICTURES],
     * *                [Environment.DIRECTORY_MOVIES],
     *                  or 自定义文件夹名称
     */
    fun getExternalAppFilesPath(type: String? = null): File? {
        return if (!SDCardUtil.checkSdCard()) null else BaseApplication.instance.getExternalFilesDir(type)
    }

    /**
     * 外部存储 根目录 需要权限
     * /storage/emulated/0
     */
    fun getExternalStoragePath(): File? {
        return if (!SDCardUtil.checkSdCard()) null else Environment.getExternalStorageDirectory()
    }

    /**
     *文件是否存在
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            true
        } else isFileExists(file.absolutePath)
    }

    /**
     * 文件是否存在
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String?): Boolean {
        if (FormatUtil.strIsBlank(filePath)) return false
        val file = File(filePath)
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath)
    }

    private fun isFileExistsApi29(filePath: String?): Boolean {
        if (filePath == null) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val uri = Uri.parse(filePath)
                val cr: ContentResolver = BaseApplication.instance.contentResolver
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }

    fun getFileByPath(filePath: String?): File? {
        return if (FormatUtil.strIsBlank(filePath)) null else File(filePath)
    }

    /**
     * 文件重命名
     */
    fun reName(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null) return false
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (newName.isEmpty()) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return (!newFile.exists() && file.renameTo(newFile))
    }

    /**
     * 删除文件或文件夹
     */
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    /**
     * 删除文件
     */
    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    /**
     * 删除文件夹
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 创建文件
     */
    fun createFile(file: File, deleteOldFile: Boolean = false): Boolean {
        return createFile(file.absolutePath, deleteOldFile)
    }

    fun createDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }


    fun createFile(filePath: String, deleteOldFile: Boolean = false): Boolean {
        val file = File(filePath)
        if (isFileExists(file)) {
            if (deleteOldFile) {
                deleteFile(file)
                file.createNewFile()
            }
        } else file.createNewFile()
        return File(filePath).exists()
    }

    /**
     * 剪切单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * *
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    fun cutFile(oldPath: String, newPath: String) {
        var fi: FileInputStream? = null
        var fo: FileOutputStream? = null
        var `in`: FileChannel? = null
        var out: FileChannel? = null
        var oldFile: File? = null
        try {
            oldFile = File(oldPath)
            if (!oldFile.exists()) {
                LogUtil.d("file", "文件不存在")
                return
            }
            fi = FileInputStream(oldPath)
            fo = FileOutputStream(newPath)
            `in` = fi.channel//得到对应的文件通道
            out = fo.channel//得到对应的文件通道
            `in`!!.transferTo(0, `in`.size(), out)//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (e: IOException) {
            e.printStackTrace()
            LogUtil.d("file", e.message)
        } finally {
            try {
                fi!!.close()
                `in`!!.close()
                fo!!.close()
                out!!.close()
                if (oldFile != null) {
                    oldFile.delete()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(srcFile, destFile)) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createDir(destFile.parentFile)) false else try {
            (writeFileFromIS(destFile, FileInputStream(srcFile),false,null)
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun writeFileFromIS(
        file: File,
        `is`: InputStream?,
        append: Boolean,
        listener: OnProgressUpdateListener?
    ): Boolean {
        if (`is` == null || !createFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append), sBufferSize)
            if (listener == null) {
                val data = ByteArray(sBufferSize)
                var len: Int
                while (`is`.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                }
            } else {
                val totalSize = `is`.available().toDouble()
                var curSize = 0
                listener.onProgressUpdate(0.0)
                val data = ByteArray(sBufferSize)
                var len: Int
                while (`is`.read(data).also { len = it } != -1) {
                    os.write(data, 0, len)
                    curSize += len
                    listener.onProgressUpdate(curSize / totalSize)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnProgressUpdateListener {
        fun onProgressUpdate(progress: Double)
    }

    interface OnReplaceListener {
        fun onReplace(srcFile: File?, destFile: File?): Boolean
    }
}


