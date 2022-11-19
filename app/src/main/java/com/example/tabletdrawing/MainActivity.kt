package com.example.tabletdrawing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.tabletdrawing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    binding.drawingCanvas.setImageURI(it)
                }
            }
        }

    val readStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pen.setOnClickListener {
            binding.drawingCanvas.setMode(DrawingCanvas.MODE_PEN)
        }

        binding.areaEraser.setOnClickListener {
            binding.drawingCanvas.setMode(DrawingCanvas.MODE_AREA_ERASER)
        }

        binding.strokeEraser.setOnClickListener {
            binding.drawingCanvas.setMode(DrawingCanvas.MODE_STROKE_ERASER)
        }

        binding.clearAll.setOnClickListener {
            binding.drawingCanvas.setMode(DrawingCanvas.MODE_CLEAR_ALL)
        }

        binding.rectangle.setOnClickListener {
            startActivity(Intent(this, RectangleDrawActivity::class.java))
        }

        binding.callImage.setOnClickListener {
            activityLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            })
        }

        binding.saveBitmap.setOnClickListener {
            binding.drawingCanvas.saveDrawing()
        }

        requestPermission()

        Log.d("containerSize", "create ${binding.container.width} & ${binding.container.height}")
    }

    private fun requestPermission() {
        val isReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val isWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val permissionArray = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

        } else {
            if (isReadPermission) {
                requestReadPermission()
            }
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        } else {
            if (isWritePermission) {
                requestWritePermission()
            }
        }
    }

    private fun requestReadPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_PERMISSION_REQUEST_CODE)
    }

    private fun requestWritePermission() {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val READ_PERMISSION_REQUEST_CODE = 100
        private const val WRITE_PERMISSION_REQUEST_CODE = 200
    }
}