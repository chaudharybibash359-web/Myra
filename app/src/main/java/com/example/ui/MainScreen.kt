package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.example.model.MasterPage
import com.example.ui.components.CyberActionButton
import com.example.ui.components.StatusPill
import com.example.ui.screens.CommandMatrixScreen
import com.example.ui.screens.MacroStudioScreen
import com.example.ui.screens.NeonBootScreen
import com.example.ui.screens.NeuralSettingsScreen
import com.example.ui.screens.ScreenVisionLabScreen
import com.example.ui.screens.SystemAutomationHubScreen
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MyraCoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MyraCoreViewModel) {
  val currentPage by viewModel.currentPage.collectAsState()
  val telemetry by viewModel.telemetry.collectAsState()
  val pendingTool by viewModel.pendingConfirmationToolCall.collectAsState()

  // If on Page 0 (Boot), show full-screen initialization
  if (currentPage == MasterPage.BOOT) {
    NeonBootScreen(viewModel = viewModel)
    return
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DeepObsidian),
    topBar = {
      // Top Cyber HUD Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(DeepObsidian)
          .windowInsetsPadding(WindowInsets.statusBars)
          .border(1.dp, BorderSubtle)
          .padding(horizontal = 14.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(HyperCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "MYRA CORE",
              fontFamily = OrbitronFontFamily,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = HyperCyan,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "[${currentPage.code}]",
              fontFamily = RobotoMonoFontFamily,
              fontSize = 10.sp,
              color = QuantumViolet
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(
              text = "BATT ${telemetry.batteryPercentage}%",
              color = if (telemetry.batteryPercentage > 20) ElectricEmerald else AlertAmber
            )
            Spacer(modifier = Modifier.width(6.dp))
            StatusPill(
              text = "CPU ${telemetry.cpuPercent}%",
              color = HyperCyan
            )
          }
        }
      }
    },
    bottomBar = {
      // Bottom Navigation Bar with mandatory WindowInsets.navigationBars handling
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DeepObsidian)
          .border(1.dp, BorderSubtle)
          .windowInsetsPadding(WindowInsets.navigationBars)
          .padding(horizontal = 4.dp, vertical = 6.dp)
          .testTag("hyperion_bottom_nav"),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Operational pages: COMMAND, VISION, AUTOMATION, MACROS, SETTINGS
        val navPages = listOf(
          MasterPage.COMMAND,
          MasterPage.VISION,
          MasterPage.AUTOMATION,
          MasterPage.MACROS,
          MasterPage.SETTINGS
        )

        navPages.forEach { page ->
          val isSelected = currentPage == page
          Column(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) SurfaceCard else DeepObsidian)
              .clickable { viewModel.navigateTo(page) }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("nav_${page.name.lowercase()}"),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = page.icon,
              contentDescription = page.title,
              tint = if (isSelected) HyperCyan else TextMuted,
              modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = page.title,
              fontFamily = OrbitronFontFamily,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 8.sp,
              color = if (isSelected) HyperCyan else TextMuted
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentPage) {
        MasterPage.BOOT -> NeonBootScreen(viewModel = viewModel)
        MasterPage.COMMAND -> CommandMatrixScreen(viewModel = viewModel)
        MasterPage.VISION -> ScreenVisionLabScreen(viewModel = viewModel)
        MasterPage.AUTOMATION -> SystemAutomationHubScreen(viewModel = viewModel)
        MasterPage.MACROS -> MacroStudioScreen(viewModel = viewModel)
        MasterPage.SETTINGS -> NeuralSettingsScreen(viewModel = viewModel)
      }

      // Confirmation Modal for Destructive Tools
      if (pendingTool != null) {
        BasicAlertDialog(
          onDismissRequest = { viewModel.cancelToolExecution() }
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceCard)
              .border(1.dp, AlertAmber, RoundedCornerShape(12.dp))
              .padding(18.dp)
              .testTag("confirmation_modal")
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(AlertAmber)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "ACTION CONFIRMATION REQUIRED",
                  fontFamily = OrbitronFontFamily,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = AlertAmber
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "MYRA CORE requires confirmation for sensitive action:",
                fontFamily = RobotoMonoFontFamily,
                fontSize = 11.sp,
                color = TextSecondary
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "TOOL: ${pendingTool?.toolName?.uppercase()}",
                fontFamily = OrbitronFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = HyperCyan
              )

              if (!pendingTool?.arguments.isNullOrEmpty()) {
                Text(
                  text = "Params: ${pendingTool?.arguments}",
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                CyberActionButton(
                  text = "ABORT",
                  onClick = { viewModel.cancelToolExecution() },
                  color = CharcoalSlate,
                  textColor = TextMuted,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("btn_abort_action")
                )
                CyberActionButton(
                  text = "EXECUTE",
                  onClick = { viewModel.confirmToolExecution() },
                  color = AlertAmber,
                  textColor = DeepObsidian,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("btn_confirm_action")
                )
              }
            }
          }
        }
      }
    }
  }
}
