@echo off
echo Initializing Gradle wrapper...

REM Create wrapper directory if not exists
if not exist "gradle\wrapper" mkdir gradle\wrapper

REM Download gradle-wrapper.jar
echo Downloading gradle-wrapper.jar...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.2.1/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"

if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Gradle wrapper downloaded successfully!
    echo Now you can run: gradlew.bat clean build
) else (
    echo Failed to download gradle-wrapper.jar
    echo Please download it manually from: https://github.com/gradle/gradle/tree/master/gradle/wrapper
)

pause

