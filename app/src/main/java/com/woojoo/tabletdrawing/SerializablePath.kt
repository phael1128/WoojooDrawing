package com.woojoo.tabletdrawing

import android.graphics.Path
import android.graphics.PointF
import java.io.Serializable

class SerializablePath: Path(), Serializable {

    private var pathList = ArrayList<PointF>()

    override fun moveTo(x: Float, y: Float) {
        pathList.add(PointF(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        pathList.add(PointF(x, y))
        super.lineTo(x, y)
    }

    fun getPathList() = pathList
}