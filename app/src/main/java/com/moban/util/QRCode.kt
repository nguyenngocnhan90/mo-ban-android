package com.moban.util

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix


/**
 * Created by LenVo on 4/10/18.
 */
class QRCode {
    companion object {
        fun encodeAsBitmap(content: String): Bitmap? {
            val result: BitMatrix
            val width = Device.getScreenWidth() * 2 / 3
            try {
                result = MultiFormatWriter().encode(content,
                        BarcodeFormat.QR_CODE, width, width, null)
            } catch (iae: IllegalArgumentException) {
                // Unsupported format
                return null
            } catch (e: WriterException) {
                e.printStackTrace()
                return null
            }

            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
            return bitmap
        }
    }
}
