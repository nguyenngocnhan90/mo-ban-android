package com.moban.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import android.util.Log
import com.moban.LHApplication
import okhttp3.ResponseBody
import java.io.*


/**
 * Created by LenVo on 3/22/18.
 */
class FileUtil {
    companion object {
        /**
         * Turn drawable resource into byte array.
         *
         * @param context parent context
         * @param id      drawable resource id
         * @return byte array
         */
        fun getFileDataFromDrawable(context: Context, id: Int): ByteArray {
            val drawable = ContextCompat.getDrawable(context, id)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }

        /**
         * Turn drawable into byte array.
         *
         * @param drawable data
         * @return byte array
         */
        fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }


        fun getFileFromUri(context: Context, uri: Uri): File? {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null) ?: return null

            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val filePath = cursor.getString(columnIndex)
            cursor.close()

            return File(filePath)
        }

        fun getExternalAppFolderPath(): String {
            val folder = File(Environment.getExternalStorageDirectory().path + "/" +
                    LHApplication.instance.packageName)
            if (!folder.exists()) {
                folder.mkdir()
            }

            return  folder.absolutePath + "/"
        }

        fun getFileNameFromURL(url: String): String {
            return url.substring(url.lastIndexOf('/') + 1, url.length)
        }

        fun writeResponseBodyToDisk(body: ResponseBody, fileName: String): String? {
            try {
                // todo change the file location/name according to your needs
                val futureStudioIconFile = File(getExternalAppFolderPath() + File.separator + fileName)

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                try {
                    val fileReader = ByteArray(4096)

                    val fileSize = body.contentLength()
                    var fileSizeDownloaded: Long = 0

                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(futureStudioIconFile)

                    while (true) {
                        val read = inputStream!!.read(fileReader)

                        if (read == -1) {
                            break
                        }

                        outputStream.write(fileReader, 0, read)

                        fileSizeDownloaded += read.toLong()

                        Log.d("DOWNLOAD", "file download: $fileSizeDownloaded of $fileSize")
                    }

                    outputStream.flush()

                    return futureStudioIconFile.path
                } catch (e: IOException) {
                    return null
                } finally {
                    inputStream?.close()

                    outputStream?.close()
                }
            } catch (e: IOException) {
                return null
            }

        }
    }
}
