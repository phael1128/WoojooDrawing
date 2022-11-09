package com.example.tabletdrawing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.tabletdrawing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val canvas = DrawingCanvas(this)
        binding.container.addView(canvas)

        binding.pen.setOnClickListener {
            canvas.setMode(DrawingCanvas.MODE_PEN)
        }

        binding.areaEraser.setOnClickListener {
            canvas.setMode(DrawingCanvas.MODE_AREA_ERASER)
        }

        binding.strokeEraser.setOnClickListener {
            canvas.setMode(DrawingCanvas.MODE_STROKE_ERASER)
        }

        binding.clearAll.setOnClickListener {
            canvas.setMode(DrawingCanvas.MODE_CLEAR_ALL)
        }

        binding.rectangle.setOnClickListener {
            startActivity(Intent(this, RectangleDrawActivity::class.java))
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