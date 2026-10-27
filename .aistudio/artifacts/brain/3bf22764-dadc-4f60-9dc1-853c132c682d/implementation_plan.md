# MYRA CORE - Autonomous Cyber-Neon AI Assistant

An advanced, cyber-neon workstation assistant and central intelligence engine for Android featuring real-time telemetry, conversational voice with TTS waveforms, screen vision inspection, system hardware control, and multimodal reasoning powered by Firebase AI Logic (Gemini).

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and design decisions were confirmed during clarification:

- **AI Engine & Inference Protocol**: Firebase AI Logic with Gemini 3.8 Flash (`gemini-3.8-flash`) for multimodal vision analysis, tool calling protocol, and conversational streaming responses, secured with App Check attestation.
- **Voice & Speech Interface**: Interactive voice output via native Android Text-to-Speech (TTS), speech-to-text input with audio waveform rendering, and concise, punchy natural phrasing. Multilingual support across English, Hindi, and Hinglish.
- **Visual Aesthetic & Styling**: Cyber-neon terminal palette featuring deep obsidian black (`#070A0F`), cyber cyan (`#00F0FF`), electric magenta (`#FF007F`), and amber telemetry accents (`#FFB800`), with monospaced data readouts, hexagonal HUD badges, and glowing borders.
- **Device Hardware & Tool Execution**: Real Android hardware integrations:
  - `open_application`: Launching installed applications and utilities via Android package manager Intents.
  - `system_control`: Device volume adjustment, audio muting, and lock screen intent.
  - `analyze_screen`: Multimodal screenshot and photo inspection via Android Photo Picker and camera input to detect bugs, code syntax errors, and UI elements.
  - `web_browse`: Opening custom URLs or instant search query execution in browser.
  - `send_message`: Intent-based messaging dispatch to SMS and communication apps.

---

## 1. Overview & Core Concept

**MYRA CORE** operates as a next-generation workstation companion and autonomous AI assistant. It bridges real-time Android device controls, live telemetry monitoring, and Gemini-driven multimodal intelligence behind a sleek cyber-neon HUD interface.

### Target Audience & Persona
- **Persona**: MYRA CORE is calm, authoritative, efficient, and proactive. Answers are concise (1 to 2 spoken sentences) with no extraneous markdown noise during speech.
- **Target Audience**: Power users, developers, and tech enthusiasts seeking an integrated cyber assistant with tactile OS controls, voice telemetry, and visual error inspection.

### Key Value
Seamlessly integrates voice-driven autonomous device actions, live CPU/RAM/Battery metrics, visual screen bug analysis, and conversational intelligence into a unified cyberpunk dashboard.

---

## 2. User Experience & Visual Design

### Key Modules & User Flows

1. **Console Dashboard (Terminal HUD)**:
   - Live conversational transcript with distinct badges for User, MYRA, and Tool Actions.
   - Interactive glowing microphone button with real-time audio waveform canvas animation.
   - Quick command action chips: *"Mute audio and open browser"*, *"Inspect code error"*, *"System status report"*, *"Awaaz badha do"*.
   - Instant text input terminal with command syntax hints (`/exec`, `/tool`, `/analyze`).
   - One-tap audio replay button for any response with Android TTS.

2. **Screen Vision Inspector**:
   - Visual inspection viewport for analyzing monitor/app screenshots or camera captures.
   - Zero-permission Android Photo Picker (`PickVisualMedia`) + sample developer error screenshots for quick testing.
   - Real-time Gemini multimodal analysis detecting syntax bugs, UI elements, and actionable fixes.
   - Spoken diagnostic summary readout with voice-ready audio confirmation.

3. **System Control Hub**:
   - Hardware telemetry cards: Real-time device battery percentage, available RAM/memory, storage gauges, and audio stream levels.
   - Interactive controls: Volume sliders, mute toggle, quick app launcher drawer (Chrome, Terminal, Calculator, Notes, Files), and screen lock.
   - Real-time execution log showing dispatched tool calls and status badges (`SUCCESS`, `PENDING`, `DISPATCHED`).

4. **Neural Settings**:
   - Voice tuning: TTS speech rate, pitch, language selection (Auto-detect English / Hindi / Hinglish).
   - Autonomous execution mode toggles: Auto-execute safe actions, confirmation prompt for destructive actions.
   - Model parameters: Temperature slider, max tokens, system role prompt customization.
   - Firebase AI Logic & App Check health status indicators.

### Visual Identity & Theme
- **Background**: Deep space obsidian `#070A0F` with secondary surface `#0D1522` and tertiary slate `#152238`.
- **Accents**: Neon Cyber Cyan (`#00F0FF`), Electric Magenta (`#FF007F`), Amber Alert (`#FFB800`), Matrix Emerald (`#00FF88`).
- **Typography**: Crisp cybernetic monospaced headers and data meters, paired with legible body typography.
- **Surfaces & Borders**: 1dp glowing neon borders, glassmorphic card overlays with subtle blur, pulsing status lights, and animated canvas audio waveforms.
- **Safe Areas**: Strict edge-to-edge support with `safeDrawing` and `navigationBarsPadding` to prevent notch or navigation bar clipping.

