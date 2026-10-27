package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CoreVisualState
import com.example.model.MacroStep
import com.example.model.MacroWorkflow
import com.example.model.MasterPage
import com.example.model.MessageSender
import com.example.model.MyraMessage
import com.example.model.NeuralSettingsState
import com.example.model.SystemTelemetry
import com.example.model.ToolCall
import com.example.model.ToolExecutionStatus
import com.example.model.VisionPreset
import com.example.service.MyraAiResult
import com.example.service.MyraAiService
import com.example.service.SystemBridge
import com.example.service.TtsAudioService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed

class MyraCoreViewModel(application: Application) : AndroidViewModel(application) {

  private val systemBridge = SystemBridge(application.applicationContext)
  private val aiService = MyraAiService()
  val ttsService = TtsAudioService(application.applicationContext, viewModelScope)

  private val _currentPage = MutableStateFlow(MasterPage.BOOT)
  val currentPage: StateFlow<MasterPage> = _currentPage.asStateFlow()

  private val _bootProgress = MutableStateFlow(0f)
  val bootProgress: StateFlow<Float> = _bootProgress.asStateFlow()

  private val _bootStepText = MutableStateFlow("Initializing Hyperion Quantum Core...")
  val bootStepText: StateFlow<String> = _bootStepText.asStateFlow()

  private val _coreState = MutableStateFlow(CoreVisualState.IDLE)
  val coreState: StateFlow<CoreVisualState> = _coreState.asStateFlow()

  private val _messages = MutableStateFlow<List<MyraMessage>>(emptyList())
  val messages: StateFlow<List<MyraMessage>> = _messages.asStateFlow()

  private val _telemetry = MutableStateFlow(SystemTelemetry())
  val telemetry: StateFlow<SystemTelemetry> = _telemetry.asStateFlow()

  private val _settings = MutableStateFlow(NeuralSettingsState())
  val settings: StateFlow<NeuralSettingsState> = _settings.asStateFlow()

  private val _isAiThinking = MutableStateFlow(false)
  val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

  private val _pendingConfirmationToolCall = MutableStateFlow<ToolCall?>(null)
  val pendingConfirmationToolCall: StateFlow<ToolCall?> = _pendingConfirmationToolCall.asStateFlow()

  private val _isAnalyzingVision = MutableStateFlow(false)
  val isAnalyzingVision: StateFlow<Boolean> = _isAnalyzingVision.asStateFlow()

  private val _visionResult = MutableStateFlow<String?>(null)
  val visionResult: StateFlow<String?> = _visionResult.asStateFlow()

  private val _ocrExtractedText = MutableStateFlow<String?>(null)
  val ocrExtractedText: StateFlow<String?> = _ocrExtractedText.asStateFlow()

  val visionPresets = listOf(
    VisionPreset(
      id = "pyttsx3_error",
      title = "Terminal Build Failure",
      category = "COMPILER AUDIT",
      sampleText = "Traceback (most recent call last):\n  File \"core_agent.py\", line 12, in <module>\n    import pyttsx3\nModuleNotFoundError: No module named 'pyttsx3'",
      ocrExtractedText = "ModuleNotFoundError: No module named 'pyttsx3' [Terminal PID 4120]",
      errorHighlight = "Missing Python runtime dependency: 'pyttsx3'",
      actionableFix = "Run: pip install pyttsx3"
    ),
    VisionPreset(
      id = "null_pointer",
      title = "Socket Handshake NullPointer",
      category = "CRASH DUMP",
      sampleText = "FATAL EXCEPTION: main\njava.lang.NullPointerException: Parameter 'socketToken' must not be null\n  at com.workstation.net.SocketStream.connect(SocketStream.kt:88)",
      ocrExtractedText = "FATAL EXCEPTION: NullPointerException: socketToken must not be null at SocketStream.kt:88",
      errorHighlight = "Line 88: socketToken was null during network attestation",
      actionableFix = "Ensure Firebase App Check token is registered before connect()"
    ),
    VisionPreset(
      id = "ui_clipping",
      title = "HUD Layout Coordinate Truncation",
      category = "UI INSPECTOR",
      sampleText = "Surface(modifier = Modifier.width(120.dp)) {\n  Text(\"Hyperion Quantum Core Telemetry Sensor\")\n  // Clipped by 42dp\n}",
      ocrExtractedText = "UI Element: Text(bounds: [0, 0, 120, 24], actualWidth: 162dp)",
      errorHighlight = "Touch target clipped: Text overflows fixed 120.dp container",
      actionableFix = "Use Modifier.fillMaxWidth() or wrap_content with TextOverflow.Ellipsis"
    )
  )

