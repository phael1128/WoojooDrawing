package com.woojoo.tabletdrawing

import android.graphics.Path
import android.graphics.PointF
import java.io.Serializable
import java.util.LinkedList

class SerializablePath: Path(), Serializable {

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
    }

}