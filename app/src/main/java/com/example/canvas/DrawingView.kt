package com.example.paintapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var currentPath = Path()
    private var currentPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var backgroundBitmap: Bitmap? = null

    fun setColor(color: Int) {
        currentPaint.color = color
    }

    fun setBrushSize(size: Float) {
        currentPaint.strokeWidth = size
    }

    fun setBackgroundBitmap(bitmap: Bitmap) {
        backgroundBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        invalidate()
    }

    fun getBitmap(): Bitmap {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        draw(canvas)
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
                paths.add(Pair(currentPath, Paint(currentPaint)))
            }
            MotionEvent.ACTION_MOVE -> currentPath.lineTo(event.x, event.y)
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }
    }

    fun clearCanvas() {
        paths.clear()
        currentPath.reset()
        backgroundBitmap = null
        invalidate()
    }

}
