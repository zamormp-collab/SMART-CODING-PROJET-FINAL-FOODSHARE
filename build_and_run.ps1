#!/usr/bin/env pwsh

# Setup Android SDK environment
$env:ANDROID_HOME = "$env:USERPROFILE\AppData\Local\Android\Sdk"
$env:Path += ";$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\tools\bin"

# Build and install the app
Write-Host "Building and installing FoodShare app..." -ForegroundColor Green
cd C:\Users\guers\AndroidStudioProjects\FoodShare

# Build
.\gradlew.bat :app:installDebug

Write-Host "`nApp installed successfully!" -ForegroundColor Green
Write-Host "Launching app..." -ForegroundColor Green

# Launch app on emulator
$env:ANDROID_HOME = "$env:USERPROFILE\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n com.example.foodshare/.MainActivity

