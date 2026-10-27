package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CoreVisualState
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CharcoalSlate
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.HyperCyanGlow
import com.example.ui.theme.OrbitronFontFamily
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.QuantumVioletGlow
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The Hyperion Quantum Core
 * Proprietary Vector-Rendered AI Architecture:
 * - Outer concentric ring with 4 segmented orbital ticks at 90° intervals
 * - Inner rotating hexagonal chassis rendered in glowing cyan wireframe
 * - Pulsing deep-violet micro-nucleus in the center flaring dynamically based on state
 */
@Composable
fun HyperionQuantumCore(
  state: CoreVisualState,
  modifier: Modifier = Modifier,
  sizeDp: Int = 140
) {
  val infiniteTransition = rememberInfiniteTransition(label = "core_anim")

  // Rotation animation
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = if (state == CoreVisualState.PROCESSING) 1800 else 9000,
        easing = LinearEasing
      )
    ),
    label = "rotation"
  )

  // Pulsing scale animation
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = when (state) {
          CoreVisualState.LISTENING -> 500
          CoreVisualState.SPEAKING -> 400
          CoreVisualState.WARNING -> 600
          CoreVisualState.PROCESSING -> 800
          CoreVisualState.IDLE -> 2200
        },
        easing = FastOutSlowInEasing
      ),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Canvas(
    modifier = modifier
      .size(sizeDp.dp)
      .testTag("hyperion_quantum_core_canvas")
  ) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRadius = (size.minDimension / 2f) * 0.92f
    val hexRadius = outerRadius * 0.65f
    val nucleusRadius = outerRadius * 0.32f * pulseScale

    // 1. Primary Colors based on state
    val primaryRingColor = when (state) {
      CoreVisualState.IDLE -> HyperCyan
      CoreVisualState.LISTENING -> QuantumViolet
      CoreVisualState.PROCESSING -> HyperCyan
      CoreVisualState.WARNING -> AlertAmber
      CoreVisualState.SPEAKING -> HyperCyan
    }

    val nucleusColor = when (state) {
      CoreVisualState.IDLE -> QuantumViolet
      CoreVisualState.LISTENING -> QuantumViolet
      CoreVisualState.PROCESSING -> HyperCyan
      CoreVisualState.WARNING -> AlertAmber
      CoreVisualState.SPEAKING -> QuantumViolet
    }

    // 2. Outer Concentric Ring
    drawCircle(
      color = primaryRingColor.copy(alpha = 0.25f),
      radius = outerRadius,
      center = center,
      style = Stroke(width = 1.5.dp.toPx())
    )

    // Secondary sub-orbit
    drawCircle(
      color = primaryRingColor.copy(alpha = 0.12f),
      radius = outerRadius * 0.82f,
      center = center,
      style = Stroke(width = 1.dp.toPx())
    )

    // 3. 4 Segmented Orbital Ticks at 90-degree intervals (0, 90, 180, 270)
    for (i in 0 until 4) {
      val angleDeg = i * 90.0
      val angleRad = Math.toRadians(angleDeg)
      val innerTickR = outerRadius - 10.dp.toPx()
      val outerTickR = outerRadius + 6.dp.toPx()

      val startX = (center.x + innerTickR * cos(angleRad)).toFloat()
      val startY = (center.y + innerTickR * sin(angleRad)).toFloat()
      val endX = (center.x + outerTickR * cos(angleRad)).toFloat()
      val endY = (center.y + outerTickR * sin(angleRad)).toFloat()

      drawLine(
        color = primaryRingColor,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
      )
    }

    // 4. Inner Rotating Hexagonal Chassis in glowing cyan wireframe
    rotate(degrees = rotation, pivot = center) {
      val hexPath = Path()
      for (i in 0 until 6) {
        val angle = (i * 60.0) * (PI / 180.0)
        val x = (center.x + hexRadius * cos(angle)).toFloat()
        val y = (center.y + hexRadius * sin(angle)).toFloat()
        if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
      }
      hexPath.close()

      // Outer Hex Wireframe
      drawPath(
        path = hexPath,
        color = HyperCyan,
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
      )

      // Hexagonal spokes to center
      for (i in 0 until 6) {
        val angle = (i * 60.0) * (PI / 180.0)
        val x = (center.x + hexRadius * cos(angle)).toFloat()
        val y = (center.y + hexRadius * sin(angle)).toFloat()
        drawLine(
          color = HyperCyan.copy(alpha = 0.35f),
          start = center,
          end = Offset(x, y),
          strokeWidth = 1.dp.toPx()
        )
      }
    }

    // 5. Pulsing Deep-Violet Micro-Nucleus
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          nucleusColor,
          nucleusColor.copy(alpha = 0.45f),
          Color.Transparent
        ),
        center = center,
        radius = nucleusRadius * 1.5f
      ),
      radius = nucleusRadius * 1.4f,
      center = center
    )

    drawCircle(
      color = Color.White,
      radius = (nucleusRadius * 0.35f).coerceAtLeast(3.dp.toPx()),
      center = center
    )
  }
}

