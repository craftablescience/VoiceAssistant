package me.belewis.voiceassistant

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var textView: TextView
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        tts = TextToSpeech(this, this)
    }

    fun getCommand(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        startActivityForResult(intent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    textView.text = list!![0]
                    speakRecordedText()
                }
            }
        }
    }

    // Text-to-speech init
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.ENGLISH
        } else {
            Toast.makeText(this, "TTS engine failed to initialize", Toast.LENGTH_LONG).show()
        }
    }

    private fun speakRecordedText() {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(s: String) {
                runOnUiThread {Toast.makeText(applicationContext, "Started $s", Toast.LENGTH_LONG).show()}
            }
            override fun onDone(s: String) {
                runOnUiThread {Toast.makeText(applicationContext, "Done ", Toast.LENGTH_SHORT).show()}
            }
            override fun onError(s: String) {
                runOnUiThread {Toast.makeText(applicationContext, "Error ", Toast.LENGTH_SHORT).show()}
            }
        })
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        val text: String = textView.text as String
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "utteranceID")
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}