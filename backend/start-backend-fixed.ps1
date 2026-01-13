param(
  [int]$Port = 8081,
  [string]$Profile = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$backendDir = Split-Path -Parent $PSCommandPath
$logDir = Join-Path $backendDir "logs"
New-Item -ItemType Directory -Force -Path $logDir | Out-Null

$stdoutLog = Join-Path $logDir "spring-boot.out.log"
$stderrLog = Join-Path $logDir "spring-boot.err.log"

function Find-MvnCmd {
  if ($env:MAVEN_HOME) {
    $p = Join-Path $env:MAVEN_HOME "bin\\mvn.cmd"
    if (Test-Path $p) { return $p }
  }

  $root = Join-Path $env:USERPROFILE ".maven"
  if (Test-Path $root) {
    $found = Get-ChildItem -Path $root -Recurse -Filter "mvn.cmd" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found -and $found.FullName) { return $found.FullName }
  }

  throw "找不到 mvn.cmd（请先安装 Maven 或设置 MAVEN_HOME）"
}

function Stop-ListenerOnPort([int]$p) {
  $pids = @()

  $cmd = Get-Command Get-NetTCPConnection -ErrorAction SilentlyContinue
  if ($cmd) {
    $pids = @(Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique)
  } else {
    $lines = netstat -ano | Select-String -SimpleMatch (":" + $p)
    foreach ($l in $lines) {
      $parts = ($l.Line -split "\\s+" | Where-Object { $_ -and $_.Trim() -ne "" })
      if ($parts.Count -ge 5) {
        $pid = $parts[-1]
        if ($pid -match "^\\d+$") { $pids += [int]$pid }
      }
    }
    $pids = $pids | Sort-Object -Unique
  }

  foreach ($pid in $pids) {
    try { Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue } catch { }
  }
}

$mvn = Find-MvnCmd
Stop-ListenerOnPort -p $Port

$args = @("-DskipTests", "spring-boot:run")
if ($Profile -and $Profile.Trim() -ne "") {
  $args += ("-Dspring-boot.run.profiles=" + $Profile)
}

Write-Host "Starting backend (detached)..."
Write-Host ("mvn: " + $mvn)

Start-Process -FilePath $mvn -ArgumentList $args -WorkingDirectory $backendDir -RedirectStandardOutput $stdoutLog -RedirectStandardError $stderrLog -WindowStyle Hidden | Out-Null

Write-Host "Backend started."
Write-Host ("stdout: " + $stdoutLog)
Write-Host ("stderr: " + $stderrLog)
