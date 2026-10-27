package com.example.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.VisionPreset
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
import com.example.ui.theme.TextViolet
import com.example.viewmodel.MyraCoreViewModel

@Composable
fun ScreenVisionLabScreen(
  viewModel: MyraCoreViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isAnalyzing by viewModel.isAnalyzingVision.collectAsState()
  val visionDiagnosis by viewModel.visionResult.collectAsState()
  val ocrText by viewModel.ocrExtractedText.collectAsState()

  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var selectedPreset by remember { mutableStateOf<VisionPreset?>(viewModel.visionPresets.first()) }
  var targetRegion by remember { mutableStateOf("Full Screen (Active Window)") }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      selectedImageUri = uri
      selectedPreset = null
      try {
        val stream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(stream)
        if (bitmap != null) {
          viewModel.analyzeScreenBitmap(bitmap, "Audit visual code and parse OCR buffer")
        }
      } catch (e: Exception) {
        // Handled
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepObsidian)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    CyberHeader(
      title = "SCREEN VISION & OCR LAB",
      code = "CV_OCR_02"
    )

    Spacer(modifier = Modifier.height(10.dp))

    // 1. Target Region Selector Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(CharcoalSlate)
        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
        .padding(6.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("Full Screen", "Active Terminal", "Error Modal").forEach { region ->
        val isSelected = targetRegion.contains(region)
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) SurfaceCard else DeepObsidian)
            .border(1.dp, if (isSelected) HyperCyan else BorderSubtle, RoundedCornerShape(6.dp))
            .clickable { targetRegion = region }
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = region.uppercase(),
            fontFamily = RobotoMonoFontFamily,
            fontSize = 9.sp,
            color = if (isSelected) HyperCyan else TextMuted,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Optical Capture Canvas / Viewport
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("ocr_viewport_card"),
      borderColor = if (isAnalyzing) HyperCyan else BorderSubtle
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "OPTICAL CANVAS VIEWPORT",
            fontFamily = OrbitronFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = HyperCyan
          )
          StatusPill(
            text = if (isAnalyzing) "OCR SCANNING" else "FEED ONLINE",
            color = if (isAnalyzing) QuantumViolet else ElectricEmerald
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DeepObsidian)
            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          if (selectedImageUri != null) {
            AsyncImage(
              model = selectedImageUri,
              contentDescription = "Active Screen Capture",
              modifier = Modifier.fillMaxSize()
            )
          } else if (selectedPreset != null) {
            Column(modifier = Modifier.fillMaxSize()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "[${selectedPreset?.category}]",
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 10.sp,
                  color = QuantumViolet
                )
                Text(
                  text = selectedPreset?.title ?: "",
                  fontFamily = OrbitronFontFamily,
                  fontSize = 10.sp,
                  color = TextPrimary
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = selectedPreset?.sampleText ?: "",
                fontFamily = RobotoMonoFontFamily,
                fontSize = 11.sp,
                color = TextCyan,
                lineHeight = 16.sp
              )
            }
          }

          if (isAnalyzing) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidian.copy(alpha = 0.8f)),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = HyperCyan, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Multimodal Vision & OCR Engine Parsing...",
                  fontFamily = RobotoMonoFontFamily,
                  fontSize = 11.sp,
                  color = HyperCyan
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Actions: Photo Picker or Run OCR
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(CharcoalSlate)
              .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .padding(10.dp)
              .testTag("btn_capture_screen"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "UPLOAD CAPTURE", fontFamily = OrbitronFontFamily, fontSize = 10.sp, color = HyperCyan, fontWeight = FontWeight.Bold)
            }
          }

          CyberActionButton(
            text = "AUDIT SCREEN",
            onClick = {
              if (selectedPreset != null) {
                viewModel.analyzePreset(selectedPreset!!)
              } else if (selectedImageUri != null) {
                try {
                  val stream = context.contentResolver.openInputStream(selectedImageUri!!)
                  val bitmap = BitmapFactory.decodeStream(stream)
                  if (bitmap != null) {
                    viewModel.analyzeScreenBitmap(bitmap, "Audit visible screen coordinates and syntax")
                  }
                } catch (e: Exception) {
                  // Fallback
                }
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("btn_audit_screen")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. OCR Text Extraction Buffer
    CyberCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("ocr_buffer_card"),
      borderColor = BorderSubtle
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, tint = QuantumViolet, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "OCR TEXT EXTRACTION BUFFER", fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = QuantumViolet)
          }

          if (ocrText != null) {
            Row(
              modifier = Modifier
                .clickable {
                  viewModel.executeManualTool("clipboard_action", mapOf("action" to "write", "content" to (ocrText ?: "")))
                }
                .padding(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = HyperCyan, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "COPY", fontFamily = RobotoMonoFontFamily, fontSize = 9.sp, color = HyperCyan)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = ocrText ?: selectedPreset?.ocrExtractedText ?: "OCR buffer waiting for screen frame input...",
          fontFamily = RobotoMonoFontFamily,
          fontSize = 11.sp,
          color = TextPrimary,
          lineHeight = 16.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Built-in Preset Scenarios
    Text(
      text = "WORKSTATION DEFECT & MODAL SCENARIOS",
      fontFamily = RobotoMonoFontFamily,
      fontSize = 10.sp,
      color = TextMuted,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(bottom = 6.dp)
    )

    viewModel.visionPresets.forEach { preset ->
      val isSelected = selectedPreset?.id == preset.id
      CyberCard(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp)
          .clickable {
            selectedPreset = preset
            selectedImageUri = null
          }
          .testTag("preset_${preset.id}"),
        borderColor = if (isSelected) QuantumViolet else BorderSubtle
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = preset.title, fontFamily = OrbitronFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) QuantumViolet else TextPrimary)
            Text(text = preset.errorHighlight, fontFamily = RobotoMonoFontFamily, fontSize = 10.sp, color = TextMuted)
          }
          StatusPill(text = preset.category, color = if (isSelected) QuantumViolet else BorderSubtle)
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 5. Spoken Diagnostic Report & Actionable Fix
    if (visionDiagnosis != null) {
      CyberCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("diagnostic_report_card"),
        borderColor = ElectricEmerald
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ElectricEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "SPOKEN DIAGNOSIS", fontFamily = OrbitronFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ElectricEmerald)
            }

            Row(
              modifier = Modifier
                .clickable { viewModel.replayTts(visionDiagnosis!!) }
                .padding(4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "VOCALIZE", fontFamily = RobotoMonoFontFamily, fontSize = 9.sp, color = HyperCyan)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = visionDiagnosis!!,
            fontFamily = RobotoMonoFontFamily,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 17.sp
          )
        }
      }
    }
  }
}
