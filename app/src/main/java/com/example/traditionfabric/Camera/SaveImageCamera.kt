package com.example.traditonfabric.ui.Camera

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

class SaveImageCamera {
    fun saveImage(image: Bitmap) {
        val file = File("/path/to/save/image.jpg")
        val outputStream = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    }
}