@Composable
fun CyberCard(
  modifier: Modifier = Modifier,
  borderColor: Color = BorderSubtle,
  backgroundColor: Color = SurfaceCard,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(backgroundColor)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .padding(14.dp)
  ) {
    content()
  }
}

@Composable
fun CyberHeader(
  title: String,
  code: String,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(HyperCyan)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = title,
        fontFamily = OrbitronFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = TextPrimary,
        letterSpacing = 1.sp
      )
    }
    Text(
      text = "[$code]",
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = HyperCyan
    )
  }
}

@Composable
fun AudioWaveformVisualizer(
  amplitudes: List<Float>,
  isSpeaking: Boolean,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .height(54.dp)
      .testTag("audio_waveform_canvas")
  ) {
    val barCount = amplitudes.size.coerceAtLeast(1)
    val totalWidth = size.width
    val maxHeight = size.height
    val spacing = 5.dp.toPx()
    val barWidth = ((totalWidth - (spacing * (barCount - 1))) / barCount).coerceAtLeast(3.dp.toPx())

    for (i in 0 until barCount) {
      val rawAmp = amplitudes.getOrElse(i) { 0.2f }
      val effectiveAmp = if (isSpeaking) (rawAmp * pulseScale).coerceIn(0.15f, 1.0f) else 0.15f
      val barHeight = maxHeight * effectiveAmp
      val top = (maxHeight - barHeight) / 2f
      val left = i * (barWidth + spacing)

      val brush = Brush.verticalGradient(
        colors = if (isSpeaking) listOf(HyperCyan, QuantumViolet) else listOf(BorderSubtle, HyperCyanGlow),
        startY = top,
        endY = top + barHeight
      )

      drawRoundRect(
        brush = brush,
        topLeft = Offset(left, top),
        size = Size(barWidth, barHeight),
        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
      )
    }
  }
}

@Composable
fun TelemetryMetricTile(
  label: String,
  value: String,
  unit: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = accentColor.copy(alpha = 0.4f)
  ) {
    Column {
      Text(
        text = label.uppercase(),
        fontFamily = RobotoMonoFontFamily,
        fontSize = 10.sp,
        color = TextMuted,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = value,
          fontFamily = OrbitronFontFamily,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          color = accentColor
        )
        if (unit.isNotBlank()) {
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = unit,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 10.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun CyberActionButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  color: Color = HyperCyan,
  textColor: Color = DeepObsidian,
  enabled: Boolean = true
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (enabled) color else BorderSubtle)
      .clickable(enabled = enabled, onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text.uppercase(),
      fontFamily = OrbitronFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 11.sp,
      color = if (enabled) textColor else TextMuted,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun StatusPill(
  text: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(color.copy(alpha = 0.12f))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = text,
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = color,
      fontWeight = FontWeight.Bold
    )
  }
}
