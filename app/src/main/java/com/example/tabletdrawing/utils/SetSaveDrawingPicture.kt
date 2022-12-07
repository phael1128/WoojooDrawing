package com.example.tabletdrawing.utils

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tabletdrawing.R
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder

fun Fragment.setSaveDrawingPictureListener(bitmap: Bitmap) {
    val savePath = getSaveFolder()
    val fileName = getImageFileName()
    val file = File(savePath, fileName)
    if (file.exists()) file.delete()

    try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

        out.flush()
        out.close()

        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.absolutePath), null) { _, uri ->
            Log.d("saved Complete", "$uri")
            Toast.makeText(requireContext(), this.getString(R.string.complete_save), Toast.LENGTH_SHORT).show()
        }
    } catch (e: java.lang.Exception) {
        Log.e("Canvas Save Fail", "${e.message}")
    }
}

fun getSaveFolder(): File {
    val rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
    val dirName = "/Drawing"
    val file = File(rootPath + dirName)
    if (!file.exists()) {
        file.mkdirs()
    }
    return file
}

fun getImageFileName() = StringBuilder().append(System.currentTimeMillis()).append(".jpg").toString()