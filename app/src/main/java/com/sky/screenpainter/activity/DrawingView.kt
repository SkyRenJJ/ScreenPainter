package com.sky.screenpainter.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)  {

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }
    private val paths = mutableListOf<Path>()    // 保存所有笔画
    private var currentPath: Path? = null

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply {
                    moveTo(event.x, event.y)
                }
                paths.add(currentPath!!)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(event.x, event.y)
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (p in paths) {
            canvas.drawPath(p, paint)
        }
    }

    /** ✅ 清空所有路径 */
    fun clear() {
        paths.clear()
        invalidate()
    }

    /** ✅ 获取所有路径（保存用） */
    fun getPaths(): List<Path> = paths.toList()

    /** ✅ 恢复路径 */
    fun restorePaths(saved: List<Path>) {
        paths.clear()
        paths.addAll(saved)
        invalidate()
    }
}
