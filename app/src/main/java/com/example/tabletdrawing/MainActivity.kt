package com.example.tabletdrawing

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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

        binding.eraser.setOnClickListener {
            canvas.setMode(DrawingCanvas.MODE_ERASER)
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