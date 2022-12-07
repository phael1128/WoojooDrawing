package com.woojoo.tabletdrawing

sealed class DrawingMode {
    object PenMode: DrawingMode()
    object EraserMode: DrawingMode()
    object ClearAllMode: DrawingMode()
}
