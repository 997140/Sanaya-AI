package com.sanaya.assistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import java.util.Locale

class SanayaVoiceReceiver : BroadcastReceiver(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var messageToSpeak: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.sanaya.ACTION_SPEAK") {
            messageToSpeak = intent.getStringExtra("message")
            
            // Text-To-Speech engine chalu karo
            if (tts == null) {
                tts = TextToSpeech(context.applicationContext, this)
            } else {
                speakOut()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Awaaz ko Indian accent me set karna
            val result = tts?.setLanguage(Locale("hi", "IN"))
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale("en", "IN"))
            }
            speakOut()
        }
    }

    private fun speakOut() {
        messageToSpeak?.let {
            tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }
}
