Param(
  [string]$ProjectRoot = (Resolve-Path "$PSScriptRoot\..\").Path
)

# 统一控制台输出编码为 UTF-8（避免中文乱码）
try {
  [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
  $OutputEncoding = [Console]::OutputEncoding
} catch {
  # 忽略：某些终端/宿主可能不允许设置
}

# 统一把本项目需要的工具指到工作区，避免受系统环境影响。
$JdkBin = Join-Path $ProjectRoot ".devtools\jdk\jdk-17.0.16\bin"
$MavenBin = Join-Path $ProjectRoot ".devtools\maven\maven-3.9.12\bin"

$env:JAVA_HOME = (Split-Path $JdkBin -Parent)
$env:Path = "$JdkBin;$MavenBin;" + $env:Path

$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"

Write-Host "[CloudClassroom] JAVA_HOME=$env:JAVA_HOME"
Write-Host "[CloudClassroom] PATH injected: JDK17 + Maven"
