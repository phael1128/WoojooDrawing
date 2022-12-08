package com.woojoo.tabletdrawing

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.woojoo.tabletdrawing.customView.DrawingCanvas
import com.woojoo.tabletdrawing.databinding.FragmentCanvasBinding
import com.woojoo.tabletdrawing.utils.setSaveDrawingPictureListener

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
            is DrawingMode.CropMode -> {
                binding.layoutDrawing.setMode(DrawingCanvas.MODE_CROP)
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


