package com.example.service

import android.graphics.Bitmap
import android.util.Log
import com.example.model.ToolCall
import com.example.model.ToolExecutionStatus
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "MyraAiService"

data class MyraAiResult(
  val spokenText: String,
  val toolCall: ToolCall? = null
)

class MyraAiService {

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash"
    )
  }

  private val systemPrompt = """
    # MASTER OPERATIONAL SPECIFICATION: MYRA CORE
    You are MYRA CORE (Multitasking Yielding Robotic Assistant), an autonomous desktop, system, and vision AI operating system assistant. You act as the primary operational neural link between the user, their operating system, files, screen, network, and connected devices.

    VOICE & CONVERSATIONAL PERSONA:
    1. Tone: Sharp, confident, polite, and hyper-efficient operational copilot.
    2. Speech-First Rules: Spoken answers must be concise, direct, and natural (1 to 2 sentences per response). Never output markdown syntax, asterisks, brackets, or code blocks in voice conversations unless explicitly instructed to dictate code. Use natural conversational punctuation.
    3. Multilingual Mastery (English, Hindi, Hinglish): Seamlessly detect and mirror the user's language. When addressed in Hindi or Hinglish, reply with fluent Hinglish (e.g. "Main Chrome open kar rahi hoon aur volume adjust kar diya hai, sir.").
    4. Action-First Principle: When an action is requested, execute the tool immediately. Never say "I can do that for you"—execute and confirm in one clean voice line.

    TOOL EXECUTION PROTOCOL:
    When an action is needed, append an action tag at the very end of your response in one of these formats:
    [TOOL:open_application|app_name=name]
    [TOOL:kill_process|process_name=name]
    [TOOL:hardware_adjust|target=volume_up/volume_down/mute/unmute/lock_screen/brightness|value=val]
    [TOOL:system_telemetry]
    [TOOL:capture_and_inspect_screen|query=inspection_query|target_region=region]
    [TOOL:execute_macro|macro_name=name|steps=step1,step2,step3]
    [TOOL:web_research|query=search_query|target_domains=domains]
    [TOOL:send_communication|platform=sms/whatsapp/email|recipient=target|payload=msg]
    [TOOL:file_manager|operation=list/organize/search|path=filepath]
    [TOOL:clipboard_action|action=read/write|content=text]

    Keep the conversational text before [TOOL:...] concise (1 to 2 sentences) describing the confirmed action.
  """.trimIndent()

  suspend fun processUserPrompt(prompt: String, conversationContext: String = ""): Result<MyraAiResult> {
    return try {
      val fullPrompt = buildString {
        append(systemPrompt)
        append("\n\n")
        if (conversationContext.isNotBlank()) {
          append("Active Session Context:\n")
          append(conversationContext)
          append("\n\n")
        }
        append("User Command: ")
        append(prompt)
        append("\nMYRA CORE Spoken Output:")
      }

      val response = withTimeout(60_000L) {
        model.generateContent(fullPrompt)
      }

      val text = response.text
      if (text.isNullOrBlank()) {
        Result.failure(IllegalStateException("MYRA CORE returned an empty transmission."))
      } else {
        Result.success(parseModelOutput(text))
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "AI processing timed out", e)
      Result.failure(Exception("Neural link timed out after 60 seconds."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Firebase AI generation error", e)
      Result.failure(e)
    }
  }

  suspend fun analyzeScreenImage(bitmap: Bitmap, userQuery: String): Result<MyraAiResult> {
    return try {
      val prompt = """
        $systemPrompt
        The operator has submitted an on-screen capture for visual diagnostic analysis.
        Audit code syntax, locate error modals, identify UI coordinates, or parse text.
        Provide a concise, 1 to 2 sentence diagnosis suitable for voice synthesis.
        Inquiry: ${userQuery.ifBlank { "Audit visual screen elements and locate errors" }}
      """.trimIndent()

      val response = withTimeout(60_000L) {
        model.generateContent(
          content {
            image(bitmap)
            text(prompt)
          }
        )
      }

      val text = response.text
      if (text.isNullOrBlank()) {
        Result.failure(IllegalStateException("Screen vision analysis returned empty."))
      } else {
        Result.success(parseModelOutput(text))
      }
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "Screen vision analysis timed out", e)
      Result.failure(Exception("Screen vision analysis timed out."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "Screen vision analysis error", e)
      Result.failure(e)
    }
  }

  private fun parseModelOutput(rawText: String): MyraAiResult {
    val toolRegex = Regex("\\[TOOL:([a-zA-Z_]+)(.*?)\\]")
    val match = toolRegex.find(rawText)

    if (match != null) {
      val toolName = match.groupValues[1]
      val rawArgs = match.groupValues[2]
      val spoken = rawText.replace(match.value, "").trim()

      val argsMap = mutableMapOf<String, String>()
      val paramPairs = rawArgs.split("|").filter { it.isNotBlank() }
      for (pair in paramPairs) {
        val parts = pair.split("=", limit = 2)
        if (parts.size == 2) {
          argsMap[parts[0].trim()] = parts[1].trim()
        }
      }

      val isDestructive = toolName in listOf("kill_process", "system_shutdown", "reboot", "file_delete")
      val toolCall = ToolCall(
        toolName = toolName,
        arguments = argsMap,
        status = if (isDestructive) ToolExecutionStatus.CONFIRMATION_REQUIRED else ToolExecutionStatus.PENDING,
        isDestructive = isDestructive
      )
      return MyraAiResult(spokenText = spoken, toolCall = toolCall)
    }

    return MyraAiResult(spokenText = rawText.trim(), toolCall = null)
  }
}
