package com.example.tabletdrawing

data class Pen(
    var stateStart: Int = 0,
    var stateMove: Int = 1,
    val x: Float,
    val y: Float,
    var moveStatus: Int
) {
    fun isMove() = moveStatus == stateMove
}