package com.woojoo.tabletdrawing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woojoo.tabletdrawing.interfaces.ChangeDrawingCanvas
import com.woojoo.tabletdrawing.customView.DrawingCanvas
import com.woojoo.tabletdrawing.viewholders.DrawingListViewHolder
import com.woojoo.tabletdrawing.databinding.ItemDrawingBinding

class DrawingListAdapter(
    private val drawingList: ArrayList<DrawingCanvas>,
    private val callback: ChangeDrawingCanvas
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemDrawingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? DrawingListViewHolder)?.onBind(position,  callback)
    }

    override fun getItemCount(): Int {
        return drawingList.size
    }
}