---

## 3. Key Product Decisions & Trade-Offs

- **Firebase AI Logic with Gemini 3.8 Flash**:
  - *Chosen Approach*: Official Firebase AI Logic SDK (`gemini-3.8-flash`) with lazy model initialization and App Check security.
  - *Why*: Provides official multimodal vision inference, fast streaming, and structured tool-calling support without hardcoding API keys.
  - *Fallback / Graceful Handling*: Transparent error propagation displaying connection/App Check state if offline, with a diagnostic retry mechanism.

- **Audio Architecture & Natural Voice TTS**:
  - *Chosen Approach*: Android `TextToSpeech` engine configured with dynamic locale detection (en-US, hi-IN), paired with Compose audio waveform animation driven by coroutine amplitude simulation.
  - *Why*: Zero latency speech output on Android with fluid speech rate and natural inflection, avoiding external streaming audio dependencies.

- **Android System Action Execution**:
  - *Chosen Approach*: Real Android system intents and service managers (`AudioManager`, `Intent.ACTION_VIEW`, `PackageManager.getLaunchIntentForPackage`, `Intent.ACTION_SENDTO`) for reliable device automation without requiring unsupported root privileges.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MYRA CORE                                 │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
    ┌───────────────────────────────┴───────────────────────────────┐
    ▼                               ▼                               ▼
┌───────────────────────┐ ┌───────────────────────┐ ┌─────────────────────┐
│   Console Dashboard   │ │Screen Vision Inspector│ │ System Control Hub  │
│  - Live Audio Waveform│ │ - Screenshot Viewer   │ │ - Battery / RAM     │
│  - TTS Voice Engine   │ │ - Photo Picker        │ │ - Volume & Mute     │
│  - Chat History Flow  │ │ - Bug / UI Detector   │ │ - App Launcher      │
└───────────┬───────────┘ └───────────┬───────────┘ └──────────┬──────────┘
            │                         │                        │
            └─────────────────────────┼────────────────────────┘
                                      ▼
                      ┌───────────────────────────────┐
                      │        MyraCoreViewModel      │
                      │  - StateFlow<MyraUiState>     │
                      │  - Tool Calling Dispatcher    │
                      │  - Speech & Audio Controller  │
                      └───────────────┬───────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
┌───────────────────────────────┐               ┌───────────────────────────┐
│     Firebase AI Logic SDK     │               │   Android System Bridge   │
│  - Gemini 3.8 Flash           │               │  - AudioManager           │
│  - Multimodal Vision          │               │  - ActivityManager / Info │
│  - App Check Attestation      │               │  - PackageManager Intents │
└───────────────────────────────┘               └───────────────────────────┘
```

### Core Data Models & State
- `MyraMessage`: Unique ID, sender role (`USER`, `MYRA`, `SYSTEM`), text content, timestamp, attached bitmap/uri, and optional `ToolActionCall`.
- `ToolActionCall`: Tool name (`open_application`, `system_control`, `analyze_screen`, `web_browse`, `send_message`), parameters map, execution status (`PENDING`, `SUCCESS`, `ERROR`), and result text.
- `SystemTelemetry`: Battery level (0-100%), charging state, free RAM (GB), total RAM (GB), internal storage space, and audio volume stream level.
- `MyraUiState`: Active tab index, conversation messages, speech recording status, TTS speaking state, telemetry snapshot, vision inspection state, and error message.

---

## 5. Phased Implementation Roadmap

1. **Phase 1: Project & Firebase Setup**:
   - Provision Firebase AI Logic via `rpc_action(ProvisionFirebase)` with `package_name` and extracted SHA fingerprints.
   - Configure App Check in `MainActivity.kt` with debug token intent handling.
   - Update `metadata.json` ("MYRA CORE") and app string resources.
2. **Phase 2: Cyber-Neon Theme & Design System**:
   - Define cybernetic color palette, typography tokens, custom card shapes, and neon glow modifier extensions.
   - Implement dynamic Canvas audio waveform visualizer and status indicators.
3. **Phase 3: Core ViewModel, TTS & System Bridge**:
   - Implement `TextToSpeech` manager supporting English and Hindi locales.
   - Implement `SystemTelemetryBridge` reading battery, memory, volume, and app launch intents.
   - Implement `MyraAiService` connecting to Gemini 3.8 Flash via Firebase AI Logic with tool parsing.
4. **Phase 4: Module Views & Navigation**:
   - Build `ConsoleDashboardScreen` with live voice waveforms, quick dispatch chips, and terminal chat.
   - Build `ScreenVisionScreen` with image upload, sample screenshot presets, and vision inspection analysis.
   - Build `SystemControlHubScreen` with interactive hardware controls and telemetry dials.
   - Build `NeuralSettingsScreen` with voice parameters, language, and system prompt tuning.
5. **Phase 5: Verification & Polish**:
   - Ensure edge-to-edge insets, touch target compliance (>= 48dp), and robust error handling.
   - Verify build with `compile_applet`.