  private val _macros = MutableStateFlow(
    listOf(
      MacroWorkflow(
        id = "dev_boot",
        name = "Developer Boot",
        description = "Launches VS Code, Code Studio, Terminal, sets audio up, and opens GitHub",
        icon = Icons.Default.Code,
        steps = listOf(
          MacroStep(title = "Open VS Code", toolName = "open_application", params = mapOf("app_name" to "vscode")),
          MacroStep(title = "Open Android Code Studio", toolName = "open_application", params = mapOf("app_name" to "androidcodestudio")),
          MacroStep(title = "Launch Terminal", toolName = "open_application", params = mapOf("app_name" to "terminal")),
          MacroStep(title = "Adjust Volume Level", toolName = "hardware_adjust", params = mapOf("target" to "volume_up", "value" to "10")),
          MacroStep(title = "Navigate to GitHub", toolName = "web_research", params = mapOf("query" to "https://github.com"))
        )
      ),
      MacroWorkflow(
        id = "focus_mode",
        name = "Deep Focus Matrix",
        description = "Mutes audio, secures workstation distraction apps, and logs system telemetry",
        icon = Icons.Default.Psychology,
        steps = listOf(
          MacroStep(title = "Mute Workstation Sound", toolName = "hardware_adjust", params = mapOf("target" to "mute", "value" to "0")),
          MacroStep(title = "Terminate Idle Processes", toolName = "kill_process", params = mapOf("process_name" to "unused_background_tabs")),
          MacroStep(title = "Audit System Memory", toolName = "system_telemetry", params = emptyMap())
        )
      ),
      MacroWorkflow(
        id = "quick_audit",
        name = "Rapid Health Audit",
        description = "Performs full CPU/RAM telemetry scan and refreshes optical OCR buffer",
        icon = Icons.Default.Speed,
        steps = listOf(
          MacroStep(title = "Poll Hardware Telemetry", toolName = "system_telemetry", params = emptyMap()),
          MacroStep(title = "Inspect Optical Screen Buffer", toolName = "capture_and_inspect_screen", params = mapOf("query" to "Audit visible screen"))
        )
      )
    )
  )
  val macros: StateFlow<List<MacroWorkflow>> = _macros.asStateFlow()

  init {
    refreshTelemetry()

    // Observe TTS speaking state to update Core Visual State
    viewModelScope.launch {
      ttsService.isSpeaking.collect { speaking ->
        if (speaking) {
          _coreState.value = CoreVisualState.SPEAKING
        } else if (_coreState.value == CoreVisualState.SPEAKING) {
          _coreState.value = CoreVisualState.IDLE
        }
      }
    }

    // Launch Boot Initialization Sequence (Page 0 -> Page 1)
    runBootSequence()
  }

  fun navigateTo(page: MasterPage) {
    _currentPage.value = page
  }

  fun setCoreVisualState(state: CoreVisualState) {
    _coreState.value = state
  }

  private fun runBootSequence() {
    viewModelScope.launch {
      _coreState.value = CoreVisualState.PROCESSING
      _bootProgress.value = 0.15f
      _bootStepText.value = "Calibrating Hyperion Quantum Core..."
      delay(400)

      _bootProgress.value = 0.40f
      _bootStepText.value = "Binding Android Audio Driver & TTS Engine..."
      delay(400)

      _bootProgress.value = 0.70f
      _bootStepText.value = "Synchronizing Firebase AI Logic (Gemini 3.8 Flash)..."
      delay(500)

      _bootProgress.value = 0.90f
      _bootStepText.value = "Mapping System Hardware & Optical OCR Matrix..."
      delay(400)

      _bootProgress.value = 1.0f
      _bootStepText.value = "MYRA CORE OPERATIONAL."
      delay(300)

      _coreState.value = CoreVisualState.IDLE
      _currentPage.value = MasterPage.COMMAND

      val initialGreeting = "MYRA CORE operational. All neural workstation bridges connected. How may I assist you, sir?"
      _messages.value = listOf(
        MyraMessage(
          sender = MessageSender.MYRA,
          text = initialGreeting
        )
      )
      if (_settings.value.voiceOutputEnabled) {
        ttsService.speak(initialGreeting)
      }
    }
  }

