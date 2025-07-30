package com.sky.screenpainter.activity


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import com.sky.screenpainter.R

object OverlayState {
    var isDrawingActive = false
    var savePaths: List<Path>? = null  // 保存所有笔画
}

class DrawingActivity : Activity() {
    private val closeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "CLOSE_DRAWING") {
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }
    private var drawingView: DrawingView? = null
    private var clearBtn: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OverlayState.isDrawingActive = true
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_drawing)

        drawingView = findViewById<DrawingView>(R.id.drawing_view)
        clearBtn = findViewById<ImageView>(R.id.btn_clear)

        clearBtn?.setOnClickListener {
            drawingView?.clear()
        }


        OverlayState.savePaths?.let {
            drawingView?.restorePaths(it)
        }
        OverlayState.isDrawingActive = true
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(closeReceiver, IntentFilter("CLOSE_DRAWING"),RECEIVER_EXPORTED)
        }else{
            registerReceiver(closeReceiver, IntentFilter("CLOSE_DRAWING"))
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(closeReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        OverlayState.savePaths = drawingView?.getPaths()
        OverlayState.isDrawingActive = false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.pointerCount > 1) return false
        return super.onTouchEvent(event)
    }
}
