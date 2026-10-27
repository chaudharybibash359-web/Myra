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
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberActionButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
import com.example.ui.components.StatusPill
import com.example.ui.components.TelemetryMetricTile
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

data class DockApp(
  val name: String,
  val icon: ImageVector,
  val target: String
)

@Composable
fun SystemAutomationHubScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val telemetry by viewModel.telemetry.collectAsState()
  var brightnessLevel by remember { mutableFloatStateOf(70f) }

  val devDockApps = listOf(
    DockApp("VS CODE", Icons.Default.Code, "vscode"),
    DockApp("CODE STUDIO", Icons.Default.DeveloperBoard, "androidcodestudio"),
    DockApp("TERMINAL", Icons.Default.Terminal, "terminal"),
    DockApp("CHROME", Icons.Default.Language, "chrome"),
    DockApp("CALCULATOR", Icons.Default.Calculate, "calc"),
    DockApp("NOTES", Icons.Default.NoteAlt, "notes")
  )

  val activeProcesses = listOf(
    "gradle-daemon (PID 1842)",
    "chrome-renderer-sandbox (PID 3912)",
    "node-local-server (PID 5021)"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      CyberHeader(
        title = "SYSTEM AUTOMATION & HARDWARE",
        code = "HW_MTX_03",
        modifier = Modifier.weight(1f)
      )
      IconButton(
        onClick = { viewModel.refreshTelemetry() },
        modifier = Modifier
          .size(32.dp)
          .testTag("btn_refresh_hardware")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Refresh",
          tint = HyperCyan,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Hardware Matrix Telemetry Tiles (2x2)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      TelemetryMetricTile(
        label = "CPU UTILIZATION",
        value = "${telemetry.cpuPercent}%",
        unit = "LOAD",
        accentColor = if (telemetry.cpuPercent > 70) AlertAmber else HyperCyan,
        modifier = Modifier.weight(1f)
      )
      TelemetryMetricTile(
        label = "MEMORY UTILIZATION",
        value = "${telemetry.ramUsedGb}",
        unit = "GB / ${telemetry.ramTotalGb} GB",
        accentColor = QuantumViolet,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      TelemetryMetricTile(
        label = "STORAGE BUFFER",
        value = "${telemetry.storageFreeGb}",
        unit = "GB FREE",
        accentColor = ElectricEmerald,
        modifier = Modifier.weight(1f)
      )
      TelemetryMetricTile(
        label = "AUDIO LEVEL",
        value = "${telemetry.volumePercent}%",
        unit = if (telemetry.isMuted) "[MUTED]" else "[ACTIVE]",
        accentColor = if (telemetry.isMuted) CriticalRed else HyperCyan,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 2. Hardware-Level Action Matrix
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("hardware_controls_card")
    ) {
      Column {
        Text(
          text = "INSTANT HARDWARE SWITCHES",
          fontFamily = OrbitronFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = HyperCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Audio controls row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CyberActionButton(
            text = "VOL +",
            onClick = { viewModel.executeManualTool("hardware_adjust", mapOf("target" to "volume_up", "value" to "10")) },
            color = CharcoalSlate,
            textColor = HyperCyan,
            modifier = Modifier.weight(1f)
          )
          CyberActionButton(
            text = "VOL -",
            onClick = { viewModel.executeManualTool("hardware_adjust", mapOf("target" to "volume_down", "value" to "10")) },
            color = CharcoalSlate,
            textColor = HyperCyan,
            modifier = Modifier.weight(1f)
          )
          CyberActionButton(
            text = if (telemetry.isMuted) "UNMUTE" else "MUTE",
            onClick = {
              val act = if (telemetry.isMuted) "unmute" else "mute"
              viewModel.executeManualTool("hardware_adjust", mapOf("target" to act, "value" to "0"))
            },
            color = if (telemetry.isMuted) ElectricEmerald else CriticalRed,
            textColor = DeepObsidian,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display Brightness Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "DISPLAY LUMINOSITY", fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = TextMuted)
          }
          Text(text = "${brightnessLevel.toInt()}%", fontFamily = OrbitronFontFamily, fontSize = 10.sp, color = HyperCyan)
        }

        Slider(
          value = brightnessLevel,
          onValueChange = {
            brightnessLevel = it
            viewModel.executeManualTool("hardware_adjust", mapOf("target" to "brightness", "value" to "${it.toInt()}"))
          },
          valueRange = 10f..100f,
          colors = SliderDefaults.colors(
            thumbColor = HyperCyan,
            activeTrackColor = HyperCyan,
            inactiveTrackColor = BorderSubtle
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Session Lock
        CyberActionButton(
          text = "LOCK WORKSTATION SESSION",
          onClick = {
            viewModel.executeManualTool("hardware_adjust", mapOf("target" to "lock_session", "value" to "lock"))
          },
          color = CharcoalSlate,
          textColor = AlertAmber,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Quick-Launch Developer Tools Dock
    Text(
      text = "DEVELOPER & SYSTEM APPLICATION DOCK",
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = TextMuted,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(bottom = 6.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        devDockApps.take(3).forEach { app ->
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceCard)
              .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
              .clickable {
                viewModel.executeManualTool("open_application", mapOf("app_name" to app.target))
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(imageVector = app.icon, contentDescription = app.name, tint = HyperCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = app.name, fontFamily = OrbitronFontFamily, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        devDockApps.drop(3).take(3).forEach { app ->
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceCard)
              .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
              .clickable {
                viewModel.executeManualTool("open_application", mapOf("app_name" to app.target))
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(imageVector = app.icon, contentDescription = app.name, tint = HyperCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = app.name, fontFamily = OrbitronFontFamily, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Process Terminator
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
            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "PROCESS TERMINATOR", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CriticalRed)
          }
          StatusPill(text = "ACTIVE MONITORS", color = AlertAmber)
        }

        Spacer(modifier = Modifier.height(8.dp))

        activeProcesses.forEach { proc ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = proc, fontFamily = RobotoMonoFontFamily, fontSize = 11.sp, color = TextPrimary)
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CriticalRed.copy(alpha = 0.2f))
                .border(1.dp, CriticalRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .clickable {
                  viewModel.executeManualTool("kill_process", mapOf("process_name" to proc.substringBefore(" ")))
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "TERMINATE", fontFamily = RobotoMonoFontFamily, fontSize = 9.sp, color = CriticalRed, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
