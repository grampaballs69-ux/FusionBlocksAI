package com.fusionblocks.ai

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class FusionAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun dragBlock(fromX: Float, fromY: Float, toX: Float, toY: Float, durationMs: Long = 500L, done: (() -> Unit)? = null) {
        val path = Path().apply { moveTo(fromX, fromY); lineTo(toX, toY) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) { done?.invoke() }
            override fun onCancelled(gestureDescription: GestureDescription?) { done?.invoke() }
        }, Handler(Looper.getMainLooper()))
    }
}
