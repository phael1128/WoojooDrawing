package com.example.tabletdrawing

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.tabletdrawing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val drawingList = ArrayList<DrawingCanvas>()
    private lateinit var drawingListAdapter: DrawingListAdapter
    private lateinit var drawingCanvas: DrawingCanvas

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let {
                    drawingCanvas.setImageBitmap(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingCanvas = DrawingCanvas(this)
        drawingList.add(drawingCanvas)
        binding.layoutDrawingCanvas.addView(drawingList[0])


        drawingListAdapter = DrawingListAdapter(drawingList) { position ->
            // 현재 문제점,,,
            // ViewGroup 에 replace 하는게 있는줄 알았는데 무조건 removeAll 를 해줘야 함
            // 그래서 View를 변경해도 계속 새로운 Canvas가 호출 됨,,
            // 그래서 ViewPager를 만들어서 ViewGroup에 addView를 하는게 아니라
            // Fragment를 바꿔주는 식으로 해주면 slide 도 할 수있고 생명주기도 보존 가능하니 1석 2조 일듯,,!
            binding.layoutDrawingCanvas.removeAllViews()
            binding.layoutDrawingCanvas.addView(drawingListAdapter.getDrawingCanvasList()[position])
        }

        binding.recyclerViewDrawingList.adapter = drawingListAdapter
        drawingListAdapter.notifyDataSetChanged()


        binding.pen.setOnClickListener {
            drawingCanvas.setMode(DrawingCanvas.MODE_PEN)
        }

        binding.areaEraser.setOnClickListener {
            drawingCanvas.setMode(DrawingCanvas.MODE_AREA_ERASER)
        }

        binding.strokeEraser.setOnClickListener {
            drawingCanvas.setMode(DrawingCanvas.MODE_STROKE_ERASER)
        }

        binding.clearAll.setOnClickListener {
            drawingCanvas.setMode(DrawingCanvas.MODE_CLEAR_ALL)
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
            drawingCanvas.saveDrawing()
        }

        binding.ivAdd.setOnClickListener {
            addCanvas()
        }

        requestPermission()
    }

    private fun addCanvas() {
        val newDrawing = DrawingCanvas(this)
        drawingListAdapter.addDrawingList(newDrawing)
    }

    private fun requestPermission() {
        val isReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val isWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (!isReadPermission) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                goPermissionGrant()
            } else {
                requestReadPermission()
            }
        }

        if (!isWritePermission) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                goPermissionGrant()
            } else {
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

    private fun goPermissionGrant() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${packageName}")
        })
    }

    companion object {
        private const val READ_PERMISSION_REQUEST_CODE = 100
        private const val WRITE_PERMISSION_REQUEST_CODE = 200
    }
}