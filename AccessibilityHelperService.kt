package com.sanaya.assistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AccessibilityHelperService : AccessibilityService() {

    // Yeh un apps ki list hai jinhe Sanaya protect karegi
    private val protectedApps = setOf(
        "com.google.android.apps.photos", // Google Photos
        "com.android.gallery3d",          // Default Android Gallery
        "com.sec.android.gallery3d",      // Samsung Gallery
        "com.miui.gallery",               // Xiaomi/Redmi Gallery
        "com.oneplus.gallery"             // OnePlus Gallery
    )

    private var isUserVoiceVerified = false 

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        this.serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            // Agar inme se koi app khulta hai aur awaaz verified nahi hai
            if (packageName in protectedApps && !isUserVoiceVerified) {
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Screen turant lock kar do
                    val locked = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                    
                    if (locked) {
                        // Sanaya ko bolne ka message bhejo
                        val intent = Intent("com.sanaya.ACTION_SPEAK")
                        intent.putExtra("message", "Sir, kisi ne aapki private photos dekhne ki koshish ki, maine screen lock kar di hai.")
                        sendBroadcast(intent)
                    }
                } else {
                    // Purane phone me Home screen par phek do
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    Toast.makeText(this, "SANAYA: Gallery Access Denied!", Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent("com.sanaya.ACTION_SPEAK")
                    intent.putExtra("message", "Gallery access denied kar diya gaya hai.")
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onInterrupt() {
    }
}
