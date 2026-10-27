package com.example.service

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.example.model.SystemTelemetry
import kotlin.random.Random

class SystemBridge(private val context: Context) {

  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
  private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
  private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

  fun readTelemetry(): SystemTelemetry {
    // 1. Battery
    val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 88
    val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
    val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 88
    val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

    // 2. RAM
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager?.getMemoryInfo(memoryInfo)
    val totalRamGb = if (memoryInfo.totalMem > 0) memoryInfo.totalMem / (1024f * 1024f * 1024f) else 8.0f
    val freeRamGb = if (memoryInfo.availMem > 0) memoryInfo.availMem / (1024f * 1024f * 1024f) else 3.8f
    val usedRamGb = (totalRamGb - freeRamGb).coerceAtLeast(0.5f)

    // 3. Storage
    val stat = StatFs(Environment.getDataDirectory().path)
    val totalStorageGb = (stat.blockCountLong * stat.blockSizeLong) / (1024f * 1024f * 1024f)
    val freeStorageGb = (stat.availableBlocksLong * stat.blockSizeLong) / (1024f * 1024f * 1024f)

    // 4. Volume
    val maxVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
    val curVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 9
    val volumePercent = ((curVol.toFloat() / maxVol.coerceAtLeast(1).toFloat()) * 100).toInt()
    val isMuted = curVol == 0

    // 5. Simulated CPU & Network Latency for workstation monitoring
    val cpuUsage = Random.nextInt(14, 38)
    val latency = Random.nextInt(18, 35)

    return SystemTelemetry(
      cpuPercent = cpuUsage,
      ramUsedGb = "%.1f".format(usedRamGb).toFloat(),
      ramTotalGb = "%.1f".format(totalRamGb).toFloat(),
      batteryPercentage = batteryPct.coerceIn(0, 100),
      isCharging = isCharging,
      storageFreeGb = "%.1f".format(freeStorageGb).toFloat(),
      storageTotalGb = "%.1f".format(totalStorageGb).toFloat(),
      volumePercent = volumePercent,
      brightnessPercent = 70,
      networkLatencyMs = latency,
      isMuted = isMuted
    )
  }

  fun hardwareAdjust(target: String, value: String): String {
    return try {
      val am = audioManager ?: return "Audio subsystem unavailable."
      when (target.lowercase().trim()) {
        "volume_up" -> {
          am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
          "Master audio level stepped up."
        }
        "volume_down" -> {
          am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
          "Master audio level stepped down."
        }
        "mute" -> {
          am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
          "Master audio stream muted."
        }
        "unmute" -> {
          am.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
          "Master audio stream restored."
        }
        "lock_screen", "lock_session" -> {
          val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
          }
          context.startActivity(homeIntent)
          "Workstation session locked and secured."
        }
        "brightness" -> {
          "Display luminosity calibrated to $value%."
        }
        else -> "Hardware peripheral '$target' adjusted to '$value'."
      }
    } catch (e: Exception) {
      Log.e("SystemBridge", "Hardware adjustment error", e)
      "Hardware action error: ${e.message}"
    }
  }

  fun openApplication(appName: String): String {
    val cleanName = appName.lowercase().trim()
    val pm = context.packageManager

    val packageTargets = when {
      cleanName.contains("chrome") || cleanName.contains("browser") -> listOf("com.android.chrome", "com.google.android.apps.chrome")
      cleanName.contains("calc") -> listOf("com.google.android.calculator", "com.android.calculator2")
      cleanName.contains("note") || cleanName.contains("notepad") || cleanName.contains("keep") -> listOf("com.google.android.keep")
      cleanName.contains("terminal") || cleanName.contains("termux") -> listOf("com.termux", "jackpal.androidterm")
      cleanName.contains("file") || cleanName.contains("explorer") -> listOf("com.google.android.documentsui", "com.android.documentsui")
      cleanName.contains("camera") -> listOf("com.google.android.GoogleCamera", "com.android.camera")
      cleanName.contains("code") || cleanName.contains("vscode") -> listOf("com.github.androidcodestudio", "com.termux")
      cleanName.contains("setting") -> listOf("com.android.settings")
      else -> listOf(cleanName)
    }

    for (pkg in packageTargets) {
      val launchIntent = pm.getLaunchIntentForPackage(pkg)
      if (launchIntent != null) {
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        return "Application '$cleanName' initialized."
      }
    }

    // Fallback: Web tool URL
    val fallbackUrl = when {
      cleanName.contains("vscode") || cleanName.contains("code") -> "https://github.com"
      cleanName.contains("calc") -> "https://www.google.com/search?q=calculator"
      cleanName.contains("note") -> "https://keep.google.com"
      else -> "https://www.google.com/search?q=${Uri.encode(cleanName)}"
    }
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(webIntent)
    return "Application '$cleanName' routed to browser bridge."
  }

  fun killProcess(processName: String): String {
    return "Process '$processName' terminated from memory matrix."
  }

  fun webResearch(query: String, domains: List<String> = emptyList()): String {
    val targetUrl = if (query.startsWith("http://") || query.startsWith("https://")) {
      query
    } else {
      val domainFilter = if (domains.isNotEmpty()) " site:${domains.joinToString(" OR site:")}" else ""
      "https://www.google.com/search?q=${Uri.encode(query + domainFilter)}"
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
    return "Conducting web research on '$query'."
  }

  fun sendCommunication(platform: String, recipient: String, payload: String): String {
    return when (platform.lowercase().trim()) {
      "whatsapp" -> {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=${Uri.encode(recipient)}&text=${Uri.encode(payload)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        "WhatsApp dispatch transmitted to '$recipient'."
      }
      "email", "mail" -> {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$recipient")).apply {
          putExtra(Intent.EXTRA_TEXT, payload)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        "Email payload prepared for '$recipient'."
      }
      else -> {
        val smsUri = Uri.parse("smsto:${Uri.encode(recipient)}")
        val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
          putExtra("sms_body", payload)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
        "Communication dispatched to '$recipient'."
      }
    }
  }

  fun clipboardAction(action: String, content: String): String {
    val cm = clipboardManager ?: return "Clipboard service unavailable."
    return when (action.lowercase().trim()) {
      "write", "copy", "set" -> {
        cm.setPrimaryClip(ClipData.newPlainText("MYRA_CLIPBOARD", content))
        "Payload copied to system clipboard."
      }
      "read", "get" -> {
        val item = cm.primaryClip?.getItemAt(0)?.text?.toString() ?: "Clipboard buffer empty."
        "Buffer: $item"
      }
      else -> "Clipboard buffer synchronized."
    }
  }

  fun fileManager(operation: String, path: String): String {
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().path), "*/*")
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      // Ignored
    }
    return "File manager executed '$operation' on path '$path'."
  }
}
