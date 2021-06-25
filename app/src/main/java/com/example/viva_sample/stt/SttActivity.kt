package com.example.viva_sample.stt

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.orhanobut.logger.Logger
import xyz.arpith.blearduino.R
import xyz.arpith.blearduino.databinding.ActivitySttBinding

class SttActivity : AppCompatActivity() {
    private val binding: ActivitySttBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.activity_stt
        )
    }

    private val mRecognizer: SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(this) }
    private val PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stt)

        binding.lifecycleOwner = this

        if (Build.VERSION.SDK_INT >= 23) { // 퍼미션 체크
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO),
                PERMISSION
            )
        }


        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")


        binding.btnStart.setOnClickListener {
            Logger.d("## btnStart click")
            mRecognizer.setRecognitionListener(recognitionListener())
            mRecognizer.startListening(intent)
        }

        binding.btnClear.setOnClickListener {
            binding.tvSTT.text = ""
        }


    }


    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) =
            Toast.makeText(this@SttActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()

        override fun onRmsChanged(rmsdB: Float) {
            Logger.d("## onRmsChanged")

        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Logger.d("## onBufferReceived")

        }

        override fun onPartialResults(partialResults: Bundle?) {
            Logger.d("## onPartialResults")

        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Logger.d("## onEvent")
        }

        override fun onBeginningOfSpeech() {
            Logger.d("## onBeginningOfSpeech")

        }

        override fun onEndOfSpeech() {
            Logger.d("## onEndOfSpeech")

        }

        override fun onError(error: Int) {
            Logger.d("## onError")

            when (error) {
                SpeechRecognizer.ERROR_AUDIO -> Toast.makeText(this@SttActivity, "ERROR_AUDIO", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_CLIENT -> Toast.makeText(this@SttActivity, "ERROR_CLIENT", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@SttActivity, "ERROR_INSUFFICIENT_PERMISSIONS", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_NETWORK -> Toast.makeText(this@SttActivity, "ERROR_NETWORK", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> Toast.makeText(this@SttActivity, "ERROR_NETWORK_TIMEOUT", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_NO_MATCH -> Toast.makeText(this@SttActivity, "ERROR_NO_MATCH", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> Toast.makeText(this@SttActivity, "ERROR_RECOGNIZER_BUSY", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_SERVER -> Toast.makeText(this@SttActivity, "ERROR_SERVER", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Toast.makeText(this@SttActivity, "ERROR_SPEECH_TIMEOUT", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@SttActivity, "퍼미션 없음", Toast.LENGTH_SHORT).show()
            }
        }


        override fun onResults(results: Bundle) { // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            Logger.d("## onResults")
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches.indices) {

                binding.tvSTT.text = matches[i]
            }
        }


    }


}
