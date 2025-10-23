package com.example.discoclub.ui.screen

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//crear nombre del archivo en el caché

private fun createTempFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir, "images").apply {
        if (!exists()) mkdirs()
    }
    return File(storageDir, "IMG_${timeStamp}.jpg")
}

