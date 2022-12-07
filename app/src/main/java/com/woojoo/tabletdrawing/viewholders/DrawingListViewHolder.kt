package com.woojoo.tabletdrawing.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.woojoo.tabletdrawing.R
import com.woojoo.tabletdrawing.interfaces.ChangeDrawingCanvas
import com.woojoo.tabletdrawing.databinding.ItemDrawingBinding

class DrawingListViewHolder(private val binding: ItemDrawingBinding): RecyclerView.ViewHolder(binding.root) {

    fun onBind(position: Int, callback: ChangeDrawingCanvas) {

        binding.textViewCurrentPage.text = "${position + 1}${binding.root.context.getString(R.string.textView_currentPage)}"

        binding.layoutItem.setOnClickListener {
            callback.onChangeDrawingCanvas(position)
        }

    }
}