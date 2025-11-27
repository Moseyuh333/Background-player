@echo off
echo ================================
echo Uninstalling old app...
echo ================================

.\gradlew.bat uninstallDebug

echo.
echo ================================
echo Building fresh APK...
echo ================================

.\gradlew.bat clean assembleDebug

echo.
echo ================================
echo Installing new app...
echo ================================

.\gradlew.bat installDebug

echo.
echo ================================
echo Done!
echo ================================
pause

