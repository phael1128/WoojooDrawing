package com.example.tabletdrawing

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
        Log.d("containerSize", "create ${binding.container.width} & ${binding.container.height}")
    }

    override fun onStart() {
        super.onStart()

        Log.d("containerSize", " start ${binding.container.width} & ${binding.container.height}")
    }

    override fun onResume() {
        super.onResume()

        Log.d("containerSize", "resume ${binding.container.width} & ${binding.container.height}")
    }
}