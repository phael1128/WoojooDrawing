package com.example.tabletdrawing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tabletdrawing.databinding.ActivityNextBinding

class RectangleDrawActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val canvas = RectangleCanvas(this)
        binding.rectangleView.addView(canvas)
    }
}