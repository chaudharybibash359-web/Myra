package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MacroWorkflow
import com.example.model.ToolExecutionStatus
import com.example.ui.components.CyberActionButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
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
import com.example.viewmodel.MyraCoreViewModel

@Composable
fun MacroStudioScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val workflows by viewModel.macros.collectAsState()
  var selectedWorkflow by remember { mutableStateOf(workflows.first()) }

  // Sync selected workflow updates
  val currentWorkflow = workflows.find { it.id == selectedWorkflow.id } ?: selectedWorkflow

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    CyberHeader(
      title = "WORKFLOW & MACRO STUDIO",
      code = "AUTO_WKF_04"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Workflow Selector Carousel
    Text(
      text = "SELECT AUTONOMOUS WORKFLOW",
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = TextMuted,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(bottom = 6.dp)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      workflows.forEach { wf ->
        val isSelected = currentWorkflow.id == wf.id
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SurfaceCard else CharcoalSlate)
            .border(1.dp, if (isSelected) HyperCyan else BorderSubtle, RoundedCornerShape(8.dp))
            .clickable { selectedWorkflow = wf }
            .padding(10.dp)
            .testTag("macro_tab_${wf.id}"),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = wf.icon,
              contentDescription = wf.name,
              tint = if (isSelected) HyperCyan else TextMuted,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = wf.name,
              fontFamily = OrbitronFontFamily,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) TextPrimary else TextMuted
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Active Macro Execution Card & Visual Timeline
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("active_macro_card"),
      borderColor = if (currentWorkflow.isRunning) HyperCyan else BorderSubtle
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = currentWorkflow.name.uppercase(),
              fontFamily = OrbitronFontFamily,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = HyperCyan
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = currentWorkflow.description,
              fontFamily = RobotoMonoFontFamily,
              fontSize = 10.sp,
              color = TextMuted
            )
          }

          StatusPill(
            text = if (currentWorkflow.isRunning) "RUNNING" else "READY",
            color = if (currentWorkflow.isRunning) HyperCyan else ElectricEmerald
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Timeline Steps
        Text(
          text = "SEQUENTIAL OPERATION TIMELINE (${currentWorkflow.steps.size} STEPS)",
          fontFamily = RobotoMonoFontFamily,
          fontSize = 10.sp,
          color = TextSecondary,
          letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        currentWorkflow.steps.forEachIndexed { index, step ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Step node circle
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                  when (step.status) {
                    ToolExecutionStatus.SUCCESS -> ElectricEmerald
                    ToolExecutionStatus.RUNNING -> HyperCyan
                    else -> CharcoalSlate
                  }
                )
                .border(1.dp, BorderSubtle, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (step.status == ToolExecutionStatus.RUNNING) {
                CircularProgressIndicator(
                  modifier = Modifier.size(14.dp),
                  color = DeepObsidian,
                  strokeWidth = 2.dp
                )
              } else if (step.status == ToolExecutionStatus.SUCCESS) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = DeepObsidian,
                  modifier = Modifier.size(14.dp)
                )
              } else {
                Text(
                  text = "${index + 1}",
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = step.title,
                fontFamily = OrbitronFontFamily,
                fontSize = 11.sp,
                color = when (step.status) {
                  ToolExecutionStatus.RUNNING -> HyperCyan
                  ToolExecutionStatus.SUCCESS -> ElectricEmerald
                  else -> TextPrimary
                }
              )
              Text(
                text = "${step.toolName}(${step.params.entries.joinToString { "${it.key}: ${it.value}" }})",
                fontFamily = RobotoMonoFontFamily,
                fontSize = 10.sp,
                color = TextMuted
              )
            }

            StatusPill(
              text = step.status.name,
              color = when (step.status) {
                ToolExecutionStatus.SUCCESS -> ElectricEmerald
                ToolExecutionStatus.RUNNING -> HyperCyan
                else -> BorderSubtle
              }
            )
          }

          if (index < currentWorkflow.steps.size - 1) {
            Box(
              modifier = Modifier
                .padding(start = 11.dp)
                .width(2.dp)
                .height(12.dp)
                .background(BorderSubtle)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CyberActionButton(
          text = if (currentWorkflow.isRunning) "EXECUTING WORKFLOW..." else "EXECUTE MACRO",
          onClick = { viewModel.runMacro(currentWorkflow) },
          enabled = !currentWorkflow.isRunning,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_execute_macro"),
          color = HyperCyan
        )
      }
    }
  }
}
