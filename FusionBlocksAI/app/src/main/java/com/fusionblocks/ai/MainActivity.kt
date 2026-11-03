package com.fusionblocks.ai

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Color

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val projectionRequest = 4102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.rgb(13, 20, 34))
        }
        fun label(text: String, size: Float, color: Int = Color.WHITE) = TextView(this).apply {
            this.text = text; textSize = size; setTextColor(color); setPadding(0, 8, 0, 12)
        }
        root.addView(label("FUSION BLOCKS AI", 25f))
        root.addView(label("On-device puzzle assistant", 15f, Color.LTGRAY))
        status = label("Setup required. Enable Accessibility, then allow screen capture.", 14f, Color.rgb(150, 200, 255))
        root.addView(status)
        root.addView(actionButton("1  •  Enable drag accessibility") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            status.text = "In Accessibility settings, enable Fusion Blocks AI, then return here."
        })
        root.addView(actionButton("2  •  Allow screen capture") {
            val mgr = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mgr.createScreenCaptureIntent(), projectionRequest)
        })
        root.addView(actionButton("3  •  Open latest screenshot folder") {
            status.text = "Screenshots are saved in this app's Pictures/FusionBlocks folder. Connect a file manager to inspect them."
        })
        root.addView(actionButton("Stop capture") {
            stopService(Intent(this, ScreenCaptureService::class.java))
            status.text = "Screen capture stopped."
        })
        root.addView(label("How to use", 19f))
        root.addView(label("• Keep Fusion Blocks open on your phone.\n• Grant screen-capture permission when Android asks.\n• The app can capture the game screen while the capture service is running.\n• Automated dragging requires the Accessibility service to be enabled.\n• Do not start automatic moves until the board and block recognition have been calibrated for your exact game version.", 14f, Color.LTGRAY))
        root.addView(label("Important: this initial build contains the Android capture/gesture foundation and the placement-search engine. Automatic visual recognition still needs calibration against a screenshot of your exact Fusion Blocks screen; it will not guess coordinates or move blocks blindly.", 13f, Color.rgb(255, 205, 120)))
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
    }

    private fun actionButton(title: String, action: () -> Unit) = Button(this).apply {
        text = title; isAllCaps = false; setOnClickListener { action() }
    }

    @Deprecated("Android activity result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == projectionRequest && resultCode == RESULT_OK && data != null) {
            val i = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("resultCode", resultCode); putExtra("projectionData", data)
            }
            startForegroundService(i)
            status.text = "Screen capture running. Switch to Fusion Blocks. This build saves screenshots for calibration."
        }
    }
}
