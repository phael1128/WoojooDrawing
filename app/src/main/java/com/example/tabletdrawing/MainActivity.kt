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
import com.example.tabletdrawing.adapters.CanvasViewPagerAdapter
import com.example.tabletdrawing.adapters.DrawingListAdapter
import com.example.tabletdrawing.customView.DrawingCanvas
import com.example.tabletdrawing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val drawingList = ArrayList<DrawingCanvas>()
    private lateinit var drawingListAdapter: DrawingListAdapter
    private lateinit var drawingCanvas: DrawingCanvas
    private lateinit var viewPagerAdapter: CanvasViewPagerAdapter
    private var currentPosition = 0

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { imageUri ->
                    //findFragment 함수로 하나 만들기
                    val findFragment = getCurrentFragment()
                    findFragment?.let {
                        it.setBitmap(imageUri)
                    } ?: run {
                        Log.d("NotFountFragment", "${binding.viewPagerDrawing.currentItem}")
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingCanvas = DrawingCanvas(this)
        viewPagerAdapter = CanvasViewPagerAdapter(this, drawingList)
        binding.viewPagerDrawing.adapter = viewPagerAdapter

        binding.viewPagerDrawing.isUserInputEnabled = false

        drawingListAdapter = DrawingListAdapter(drawingList) { position ->
            binding.viewPagerDrawing.currentItem = position
            currentPosition = position
            Log.d("currentPosition", "${currentPosition}")
        }

        binding.recyclerViewDrawingList.adapter = drawingListAdapter
        drawingListAdapter.notifyDataSetChanged()

        addCanvas()

        binding.viewPagerDrawing.currentItem = 0
        currentPosition = 0

        setDrawingListener()
        requestPermission()
    }

    private fun addCanvas() {
        val newDrawing = DrawingCanvas(this)
        drawingList.add(newDrawing)
        drawingListAdapter.notifyDataSetChanged()
        viewPagerAdapter.notifyDataSetChanged()
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

    private fun setDrawingListener() {
        binding.pen.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.PenMode)
        }

        binding.areaEraser.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.EraserMode)
        }

        binding.clearAll.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.ClearAllMode)
        }

        binding.rectangle.setOnClickListener {
            startActivity(Intent(this, RectangleDrawActivity::class.java))
        }

        binding.imageViewGetImage.setOnClickListener {
            activityLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            })
        }

        binding.imageViewSaveBitmap.setOnClickListener {
            getCurrentFragment()?.saveCurrentDrawing()
        }

        binding.ivAdd.setOnClickListener {
            addCanvas()
        }
    }

    private fun getCurrentFragment(): CanvasFragment? {
        return supportFragmentManager.findFragmentByTag("f" + binding.viewPagerDrawing.currentItem) as? CanvasFragment
    }

    companion object {
        private const val READ_PERMISSION_REQUEST_CODE = 100
        private const val WRITE_PERMISSION_REQUEST_CODE = 200
    }
}