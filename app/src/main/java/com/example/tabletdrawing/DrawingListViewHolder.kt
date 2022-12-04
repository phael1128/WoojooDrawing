package com.example.tabletdrawing

import androidx.recyclerview.widget.RecyclerView
import com.example.tabletdrawing.databinding.ItemDrawingBinding

class DrawingListViewHolder(private val binding: ItemDrawingBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind(isTouch: Boolean, position: Int, callback: ChangeDrawingCanvas) {

        binding.layoutItem.setOnClickListener {
            callback.onChangeDrawingCanvas(position)
        }

    }
}