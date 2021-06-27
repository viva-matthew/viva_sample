package com.example.viva_sample.tts

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.orhanobut.logger.Logger
import xyz.arpith.blearduino.R
import xyz.arpith.blearduino.databinding.ActivityTtsBinding
import java.util.*

class TtsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val binding: ActivityTtsBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_tts) }
    private val tts: TextToSpeech? by lazy { TextToSpeech(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.lifecycleOwner = this



        binding.btnOk.setOnClickListener {
            speakOut()
        }

    }

    private fun speakOut() {


        val text: CharSequence = binding.etText.text
        tts?.setPitch(0.6f)
        tts?.setSpeechRate(0.1f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1")

    }

    override fun onDestroy() {
        tts.let {
            tts?.stop()
            tts?.shutdown()
        }

        super.onDestroy()

    }

    override fun onInit(status: Int) {
        Logger.d("## onInit")
        if (status === TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.KOREA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported")
            } else {
                binding.btnOk.isEnabled = true
                speakOut()
            }
        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }
}