package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

enum class CoreVisualState {
  IDLE,
  LISTENING,
  PROCESSING,
  WARNING,
  SPEAKING
}

enum class MessageSender {
  USER,
  MYRA,
  SYSTEM
}

enum class ToolExecutionStatus {
  PENDING,
  RUNNING,
  SUCCESS,
  FAILED,
  CONFIRMATION_REQUIRED
}

data class ToolCall(
  val id: String = UUID.randomUUID().toString(),
  val toolName: String,
  val arguments: Map<String, String>,
  val status: ToolExecutionStatus = ToolExecutionStatus.PENDING,
  val resultMessage: String? = null,
  val isDestructive: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

data class MyraMessage(
  val id: String = UUID.randomUUID().toString(),
  val sender: MessageSender,
  val text: String,
  val toolCall: ToolCall? = null,
  val imageUri: String? = null,
  val timestamp: Long = System.currentTimeMillis()
)

data class SystemTelemetry(
  val cpuPercent: Int = 18,
  val ramUsedGb: Float = 4.2f,
  val ramTotalGb: Float = 8.0f,
  val batteryPercentage: Int = 88,
  val isCharging: Boolean = false,
  val storageFreeGb: Float = 45.2f,
  val storageTotalGb: Float = 128.0f,
  val volumePercent: Int = 65,
  val brightnessPercent: Int = 70,
  val networkLatencyMs: Int = 24,
  val isMuted: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

enum class MasterPage(
  val pageIndex: Int,
  val title: String,
  val code: String,
  val icon: ImageVector,
  val subtitle: String
) {
  BOOT(0, "BOOT", "SYS_BOOT_00", Icons.Default.Power, "Neon Boot & Diagnostics"),
  COMMAND(1, "COMMAND", "CMD_MTX_01", Icons.Default.Terminal, "Command Matrix"),
  VISION(2, "VISION", "CV_OCR_02", Icons.Default.Visibility, "Screen Vision & OCR Lab"),
  AUTOMATION(3, "CONTROL", "HW_MTX_03", Icons.Default.DisplaySettings, "System Automation Hub"),
  MACROS(4, "MACROS", "AUTO_WKF_04", Icons.Default.AccountTree, "Autonomous Workflow Studio"),
  SETTINGS(5, "NEURAL", "CORE_CFG_05", Icons.Default.Settings, "Neural Engine & Settings")
}

data class MacroStep(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val toolName: String,
  val params: Map<String, String>,
  var status: ToolExecutionStatus = ToolExecutionStatus.PENDING
)

data class MacroWorkflow(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val description: String,
  val icon: ImageVector,
  val steps: List<MacroStep>,
  val isRunning: Boolean = false
)

data class VisionPreset(
  val id: String,
  val title: String,
  val category: String,
  val sampleText: String,
  val ocrExtractedText: String,
  val errorHighlight: String,
  val actionableFix: String
)

data class NeuralSettingsState(
  val speechRate: Float = 1.05f,
  val speechPitch: Float = 1.0f,
  val selectedModel: String = "gemini-3.8-flash",
  val languageMode: String = "Auto (English / Hindi / Hinglish)",
  val autoExecuteSafeTools: Boolean = true,
  val voiceOutputEnabled: Boolean = true,
  val wakeWordSensitivity: Float = 0.8f,
  val privacyKillSwitch: Boolean = false
)
