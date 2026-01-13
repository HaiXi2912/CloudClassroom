@echo off
setlocal

set PORT=8081
set BACKEND_DIR=%~dp0
set LOG_DIR=%BACKEND_DIR%logs

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

set OUT_LOG=%LOG_DIR%\spring-boot.out.log
set ERR_LOG=%LOG_DIR%\spring-boot.err.log

set MVN_CMD=

if defined MAVEN_HOME (
  if exist "%MAVEN_HOME%\bin\mvn.cmd" set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
)

if not defined MVN_CMD (
  if exist "%USERPROFILE%\.maven\maven-3.9.12\bin\mvn.cmd" set MVN_CMD=%USERPROFILE%\.maven\maven-3.9.12\bin\mvn.cmd
)

if not defined MVN_CMD (
  for /r "%USERPROFILE%\.maven" %%f in (mvn.cmd) do (
    set MVN_CMD=%%f
    goto :found
  )
)

:found
if not defined MVN_CMD (
  echo ERROR: mvn.cmd not found. Install Maven or set MAVEN_HOME.
  exit /b 1
)

REM Kill existing listener on PORT
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%PORT% " ^| findstr LISTENING') do (
  taskkill /F /PID %%p >nul 2>nul
)

cd /d "%BACKEND_DIR%"

echo Starting backend on port %PORT% ...
echo Maven: %MVN_CMD%
echo Stdout: %OUT_LOG%
echo Stderr: %ERR_LOG%

start "cloudclassroom-backend" /b cmd /c "\"%MVN_CMD%\" -DskipTests spring-boot:run 1>>\"%OUT_LOG%\" 2>>\"%ERR_LOG%\""

echo Started (detached).
endlocal
