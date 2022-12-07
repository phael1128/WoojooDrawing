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
import com.example.tabletdrawing.utils.setSaveDrawingPictureListener
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
            setSaveDrawingPictureListener(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}


