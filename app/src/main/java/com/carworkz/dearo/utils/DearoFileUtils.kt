package com.carworkz.dearo.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DearOFileUtils {


    companion object {
        val FOLDER = "DearO"

        fun saveInDownloads(appContext: Context, fromFile: File) {
            val dst = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveInDownloadsForQ(appContext, fromFile.name)
            } else {
                //dont forget to check if you have the permission
                //to WRITE_EXTERNAL_STORAGE, and ask for it if you don't
                saveInDownloadsBelowQ(appContext, fromFile.name)
            }

            dst?.let {
                try {
                    val src = FileInputStream(fromFile)
                    dst.channel.transferFrom(src.channel, 0, src.channel.size())
                    src.close()
                    dst.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


        @RequiresApi(Build.VERSION_CODES.Q)
        private fun saveInDownloadsForQ(appContext: Context, fileName: String): FileOutputStream? {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.DATE_ADDED, (System.currentTimeMillis() / 1000).toInt())
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + File.separator + FOLDER
                )
            }

            val resolver = appContext.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            return uri?.let {
                resolver.openOutputStream(uri) as FileOutputStream
            }
        }

        private fun saveInDownloadsBelowQ(appContext: Context, fileName: String): FileOutputStream {

            val path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).toString()

            val dir = File(path, FOLDER)
            val dirFlag = dir.mkdirs()

            val file = File(dir, fileName)
            return FileOutputStream(file)
        }
    }

}