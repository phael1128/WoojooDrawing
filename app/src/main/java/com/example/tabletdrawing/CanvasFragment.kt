package com.example.tabletdrawing

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tabletdrawing.customView.DrawingCanvas
import com.example.tabletdrawing.databinding.FragmentCanvasBinding
import java.io.File
import java.io.FileOutputStream

class CanvasFragment: Fragment() {

   private lateinit var binding: FragmentCanvasBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCanvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSavePictureListener()
    }

    fun setMode(mode: DrawingMode) {
        when (mode) {
            is DrawingMode.PenMode -> {
                binding.layoutDrawing.setMode(DrawingCanvas.MODE_PEN)
            }
            is DrawingMode.EraserMode -> {
                binding.layoutDrawing.setMode(DrawingCanvas.MODE_AREA_ERASER)
            }
            else -> {
                binding.layoutDrawing.setMode(DrawingCanvas.MODE_CLEAR_ALL)
            }
        }
    }

    fun setBitmap(uri: Uri) {
        binding.layoutDrawing.setImageBitmap(uri)
    }

    fun saveCurrentDrawing() {
        binding.layoutDrawing.saveDrawing()
    }

    private fun setSavePictureListener() {
        binding.layoutDrawing.setSavePictureListener { bitmap ->
            val environmentState = Environment.getExternalStorageState()

            if (Environment.MEDIA_MOUNTED == environmentState) {
                val rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                val dirName = "/Drawing"
                val fileName = "${System.currentTimeMillis()}.png"
                val savePath = File(rootPath + dirName)
                savePath.mkdirs()

                val file = File(savePath, fileName)
                if (file.exists()) file.delete()

                try {
                    val out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

                    out.flush()
                    out.close()

                    MediaScannerConnection.scanFile(requireContext(), arrayOf(file.absolutePath), null) { _, uri ->
                        Log.d("saved Complete", "$uri")
                        Toast.makeText(requireContext(), "저장완료", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("Canvas Save Fail", "${e.message}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}


