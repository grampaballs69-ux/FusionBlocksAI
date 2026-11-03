package com.fusionblocks.ai

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import java.io.File
import java.io.FileOutputStream

class ScreenCaptureService : Service() {
    private var projection: MediaProjection? = null
    private var reader: ImageReader? = null
    private var display: VirtualDisplay? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastSaved = 0L

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        val code = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: Activity.RESULT_CANCELED
        val data = intent?.parcelableIntent("projectionData")
        if (code != Activity.RESULT_OK || data == null) { stopSelf(); return START_NOT_STICKY }
        val mgr = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projection = mgr.getMediaProjection(code, data)
        startCapture()
        return START_NOT_STICKY
    }

    private fun createNotification() {
        val channelId = "fusion_capture"
        val nm = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(NotificationChannel(channelId, "Fusion Blocks capture", NotificationManager.IMPORTANCE_LOW))
        val n = Notification.Builder(this, channelId)
            .setContentTitle("Fusion Blocks AI")
            .setContentText("Screen capture is active")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
        startForeground(17, n)
    }

    private fun startCapture() {
        val dm = resources.displayMetrics
        val width = dm.widthPixels; val height = dm.heightPixels; val density = dm.densityDpi
        reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        reader?.setOnImageAvailableListener({ r ->
            val image = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            try {
                val now = System.currentTimeMillis()
                if (now - lastSaved >= 2500) {
                    val plane = image.planes[0]
                    val buffer = plane.buffer
                    val pixelStride = plane.pixelStride; val rowStride = plane.rowStride
                    val rowPadding = rowStride - pixelStride * width
                    val raw = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
                    raw.copyPixelsFromBuffer(buffer)
                    val cropped = Bitmap.createBitmap(raw, 0, 0, width, height)
                    raw.recycle()
                    val dir = File(getExternalFilesDir(null), "captures").apply { mkdirs() }
                    FileOutputStream(File(dir, "latest.png")).use { cropped.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    cropped.recycle(); lastSaved = now
                }
            } finally { image.close() }
        }, handler)
        display = projection?.createVirtualDisplay("FusionBlocksCapture", width, height, density, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, reader?.surface, null, handler)
    }

    override fun onDestroy() {
        display?.release(); reader?.close(); projection?.stop()
        super.onDestroy()
    }

    private inline fun <reified T> Intent.parcelableIntent(key: String): T? =
        if (Build.VERSION.SDK_INT >= 33) getParcelableExtra(key, T::class.java) else @Suppress("DEPRECATION") (getParcelableExtra(key) as? T)
}