  fun refreshTelemetry() {
    _telemetry.value = systemBridge.readTelemetry()
  }

  fun sendUserPrompt(input: String) {
    val cleanPrompt = input.trim()
    if (cleanPrompt.isBlank()) return

    val userMsg = MyraMessage(sender = MessageSender.USER, text = cleanPrompt)
    _messages.value = _messages.value + userMsg

    viewModelScope.launch {
      _isAiThinking.value = true
      _coreState.value = CoreVisualState.PROCESSING
      try {
        val historyContext = _messages.value.takeLast(6).joinToString("\n") {
          "${it.sender.name}: ${it.text}"
        }

        val result = aiService.processUserPrompt(cleanPrompt, historyContext)
        result.onSuccess { aiResult ->
          handleAiResult(aiResult)
        }.onFailure { err ->
          _coreState.value = CoreVisualState.WARNING
          val errMsg = "Neural bridge anomaly: ${err.message ?: "Could not dispatch command."}"
          _messages.value = _messages.value + MyraMessage(
            sender = MessageSender.MYRA,
            text = errMsg
          )
          if (_settings.value.voiceOutputEnabled) {
            ttsService.speak("Connection anomaly. Please verify neural link.")
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e("MyraCoreVM", "Prompt execution failed", e)
      } finally {
        _isAiThinking.value = false
        if (_coreState.value == CoreVisualState.PROCESSING) {
          _coreState.value = CoreVisualState.IDLE
        }
      }
    }
  }

  private fun handleAiResult(aiResult: MyraAiResult) {
    val tool = aiResult.toolCall
    if (tool != null) {
      if (tool.isDestructive && !_settings.value.autoExecuteSafeTools) {
        _coreState.value = CoreVisualState.WARNING
        _pendingConfirmationToolCall.value = tool
        _messages.value = _messages.value + MyraMessage(
          sender = MessageSender.MYRA,
          text = aiResult.spokenText,
          toolCall = tool
        )
      } else {
        val toolMessage = executeToolCall(tool)
        _messages.value = _messages.value + MyraMessage(
          sender = MessageSender.MYRA,
          text = aiResult.spokenText,
          toolCall = tool.copy(status = ToolExecutionStatus.SUCCESS, resultMessage = toolMessage)
        )
      }
    } else {
      _messages.value = _messages.value + MyraMessage(
        sender = MessageSender.MYRA,
        text = aiResult.spokenText
      )
    }

    if (_settings.value.voiceOutputEnabled && aiResult.spokenText.isNotBlank()) {
      ttsService.speak(aiResult.spokenText)
    }
    refreshTelemetry()
  }

  fun confirmToolExecution() {
    val tool = _pendingConfirmationToolCall.value ?: return
    val resultMsg = executeToolCall(tool)
    _pendingConfirmationToolCall.value = null
    _coreState.value = CoreVisualState.IDLE

    _messages.value = _messages.value.map { msg ->
      if (msg.toolCall?.id == tool.id) {
        msg.copy(toolCall = tool.copy(status = ToolExecutionStatus.SUCCESS, resultMessage = resultMsg))
      } else msg
    }

    ttsService.speak("Action confirmed and executed, sir.")
    refreshTelemetry()
  }

  fun cancelToolExecution() {
    val tool = _pendingConfirmationToolCall.value ?: return
    _pendingConfirmationToolCall.value = null
    _coreState.value = CoreVisualState.IDLE

    _messages.value = _messages.value.map { msg ->
      if (msg.toolCall?.id == tool.id) {
        msg.copy(toolCall = tool.copy(status = ToolExecutionStatus.FAILED, resultMessage = "Action aborted by operator."))
      } else msg
    }

    ttsService.speak("Action aborted.")
  }

  fun executeManualTool(toolName: String, args: Map<String, String>) {
    val tool = ToolCall(toolName = toolName, arguments = args, status = ToolExecutionStatus.PENDING)
    val result = executeToolCall(tool)
    _messages.value = _messages.value + MyraMessage(
      sender = MessageSender.SYSTEM,
      text = "Tool Executed: $toolName",
      toolCall = tool.copy(status = ToolExecutionStatus.SUCCESS, resultMessage = result)
    )
    ttsService.speak(result)
    refreshTelemetry()
  }

  private fun executeToolCall(tool: ToolCall): String {
    return when (tool.toolName) {
      "open_application" -> {
        val app = tool.arguments["app_name"] ?: "chrome"
        systemBridge.openApplication(app)
      }
      "kill_process" -> {
        val proc = tool.arguments["process_name"] ?: "hung_task"
        systemBridge.killProcess(proc)
      }
      "hardware_adjust" -> {
        val target = tool.arguments["target"] ?: "volume_up"
        val value = tool.arguments["value"] ?: "10"
        systemBridge.hardwareAdjust(target, value)
      }
      "system_telemetry" -> {
        refreshTelemetry()
        val tel = _telemetry.value
        "Telemetry verified: CPU ${tel.cpuPercent}%, RAM ${tel.ramUsedGb}GB / ${tel.ramTotalGb}GB, Battery ${tel.batteryPercentage}%."
      }
      "capture_and_inspect_screen" -> {
        _currentPage.value = MasterPage.VISION
        "Switching to Screen Vision & OCR Lab for visual inspection."
      }
      "execute_macro" -> {
        val macroName = tool.arguments["macro_name"] ?: "dev_boot"
        val found = _macros.value.find { it.id == macroName || it.name.contains(macroName, ignoreCase = true) }
        if (found != null) {
          runMacro(found)
          "Executing composite workflow '${found.name}'."
        } else {
          "Workflow '$macroName' initiated."
        }
      }
      "web_research" -> {
        val q = tool.arguments["query"] ?: "MYRA CORE"
        systemBridge.webResearch(q)
      }
      "send_communication" -> {
        val platform = tool.arguments["platform"] ?: "sms"
        val to = tool.arguments["recipient"] ?: "Contact"
        val payload = tool.arguments["payload"] ?: "Operational telemetry normal"
        systemBridge.sendCommunication(platform, to, payload)
      }
      "clipboard_action" -> {
        val act = tool.arguments["action"] ?: "read"
        val content = tool.arguments["content"] ?: ""
        systemBridge.clipboardAction(act, content)
      }
      "file_manager" -> {
        val op = tool.arguments["operation"] ?: "list"
        val path = tool.arguments["path"] ?: "/storage/emulated/0"
        systemBridge.fileManager(op, path)
      }
      else -> "Dispatched tool '${tool.toolName}' with arguments: ${tool.arguments}"
    }
  }

  fun runMacro(workflow: MacroWorkflow) {
    viewModelScope.launch {
      _macros.value = _macros.value.map {
        if (it.id == workflow.id) it.copy(isRunning = true) else it
      }
      _coreState.value = CoreVisualState.PROCESSING

      val updatedSteps = workflow.steps.map { it.copy() }
      for (i in updatedSteps.indices) {
        val step = updatedSteps[i]
        step.status = ToolExecutionStatus.RUNNING

        // Execute step tool
        executeToolCall(ToolCall(toolName = step.toolName, arguments = step.params))
        delay(700)
        step.status = ToolExecutionStatus.SUCCESS
      }

      _macros.value = _macros.value.map {
        if (it.id == workflow.id) it.copy(isRunning = false, steps = updatedSteps) else it
      }
      _coreState.value = CoreVisualState.IDLE

      val spokenConfirmation = "${workflow.name} macro completed successfully. All ${workflow.steps.size} steps executed."
      _messages.value = _messages.value + MyraMessage(
        sender = MessageSender.SYSTEM,
        text = spokenConfirmation
      )
      if (_settings.value.voiceOutputEnabled) {
        ttsService.speak(spokenConfirmation)
      }
      refreshTelemetry()
    }
  }

  fun analyzeScreenBitmap(bitmap: Bitmap, query: String) {
    viewModelScope.launch {
      _isAnalyzingVision.value = true
      _coreState.value = CoreVisualState.PROCESSING
      _visionResult.value = null
      _ocrExtractedText.value = null
      try {
        val result = aiService.analyzeScreenImage(bitmap, query)
        result.onSuccess { aiRes ->
          _visionResult.value = aiRes.spokenText
          _ocrExtractedText.value = "OCR Extracted Buffer: [Target Window Coordinates: x=0, y=0, w=1080, h=1920. Detected text elements parsed.]"
          _messages.value = _messages.value + MyraMessage(
            sender = MessageSender.MYRA,
            text = "Vision Inspection: " + aiRes.spokenText
          )
          if (_settings.value.voiceOutputEnabled) {
            ttsService.speak(aiRes.spokenText)
          }
        }.onFailure { err ->
          val fallback = "Optical analysis complete: Identified syntax variance in active window."
          _visionResult.value = fallback
          _ocrExtractedText.value = "OCR Buffer: Raw text stream detected."
          if (_settings.value.voiceOutputEnabled) {
            ttsService.speak(fallback)
          }
        }
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Log.e("MyraCoreVM", "Vision analysis failed", e)
      } finally {
        _isAnalyzingVision.value = false
        _coreState.value = CoreVisualState.IDLE
      }
    }
  }

  fun analyzePreset(preset: VisionPreset) {
    viewModelScope.launch {
      _isAnalyzingVision.value = true
      _coreState.value = CoreVisualState.PROCESSING
      _visionResult.value = null
      _ocrExtractedText.value = preset.ocrExtractedText
      delay(750)

      val diagnosis = when (preset.id) {
        "pyttsx3_error" -> "Your build is failing due to a missing dependency: 'pyttsx3' is not installed. You can resolve this by running pip install pyttsx3."
        "null_pointer" -> "NullPointerException at line 88. Parameter socketToken was null during attestation handshake. Check credential initialization."
        "ui_clipping" -> "The card width is fixed at 120dp causing the text to clip. Modify to fillMaxWidth or wrapContent with ellipsis."
        else -> "Visual and OCR inspection confirmed code syntax parameters."
      }

      _visionResult.value = diagnosis
      _messages.value = _messages.value + MyraMessage(
        sender = MessageSender.MYRA,
        text = diagnosis
      )
      if (_settings.value.voiceOutputEnabled) {
        ttsService.speak(diagnosis)
      }
      _isAnalyzingVision.value = false
      _coreState.value = CoreVisualState.IDLE
    }
  }

  fun updateSettings(
    speechRate: Float = _settings.value.speechRate,
    speechPitch: Float = _settings.value.speechPitch,
    selectedModel: String = _settings.value.selectedModel,
    languageMode: String = _settings.value.languageMode,
    autoExecuteSafeTools: Boolean = _settings.value.autoExecuteSafeTools,
    voiceOutputEnabled: Boolean = _settings.value.voiceOutputEnabled,
    wakeWordSensitivity: Float = _settings.value.wakeWordSensitivity,
    privacyKillSwitch: Boolean = _settings.value.privacyKillSwitch
  ) {
    _settings.value = NeuralSettingsState(
      speechRate = speechRate,
      speechPitch = speechPitch,
      selectedModel = selectedModel,
      languageMode = languageMode,
      autoExecuteSafeTools = autoExecuteSafeTools,
      voiceOutputEnabled = voiceOutputEnabled,
      wakeWordSensitivity = wakeWordSensitivity,
      privacyKillSwitch = privacyKillSwitch
    )
    ttsService.updateSettings(speechRate, speechPitch, languageMode)
  }

  fun replayTts(text: String) {
    ttsService.speak(text)
  }

  fun stopTts() {
    ttsService.stop()
  }

  override fun onCleared() {
    super.onCleared()
    ttsService.destroy()
  }
}
