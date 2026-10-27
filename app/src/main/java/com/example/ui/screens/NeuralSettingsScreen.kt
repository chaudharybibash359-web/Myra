package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CharcoalSlate
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MyraCoreViewModel

@Composable
fun NeuralSettingsScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val settings by viewModel.settings.collectAsState()

  val languages = listOf(
    "Auto (English / Hindi / Hinglish)",
    "English (US Dedicated)",
    "Hindi (Dedicated hi-IN)"
  )

  val models = listOf(
    "gemini-3.8-flash" to "Gemini 3.8 Flash (Frontier Speed & Multimodal)",
    "gemini-4-argon" to "Gemini 4 Argon (Advanced STEM & OS Reasoning)"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    CyberHeader(
      title = "NEURAL ENGINE & CORE CONFIG",
      code = "CORE_CFG_05"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Model Selector Card
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("model_selector_card")
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "INTELLIGENCE ENGINE MODEL", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HyperCyan)
        }

        Spacer(modifier = Modifier.height(8.dp))

        models.forEach { (modelId, desc) ->
          val isSelected = settings.selectedModel == modelId
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) CharcoalSlate else DeepObsidian)
              .border(1.dp, if (isSelected) HyperCyan else BorderSubtle, RoundedCornerShape(8.dp))
              .clickable { viewModel.updateSettings(selectedModel = modelId) }
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = modelId, fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) HyperCyan else TextPrimary)
                Text(text = desc, fontFamily = RobotoMonoFontFamily, fontSize = 9.sp, color = TextMuted)
              }
              if (isSelected) {
                StatusPill(text = "ACTIVE", color = HyperCyan)
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. TTS Voice Parameters
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("tts_voice_card")
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = QuantumViolet, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "TTS VOICE CADENCE", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumViolet)
          }

          Switch(
            checked = settings.voiceOutputEnabled,
            onCheckedChange = { viewModel.updateSettings(voiceOutputEnabled = it) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = DeepObsidian,
              checkedTrackColor = QuantumViolet,
              uncheckedThumbColor = TextMuted,
              uncheckedTrackColor = CharcoalSlate
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "SPEECH RATE: ${"%.2f".format(settings.speechRate)}x", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = TextMuted)
        Slider(
          value = settings.speechRate,
          onValueChange = { viewModel.updateSettings(speechRate = it) },
          valueRange = 0.7f..1.5f,
          colors = SliderDefaults.colors(thumbColor = QuantumViolet, activeTrackColor = QuantumViolet, inactiveTrackColor = BorderSubtle)
        )

        Text(text = "SPEECH PITCH: ${"%.2f".format(settings.speechPitch)}x", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = TextMuted)
        Slider(
          value = settings.speechPitch,
          onValueChange = { viewModel.updateSettings(speechPitch = it) },
          valueRange = 0.75f..1.35f,
          colors = SliderDefaults.colors(thumbColor = HyperCyan, activeTrackColor = HyperCyan, inactiveTrackColor = BorderSubtle)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Wake-Word Sensitivity & Multilingual
    CyberCard(
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Hearing, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "WAKE-WORD SENSITIVITY THRESHOLD", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HyperCyan)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "THRESHOLD: ${(settings.wakeWordSensitivity * 100).toInt()}%", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = TextMuted)

        Slider(
          value = settings.wakeWordSensitivity,
          onValueChange = { viewModel.updateSettings(wakeWordSensitivity = it) },
          valueRange = 0.3f..1.0f,
          colors = SliderDefaults.colors(thumbColor = HyperCyan, activeTrackColor = HyperCyan, inactiveTrackColor = BorderSubtle)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = QuantumViolet, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "MULTILINGUAL AGILITY", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumViolet)
        }

        Spacer(modifier = Modifier.height(8.dp))

        languages.forEach { lang ->
          val isSelected = settings.languageMode == lang
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) CharcoalSlate else DeepObsidian)
              .border(1.dp, if (isSelected) QuantumViolet else BorderSubtle, RoundedCornerShape(8.dp))
              .clickable { viewModel.updateSettings(languageMode = lang) }
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = lang, fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = if (isSelected) TextPrimary else TextMuted)
              if (isSelected) StatusPill(text = "ACTIVE", color = QuantumViolet)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. Privacy Kill-Switch & Guardrails
    CyberCard(
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = AlertAmber, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "PRIVACY KILL-SWITCH", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AlertAmber)
          }

          Switch(
            checked = settings.privacyKillSwitch,
            onCheckedChange = { viewModel.updateSettings(privacyKillSwitch = it) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = DeepObsidian,
              checkedTrackColor = AlertAmber,
              uncheckedThumbColor = TextMuted,
              uncheckedTrackColor = CharcoalSlate
            )
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (settings.privacyKillSwitch) {
            "PRIVACY SHIELD ACTIVE: Optical frame captures and audio telemetry quarantined."
          } else {
            "STANDBY: Optical vision and audio pipelines ready for operator commands."
          },
          fontFamily = RobotoMonoFontFamily,
          fontSize = 10.sp,
          color = if (settings.privacyKillSwitch) AlertAmber else TextMuted
        )
      }
    }
  }
}
