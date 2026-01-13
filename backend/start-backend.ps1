param(
  [int]$Port = 8081,
  [string]$Profile = ""
)

$ErrorActionPreference = 'Stop'

$backendDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $backendDir 'logs'
New-Item -ItemType Directory -Force -Path $logDir | Out-Null

$stdoutLog = Join-Path $logDir 'spring-boot.out.log'
$stderrLog = Join-Path $logDir 'spring-boot.err.log'

function Find-MvnCmd {
  $candidates = @()

  if ($env:MAVEN_HOME) {
    $candidates += (Join-Path $env:MAVEN_HOME 'bin\mvn.cmd')
  }

  $userMavenRoot = Join-Path $env:USERPROFILE '.maven'
  if (Test-Path $userMavenRoot) {
    $found = Get-ChildItem -Path $userMavenRoot -Recurse -Filter mvn.cmd -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found -and $found.FullName) {
      $candidates += $found.FullName
    }
  }

  foreach ($p in $candidates) {
    if ($p -and (Test-Path $p)) {
      return $p
    }
  }

  throw '找不到 mvn.cmd。请先安装 Maven 或设置 MAVEN_HOME。'
}

function Stop-PortProcess([int]$p) {
  $matches = netstat -ano | Select-String -Pattern (':'+$p+'\s')
  if (-not $matches) {
    return
  }

  $pids = @()
  foreach ($m in $matches) {
    $line = $m.Line
    if (-not $line) { continue }
    $parts = ($line -split '\s+') | Where-Object { $_ -and $_.Trim() -ne '' }
    if ($parts.Count -ge 5) {
      $pid = $parts[-1]
      if ($pid -match '^\d+$') {
        $pids += [int]$pid
      }
    }
  }

  $pids = $pids | Sort-Object -Unique
  foreach ($pid in $pids) {
    try {
      Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    } catch {
      # ignore
    }
  }
}

$mvn = Find-MvnCmd

# 避免端口被旧进程占用
Stop-PortProcess -p $Port

$args = @('-DskipTests', 'spring-boot:run')
if ($Profile -and $Profile.Trim() -ne '') {
  $args += ("-Dspring-boot.run.profiles=" + $Profile)
}

Write-Host 'Starting backend...'
Write-Host ('mvn: ' + $mvn)
Write-Host ('stdout: ' + $stdoutLog)
Write-Host ('stderr: ' + $stderrLog)

Start-Process -FilePath $mvn -ArgumentList $args -WorkingDirectory $backendDir -RedirectStandardOutput $stdoutLog -RedirectStandardError $stderrLog -WindowStyle Hidden | Out-Null

Write-Host 'Backend started (detached).'
