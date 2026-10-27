package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HyperionColorScheme = darkColorScheme(
  primary = HyperCyan,
  onPrimary = DeepObsidian,
  primaryContainer = CharcoalSlate,
  onPrimaryContainer = HyperCyan,
  secondary = QuantumViolet,
  onSecondary = DeepObsidian,
  secondaryContainer = CharcoalSlate,
  onSecondaryContainer = QuantumViolet,
  tertiary = AlertAmber,
  onTertiary = DeepObsidian,
  background = DeepObsidian,
  onBackground = TextPrimary,
  surface = CharcoalSlate,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceCard,
  onSurfaceVariant = TextMuted,
  outline = BorderSubtle,
  error = CriticalRed,
  onError = DeepObsidian
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = HyperionColorScheme,
    typography = Typography,
    content = content
  )
}
