package com.example.tabletdrawing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabletdrawing.databinding.ItemDrawingBinding

class DrawingListAdapter(
    private val drawingList: ArrayList<DrawingCanvas>,
    private val callback: ChangeDrawingCanvas
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemDrawingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrawingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = drawingList[position]
        (holder as? DrawingListViewHolder)?.onBind(false, position,  callback)
    }

    override fun getItemCount(): Int {
        return drawingList.size
    }

    fun addDrawingList(drawingCanvas: DrawingCanvas) {
        drawingList.add(drawingCanvas)
        notifyDataSetChanged()
    }

    fun getDrawingCanvasList() = drawingList

}