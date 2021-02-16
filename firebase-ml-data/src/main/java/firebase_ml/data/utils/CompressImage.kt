package firebase_ml.data.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object CompressImage {
    fun getCompressedImageFile(
        file: File
    ): String? {
        return try {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inSampleSize = 6
            var inputStream = FileInputStream(file)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            // The new size we want to scale to
            val REQUIRED_SIZE = 100
            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE &&
                options.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            options = BitmapFactory.Options()
            options.inSampleSize = scale
            inputStream = FileInputStream(file)
            var selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options)
            val ei =
                ExifInterface(file.absolutePath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> selectedBitmap =
                    rotateImage(selectedBitmap!!, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> selectedBitmap =
                    rotateImage(selectedBitmap!!, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> selectedBitmap =
                    rotateImage(selectedBitmap!!, 270f)
                ExifInterface.ORIENTATION_NORMAL -> {
                }
                else -> {
                }
            }
            inputStream.close()

            val openCvFileName = file.absolutePath.replace(".jpg", "compressed.jpg")
            val outputStream = FileOutputStream(openCvFileName)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            openCvFileName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}