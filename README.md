# Nova Calculator

A polished Android calculator starter built with Kotlin and Jetpack Compose.

## Features
- Modern dark interface with a high-contrast display
- Basic arithmetic with operator precedence and parentheses
- Percentage, sign toggle, decimal input, clear and delete
- Calculation history (in-memory for this starter)
- Responsive button grid and haptic-friendly Material controls

## Requirements
- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35

## Run
1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Select an emulator or Android device.
4. Press **Run**.

## Project structure
- `app/src/main/java/com/nova/calculator/MainActivity.kt` — UI, state and expression parser
- `app/src/main/AndroidManifest.xml` — app entry point

## Notes
This is a starter project. History is currently kept in memory and resets when the app process is restarted.
