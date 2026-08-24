package com.nova.assistant.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nova.assistant.R
import com.nova.assistant.databinding.ActivityMainBinding
import com.nova.assistant.services.BackgroundService
import com.nova.assistant.services.WhatsAppService
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private var isListening = false
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        tts = TextToSpeech(this, this)
        
        setupSpeechRecognizer()
        setupClickListeners()
        checkPermissions()
        startBackgroundService()
        startPulseAnimation()
    }
    
    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.novaText.text = getString(R.string.listening)
                binding.responseText.text = "Sikiliza..."
            }
            
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                binding.novaText.text = getString(R.string.thinking)
                binding.responseText.text = "Inafikiria..."
            }
            
            override fun onError(error: Int) {
                Log.e("Nova", "Speech error: $error")
                binding.novaText.text = getString(R.string.nova_ready)
                binding.responseText.text = "Jaribu tena"
                isListening = false
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    processVoiceCommand(spokenText)
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    
    private fun setupClickListeners() {
        binding.speakButton.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        }
        
        binding.settingsButton.setOnClickListener {
            Toast.makeText(this, "Mipangilio", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_CODE
            )
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sw-KE")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        speechRecognizer.startListening(intent)
        isListening = true
        binding.novaText.text = getString(R.string.listening)
    }
    
    private fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
        binding.novaText.text = getString(R.string.nova_ready)
    }
    
    private fun processVoiceCommand(command: String) {
        val lowerCommand = command.lowercase()
        
        when {
            lowerCommand.contains("tuma ujumbe") || lowerCommand.contains("send message") -> {
                handleWhatsAppMessage(command)
            }
            lowerCommand.contains("piga simu") || lowerCommand.contains("call") -> {
                handlePhoneCall(command)
            }
            lowerCommand.contains("fungua") || lowerCommand.contains("open") -> {
                handleOpenApp(command)
            }
            lowerCommand.contains("habari") || lowerCommand.contains("hello") -> {
                speak("Habari! Mimi ni Nova, msaidizi wako")
                binding.responseText.text = "Habari! Mimi ni Nova"
            }
            lowerCommand.contains("asante") || lowerCommand.contains("thank") -> {
                speak("Karibu! Nipo hapa kukusaidia")
                binding.responseText.text = "Karibu!"
            }
            else -> {
                speak("Sijaelewa. Jaribu tena")
                binding.responseText.text = "Sijaelewa: $command"
            }
        }
    }
    
    private fun handleWhatsAppMessage(command: String) {
        val intent = Intent(this, WhatsAppService::class.java).apply {
            putExtra("command", command)
        }
        startService(intent)
        speak("Ninatuma ujumbe wa WhatsApp")
        binding.responseText.text = "Ninatuma ujumbe..."
    }
    
    private fun handlePhoneCall(command: String) {
        speak("Ninaanza kupiga simu")
        binding.responseText.text = "Ninapiga simu..."
    }
    
    private fun handleOpenApp(command: String) {
        speak("Ninafungua programu")
        binding.responseText.text = "Ninafungua programu..."
    }
    
    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "nova_speech")
    }
    
    private fun startBackgroundService() {
        val intent = Intent(this, BackgroundService::class.java)
        startForegroundService(intent)
    }
    
    private fun startPulseAnimation() {
        val pulseAnimation = AlphaAnimation(0.3f, 1.0f).apply {
            duration = 1000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.pulseAnimation.startAnimation(pulseAnimation)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("sw", "KE"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.US)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        tts.stop()
        tts.shutdown()
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.POST_NOTIFICATIONS
        )
        
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Ruhusa zote zimetolewa", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
