package com.moban.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import com.moban.R
import com.mvc.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_booking_detail.*
import java.io.*


/**
 * Created by LenVo on 3/22/18.
 */
class BitmapUtil {
    companion object {
        private val MAX_SIZE_AVATAR = 400
        private val MAX_SIZE_IMAGE = 2048

        fun convertDpToPx(context: Context, dp: Int): Int {
            val r = context.resources
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
                    r.displayMetrics).toInt()
        }

        fun getImageUri(context: Context, bm: Bitmap, scale: Int, title: String): Uri? {
            val bytes = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, scale, bytes)
            val path = MediaStore.Images.Media
                    .insertImage(context.contentResolver, bm, title, null) ?: return null
            return Uri.parse(path)
        }

        fun convertToBytes(scale: Int, bm: Bitmap): ByteArray {
            val bytes = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, scale, bytes)
            return bytes.toByteArray()
        }

        fun resizeBitmap(bm: Bitmap, maxSize: Int): Bitmap {
            val width = bm.width
            val height = bm.height
            var scale = 1f
            if (height > maxSize) {
                scale = maxSize.toFloat() / height.toFloat()
            }

            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scale, scale)

            // "RECREATE" THE NEW BITMAP

            return Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false)
        }

        fun getSquareBitmap(bm: Bitmap): Bitmap {
            val result: Bitmap
            val width = bm.width
            val height = bm.height

            if (width >= height) {
                result = Bitmap.createBitmap(bm,
                        width / 2 - height / 2, 0,
                        height, height
                )

            } else {
                result = Bitmap.createBitmap(bm,
                        0, height / 2 - width / 2,
                        width, width
                )
            }

            return result
        }

        fun getResizedBitmap(bm: Bitmap): Bitmap {
            return resizeBitmap(bm, MAX_SIZE_IMAGE)
        }

        fun getResizedAvatar(bm: Bitmap): Bitmap {
            return resizeBitmap(bm, MAX_SIZE_AVATAR)
        }

        fun getBitmapFromFile(photoPath: String): Bitmap {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val bitmap = BitmapFactory.decodeFile(photoPath, options)

            val exif = ExifInterface(photoPath)
            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

           rotateBitmap(bitmap, rotation)?.let {
               if (bitmap != it) {
                   return it
               }
           }

            return bitmap
        }

        private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
            val matrix = Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> return bitmap
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)

                else -> return bitmap
            }

            val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()

            return bmRotated
        }

        fun saveImage(context: Context, inputStream: InputStream, imageName: String): Boolean {
            try {
                // Assume block needs to be inside a Try/Catch block.
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                val file = File(path, imageName) // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                val fOut = FileOutputStream(file)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush() // Not really required
                fOut.close() // do not forget to close the stream

                MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, file.name)

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.parse(path)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)

                MediaScannerConnection.scanFile(context, arrayOf(contentUri.path), null) { pathFile, uri ->
                    Log.i("ExternalStorage", "Scanned $pathFile:")
                    Log.i("ExternalStorage", "-> uri=$uri")
                }

                Toast.makeText(context, context.getText(R.string.downloaded_image), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.getText(R.string.download_image_failed), Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }

        fun openPickImage(activity: Activity, requestCode: Int = 0) {
            ImagePicker.setMinQuality(600, 600)
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            ImagePicker.pickImage(activity, requestCode)
        }
    }
}
