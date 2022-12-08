package com.woojoo.tabletdrawing.activitys

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.woojoo.tabletdrawing.CanvasFragment
import com.woojoo.tabletdrawing.DrawingMode
import com.woojoo.tabletdrawing.adapters.CanvasViewPagerAdapter
import com.woojoo.tabletdrawing.adapters.DrawingListAdapter
import com.woojoo.tabletdrawing.customView.DrawingCanvas
import com.woojoo.tabletdrawing.databinding.ActivityMainBinding
import com.woojoo.tabletdrawing.utils.checkWriteStoragePermission
import com.woojoo.tabletdrawing.utils.checkReadStoragePermission

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val drawingList = ArrayList<DrawingCanvas>()
    private lateinit var drawingListAdapter: DrawingListAdapter
    private lateinit var viewPagerAdapter: CanvasViewPagerAdapter

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                imageUri?.let { imageUri ->
                    val findFragment = getCurrentFragment()
                    findFragment?.let { canvasFragment ->
                        canvasFragment.setBitmap(imageUri)
                    } ?: run {
                        Log.d("NotFoundFragment", "${binding.viewPagerDrawing.currentItem}")
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = CanvasViewPagerAdapter(this, drawingList)
        binding.viewPagerDrawing.adapter = viewPagerAdapter

        binding.viewPagerDrawing.isUserInputEnabled = false

        drawingListAdapter = DrawingListAdapter(drawingList) { position ->
            binding.viewPagerDrawing.currentItem = position
            Log.d("currentPosition", "$position")
        }

        binding.recyclerViewDrawingList.adapter = drawingListAdapter

        addCanvas()
        binding.viewPagerDrawing.currentItem = 0

        initView()
        requestPermission()
    }

    private fun initView() {
        binding.imageViewPen.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.PenMode)
        }

        binding.imageViewAreaEraser.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.EraserMode)
        }

        binding.imageViewClearAll.setOnClickListener {
            getCurrentFragment()?.setMode(DrawingMode.ClearAllMode)
        }

        binding.imageViewRectangle.setOnClickListener {
//            startActivity(Intent(this, RectangleDrawActivity::class.java))
            getCurrentFragment()?.setMode(DrawingMode.CropMode)
        }

        binding.imageViewGetImage.setOnClickListener {
            activityLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            })
        }

        binding.imageViewSaveBitmap.setOnClickListener {
            getCurrentFragment()?.saveCurrentDrawing()
        }

        binding.imageViewAdd.setOnClickListener {
            addCanvas()
        }
    }

    private fun addCanvas() {
        val newDrawing = DrawingCanvas(this)
        drawingList.add(newDrawing)
        drawingListAdapter.notifyDataSetChanged()
        viewPagerAdapter.notifyDataSetChanged()
    }

    private fun requestPermission() {
        checkReadStoragePermission()
        checkWriteStoragePermission()
    }

    private fun getCurrentFragment(): CanvasFragment? {
        return supportFragmentManager.findFragmentByTag("f" + binding.viewPagerDrawing.currentItem) as? CanvasFragment
    }
}