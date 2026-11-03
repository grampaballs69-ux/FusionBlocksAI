# Fusion Blocks AI — Android project (foundation build)

This is a native Android Studio project, package `com.fusionblocks.ai`, min Android 8.0 (API 26), target API 35.

## Included
- Android MediaProjection foreground service; saves a fresh full-screen screenshot to the app's private external-files folder at `Android/data/com.fusionblocks.ai/files/captures/latest.png`.
- Accessibility service declaration and a one-at-a-time drag gesture method.
- Exhaustive three-piece placement search engine for a configurable rectangular board, including row/column clearing.
- Simple setup UI and permission flow.

## Important current limitation
This is **not yet a finished automatic-playing APK**. The screenshot-to-grid/block visual detector, calibration screen, board-state verification, and orchestration that calls the solver and then sends three separate drags are not implemented yet. Those parts depend on a screenshot of the exact Fusion Blocks app and its grid/rules. This build intentionally does not guess coordinates or make unsafe blind moves.

## Build
1. Install Android Studio (current stable) on Windows.
2. Open this folder in Android Studio.
3. Allow Gradle sync; Android Studio will download Android Gradle Plugin and Android SDK 35 if needed.
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Device setup
1. Install APK.
2. Open app and enable its Accessibility service in Android Settings > Accessibility.
3. Return and grant screen-capture permission.
4. Switch to Fusion Blocks. Android shows an ongoing capture notification.

Screen capture and automated gestures are explicitly permission-gated by Android. The app does not use ADB and does not bypass Android's consent dialogs.
