package com.sky.screenpainter.activity

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.sky.screenpainter.R

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingIcon: ImageView
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        floatingIcon = ImageView(this).apply {
            setImageResource(R.drawable.icon_painter)
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 300

        floatingIcon.setOnTouchListener(FloatingTouchListener())
        floatingIcon.setOnClickListener {
            if (OverlayState.isDrawingActive) {
                // ✅ 如果画板已经显示 → 关闭
                sendBroadcast(Intent("CLOSE_DRAWING"))
            } else {
                // ✅ 如果未显示 → 打开
                val intent = Intent(this@FloatingService, DrawingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        windowManager.addView(floatingIcon, params)
    }

    inner class FloatingTouchListener : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        private var isClick = false

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isClick = true
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                        isClick = false
                        params.x = initialX + dx
                        params.y = initialY + dy
                        windowManager.updateViewLayout(floatingIcon, params)
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        v?.performClick()   // 触发 OnClickListener
                    }
                    return true
                }
            }
            return false
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}