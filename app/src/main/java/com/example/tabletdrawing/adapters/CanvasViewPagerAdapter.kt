package com.example.tabletdrawing.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tabletdrawing.CanvasFragment
import com.example.tabletdrawing.customView.DrawingCanvas

class CanvasViewPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    private val drawingList: ArrayList<DrawingCanvas>
): FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return drawingList.size
    }

    override fun createFragment(position: Int): Fragment {
        return CanvasFragment()
    }

    fun getDrawingList() = drawingList

}
