package com.github.xpenatan.maskview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class Utils {
    companion object {
        // TODO needs to improve performance
        fun cropURI(context: Context, inputUri: Uri, outputFile: File, startX: Int, startY: Int, endX: Int, endY: Int) {
            // create bitmap from original uri and crop it
            val imageStream = context.contentResolver.openInputStream(inputUri)
            val bitmap = BitmapFactory.decodeStream(imageStream)
            val rotate = getImageOrientation(inputUri.toFile())
            val matrix = Matrix()
            matrix.reset()
            matrix.postRotate(rotate)

            // TODO needs to convert and calculate aspect ratio
//            val newBitmap = Bitmap.createBitmap(bitmap, startX, startY, endX, endY, matrix, true)
            val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            imageStream?.close()
            //write the bytes in file
            val bytes = ByteArrayOutputStream()
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val bitmapData: ByteArray = bytes.toByteArray()

            val fos = FileOutputStream(outputFile)
            fos.write(bitmapData)
            fos.flush();
            fos.close();
            bitmap.recycle()
            newBitmap.recycle()
        }

        private fun getImageOrientation(imageInput: File): Float {
            // https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
            // Most phone cameras are landscape, meaning if you take the photo in portrait,
            // the resulting photos will be rotated 90 degrees.
            // In this case, the camera software should populate the Exif data with
            // the orientation that the photo should be viewed in.
            val ei = ExifInterface(imageInput)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                ExifInterface.ORIENTATION_NORMAL -> 0f
                else -> 0f
            }
        }
    }
}