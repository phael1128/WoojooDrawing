package com.woojoo.tabletdrawing

import android.graphics.Path
import android.graphics.PointF
import java.io.Serializable
import java.util.Hashtable

class SerializablePath: Path(), Serializable {

    private var pathList = Hashtable<PointF, PointF>()

    override fun moveTo(x: Float, y: Float) {
//        pathList.put(PointF(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
//        pathList.put(PointF(x, y))
        super.lineTo(x, y)
    }

    fun getPathList() = pathList

    fun currentPoint(key: PointF) = pathList.getOrDefault(key, null)
}