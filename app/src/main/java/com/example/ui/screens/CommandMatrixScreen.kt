package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CoreVisualState
import com.example.model.MessageSender
import com.example.model.MyraMessage
import com.example.model.ToolExecutionStatus
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.CyberCard
import com.example.ui.components.HyperionQuantumCore
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CharcoalSlate
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
import com.example.ui.theme.TextViolet
import com.example.viewmodel.MyraCoreViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommandMatrixScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsState()
  val telemetry by viewModel.telemetry.collectAsState()
  val isThinking by viewModel.isAiThinking.collectAsState()
  val coreState by viewModel.coreState.collectAsState()
  val isSpeaking by viewModel.ttsService.isSpeaking.collectAsState()
  val amplitudes by viewModel.ttsService.amplitudes.collectAsState()

  var inputText by remember { mutableStateOf("") }
  var isMicListening by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickPrompts = listOf(
    "Myra, look at my terminal screen. Why is my build failing?",
    "Computer bohot slow chal raha hai, check karo kya scene hai.",
    "Start developer mode.",
    "Mute audio and lock screen",
    "Search quantum computing"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    // 1. Central Dynamic Hyperion Quantum Core State Indicator & Waveforms
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("command_matrix_core_card"),
      borderColor = when (coreState) {
        CoreVisualState.LISTENING -> QuantumViolet
        CoreVisualState.PROCESSING -> HyperCyan
        CoreVisualState.WARNING -> AlertAmber
        CoreVisualState.SPEAKING -> HyperCyan
        CoreVisualState.IDLE -> BorderSubtle
      }
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                  when (coreState) {
                    CoreVisualState.LISTENING -> QuantumViolet
                    CoreVisualState.PROCESSING -> HyperCyan
                    CoreVisualState.WARNING -> AlertAmber
                    CoreVisualState.SPEAKING -> ElectricEmerald
                    CoreVisualState.IDLE -> HyperCyan
                  }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "HYPERION QUANTUM CORE [${coreState.name}]",
              fontFamily = OrbitronFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = when (coreState) {
                CoreVisualState.LISTENING -> QuantumViolet
                CoreVisualState.PROCESSING -> HyperCyan
                CoreVisualState.WARNING -> AlertAmber
                else -> TextPrimary
              },
              letterSpacing = 1.sp
            )
          }

          if (isSpeaking) {
            IconButton(
              onClick = { viewModel.stopTts() },
              modifier = Modifier
                .size(28.dp)
                .testTag("btn_stop_tts")
            ) {
              Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Stop",
                tint = AlertAmber,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          // Proprietary Vector Rendered Core
          HyperionQuantumCore(
            state = coreState,
            sizeDp = 100
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dynamic Audio Waveform Visualizer
        AudioWaveformVisualizer(
          amplitudes = amplitudes,
          isSpeaking = isSpeaking
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Quick Action Bar
    FlowRow(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      quickPrompts.take(4).forEach { prompt ->
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CharcoalSlate)
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .clickable { viewModel.sendUserPrompt(prompt) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("quick_action_${prompt.take(10)}")
        ) {
          Text(
            text = prompt,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 11.sp,
            color = TextCyan
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 3. Live Console Feed / Chat
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .testTag("live_console_feed"),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        CommandMatrixMessageItem(
          message = msg,
          onReplayTts = { viewModel.replayTts(msg.text) }
        )
      }

      if (isThinking) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = HyperCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "MYRA CORE computing autonomous action...",
              fontFamily = RobotoMonoFontFamily,
              fontSize = 11.sp,
              color = HyperCyan
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 4. Input Row & Voice Mic
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = {
          isMicListening = !isMicListening
          if (isMicListening) {
            viewModel.setCoreVisualState(CoreVisualState.LISTENING)
            viewModel.sendUserPrompt("Start developer mode.")
            isMicListening = false
          }
        },
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(if (isMicListening) QuantumViolet else CharcoalSlate)
          .border(1.dp, if (isMicListening) QuantumViolet else HyperCyan, CircleShape)
          .testTag("matrix_mic_button")
      ) {
        Icon(
          imageVector = if (isMicListening) Icons.Default.MicOff else Icons.Default.Mic,
          contentDescription = "Mic",
          tint = if (isMicListening) Color.White else HyperCyan,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        modifier = Modifier
          .weight(1f)
          .testTag("matrix_input_field"),
        placeholder = {
          Text(
            text = "Command workstation (/tool, /macro)...",
            fontFamily = RobotoMonoFontFamily,
            fontSize = 12.sp,
            color = TextMuted
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = CharcoalSlate,
          unfocusedContainerColor = CharcoalSlate,
          focusedBorderColor = HyperCyan,
          unfocusedBorderColor = BorderSubtle,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          cursorColor = HyperCyan
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = {
          if (inputText.isNotBlank()) {
            viewModel.sendUserPrompt(inputText)
            inputText = ""
          }
        })
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = {
          if (inputText.isNotBlank()) {
            viewModel.sendUserPrompt(inputText)
            inputText = ""
          }
        },
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(HyperCyan)
          .testTag("matrix_send_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = DeepObsidian,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // 5. Dynamic Hardware Telemetry Footer (CPU, RAM, Battery, Latency)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(CharcoalSlate)
        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("hardware_telemetry_footer"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "CPU: ${telemetry.cpuPercent}%",
        fontFamily = RobotoMonoFontFamily,
        fontSize = 10.sp,
        color = HyperCyan
      )
      Text(
        text = "RAM: ${telemetry.ramUsedGb}/${telemetry.ramTotalGb}GB",
        fontFamily = RobotoMonoFontFamily,
        fontSize = 10.sp,
        color = TextPrimary
      )
      Text(
        text = "BATT: ${telemetry.batteryPercentage}%",
        fontFamily = RobotoMonoFontFamily,
        fontSize = 10.sp,
        color = if (telemetry.batteryPercentage > 20) ElectricEmerald else AlertAmber
      )
      Text(
        text = "NET: ${telemetry.networkLatencyMs}ms",
        fontFamily = RobotoMonoFontFamily,
        fontSize = 10.sp,
        color = QuantumViolet
      )
    }
  }
}

@Composable
fun CommandMatrixMessageItem(
  message: MyraMessage,
  onReplayTts: () -> Unit
) {
  val isUser = message.sender == MessageSender.USER
  val isSystem = message.sender == MessageSender.SYSTEM

  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Text(
      text = when (message.sender) {
        MessageSender.USER -> "OPERATOR"
        MessageSender.MYRA -> "MYRA CORE"
        MessageSender.SYSTEM -> "SYSTEM BRIDGE"
      },
      fontFamily = OrbitronFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 10.sp,
      color = when (message.sender) {
        MessageSender.USER -> QuantumViolet
        MessageSender.MYRA -> HyperCyan
        MessageSender.SYSTEM -> AlertAmber
      },
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )

    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .background(if (isUser) CharcoalSlate else SurfaceCard)
        .border(
          1.dp,
          when (message.sender) {
            MessageSender.USER -> QuantumViolet.copy(alpha = 0.4f)
            MessageSender.MYRA -> HyperCyan.copy(alpha = 0.4f)
            MessageSender.SYSTEM -> AlertAmber.copy(alpha = 0.4f)
          },
          RoundedCornerShape(10.dp)
        )
        .padding(10.dp)
    ) {
      Column {
        Text(
          text = message.text,
          fontFamily = RobotoMonoFontFamily,
          fontSize = 12.sp,
          color = TextPrimary,
          lineHeight = 17.sp
        )

        // Tool Action Details
        if (message.toolCall != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(DeepObsidian)
              .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "TOOL: ${message.toolCall.toolName}",
                  fontFamily = OrbitronFontFamily,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = HyperCyan
                )
                StatusPill(
                  text = message.toolCall.status.name,
                  color = when (message.toolCall.status) {
                    ToolExecutionStatus.SUCCESS -> ElectricEmerald
                    ToolExecutionStatus.RUNNING -> HyperCyan
                    ToolExecutionStatus.CONFIRMATION_REQUIRED -> AlertAmber
                    else -> AlertAmber
                  }
                )
              }
              if (message.toolCall.arguments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = message.toolCall.arguments.entries.joinToString(", ") { "${it.key}: ${it.value}" },
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
              if (!message.toolCall.resultMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = message.toolCall.resultMessage,
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 10.sp,
                  color = ElectricEmerald
                )
              }
            }
          }
        }

        if (message.sender == MessageSender.MYRA && message.text.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier
              .clickable(onClick = onReplayTts)
              .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Speak",
              tint = HyperCyan,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "VOCALIZE",
              fontFamily = RobotoMonoFontFamily,
              fontSize = 9.sp,
              color = HyperCyan
            )
          }
        }
      }
    }
  }
}
