package com.nova.assistant.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class VoiceRecognitionService : Service() {
    
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListening = false
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onCreate() {
        super.onCreate()
        setupSpeechRecognizer()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isListening) {
            startListening()
        }
        return START_STICKY
    }
    
    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                Log.d("NovaVoice", "Ready for speech")
            }
            
            override fun onBeginningOfSpeech() {
                Log.d("NovaVoice", "Speech started")
            }
            
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                Log.d("NovaVoice", "Speech ended")
            }
            
            override fun onError(error: Int) {
                Log.e("NovaVoice", "Error: $error")
                isListening = false
                // Restart listening after error
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    startListening()
                }, 1000)
            }
            
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    processCommand(spokenText)
                }
            }
            
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
    }
    
    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sw-KE")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer.startListening(intent)
        isListening = true
    }
    
    private fun processCommand(command: String) {
        Log.d("NovaVoice", "Command: $command")
        // Process command and send broadcast
        val intent = Intent("com.nova.VOICE_COMMAND").apply {
            putExtra("command", command)
        }
        sendBroadcast(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
