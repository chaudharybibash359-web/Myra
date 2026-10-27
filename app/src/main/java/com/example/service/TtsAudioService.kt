package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class TtsAudioService(
  private val context: Context,
  private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _amplitudes = MutableStateFlow(List(16) { 0.15f })
  val amplitudes: StateFlow<List<Float>> = _amplitudes.asStateFlow()

  private var waveformJob: Job? = null

  init {
    tts = TextToSpeech(context, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isInitialized = true
      tts?.language = Locale.US
      tts?.setPitch(1.0f)
      tts?.setSpeechRate(1.05f)

      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isSpeaking.value = true
          startWaveformSimulation()
        }

        override fun onDone(utteranceId: String?) {
          _isSpeaking.value = false
          stopWaveformSimulation()
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
          _isSpeaking.value = false
          stopWaveformSimulation()
        }
      })
    } else {
      Log.e("TtsAudioService", "TTS Initialization failed")
    }
  }

  fun updateSettings(speechRate: Float, speechPitch: Float, languageMode: String) {
    tts?.setSpeechRate(speechRate.coerceIn(0.5f, 2.0f))
    tts?.setPitch(speechPitch.coerceIn(0.5f, 2.0f))

    if (languageMode.contains("Hindi", ignoreCase = true)) {
      val hindi = Locale("hi", "IN")
      if (tts?.isLanguageAvailable(hindi) ?: TextToSpeech.LANG_NOT_SUPPORTED >= TextToSpeech.LANG_AVAILABLE) {
        tts?.language = hindi
      }
    } else {
      tts?.language = Locale.US
    }
  }

  fun speak(rawText: String) {
    if (!isInitialized) return

    val cleanSpokenText = sanitizeForVoice(rawText)
    if (cleanSpokenText.isBlank()) return

    // Auto-detect Hindi characters
    val hasHindiScript = cleanSpokenText.any { it.code in 0x0900..0x097F }
    if (hasHindiScript) {
      tts?.language = Locale("hi", "IN")
    }

    tts?.speak(
      cleanSpokenText,
      TextToSpeech.QUEUE_FLUSH,
      null,
      "MYRA_TTS_${System.currentTimeMillis()}"
    )
  }

  fun stop() {
    tts?.stop()
    _isSpeaking.value = false
    stopWaveformSimulation()
  }

  fun destroy() {
    stop()
    tts?.shutdown()
    tts = null
  }

  private fun sanitizeForVoice(input: String): String {
    // Strip markdown code blocks, backticks, asterisks, brackets, JSON, tags
    return input
      .replace(Regex("```[\\s\\S]*?```"), "")
      .replace(Regex("`.*?`"), "")
      .replace(Regex("\\*\\*|\\*"), "")
      .replace(Regex("\\[(.*?)\\]\\(.*\\)"), "$1")
      .replace(Regex("#+\\s+"), "")
      .replace(Regex("(?m)^[-*+]\\s+"), "")
      .replace(Regex("\\{.*?\\}"), "")
      .replace(Regex("<.*?>"), "")
      .replace(Regex("[\\n\\r]+"), " ")
      .trim()
  }

  private fun startWaveformSimulation() {
    waveformJob?.cancel()
    waveformJob = scope.launch(Dispatchers.Default) {
      while (isActive && _isSpeaking.value) {
        val newAmps = List(16) {
          Random.nextFloat().coerceIn(0.2f, 1.0f)
        }
        _amplitudes.value = newAmps
        delay(60)
      }
      _amplitudes.value = List(16) { 0.15f }
    }
  }

  private fun stopWaveformSimulation() {
    waveformJob?.cancel()
    waveformJob = null
    _amplitudes.value = List(16) { 0.15f }
  }
}
