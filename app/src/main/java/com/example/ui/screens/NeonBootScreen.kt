package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.CoreVisualState
import com.example.model.MasterPage
import com.example.ui.components.CyberActionButton
import com.example.ui.components.HyperionQuantumCore
import com.example.ui.components.StatusPill
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.MyraCoreViewModel

@Composable
fun NeonBootScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val progress by viewModel.bootProgress.collectAsState()
  val stepText by viewModel.bootStepText.collectAsState()
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "bootProgress")

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Hyperion Quantum Core Animated Vector
    HyperionQuantumCore(
      state = CoreVisualState.PROCESSING,
      sizeDp = 180,
      modifier = Modifier.testTag("boot_quantum_core")
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "MYRA CORE",
      fontFamily = OrbitronFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      color = HyperCyan,
      letterSpacing = 2.sp
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = "MULTITASKING YIELDING ROBOTIC ASSISTANT",
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = QuantumViolet,
      letterSpacing = 1.sp
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Self-Diagnostic Diagnostic Container
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(SurfaceCard)
        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
        .padding(16.dp)
        .testTag("boot_diagnostic_card")
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SYSTEM INITIALIZATION ENGINE",
            fontFamily = OrbitronFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          StatusPill(
            text = if (progress >= 1f) "ONLINE" else "BOOTING",
            color = if (progress >= 1f) ElectricEmerald else HyperCyan
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .testTag("boot_progress_bar"),
          color = HyperCyan,
          trackColor = BorderSubtle
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "> $stepText",
          fontFamily = RobotoMonoFontFamily,
          fontSize = 11.sp,
          color = HyperCyan
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "DIAGNOSTIC STATUS: OK",
            fontFamily = RobotoMonoFontFamily,
            fontSize = 9.sp,
            color = TextMuted
          )
          Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontFamily = OrbitronFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HyperCyan
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    CyberActionButton(
      text = "ENTER COMMAND MATRIX",
      onClick = { viewModel.navigateTo(MasterPage.COMMAND) },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("btn_enter_command_matrix"),
      color = HyperCyan
    )
  }
}
