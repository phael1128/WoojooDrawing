package com.woojoo.tabletdrawing

sealed class DrawingMode {
    object PenMode: DrawingMode()
    object EraserMode: DrawingMode()
    object CropMode: DrawingMode()
    object ClearAllMode: DrawingMode()
}
