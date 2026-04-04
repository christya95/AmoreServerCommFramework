# Build AmoreServerCommCore.jar — same classpath contract as JossDoubleJump (HytaleServer.jar + Hyxin + JDK 25).
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent

function Get-ServerRoot([string]$repoRoot) {
  $custom = $env:PEBBLE_SERVER_ROOT
  if (-not [string]::IsNullOrWhiteSpace($custom) -and (Test-Path (Join-Path $custom "HytaleServer.jar"))) {
    return $custom
  }
  $parent = Split-Path $repoRoot -Parent
  $candidates = @(
    (Join-Path $parent "PebbleHotServerRoot"),
    (Join-Path $parent "PebbleHostServerRoot"),
    $parent,
    $repoRoot
  )
  foreach ($d in $candidates) {
    if ([string]::IsNullOrWhiteSpace($d)) { continue }
    if (Test-Path (Join-Path $d "HytaleServer.jar")) { return $d }
  }
  throw "Could not find HytaleServer.jar. Set PEBBLE_SERVER_ROOT."
}

$serverRoot = Get-ServerRoot $root
$sourcesDir = Join-Path $root "src\main\java"
$outClasses = Join-Path $root "build\classes"
$jdkHome = Get-ChildItem (Join-Path $root "..\Hytale_JossDoubleJump\tools\jdk25") -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $jdkHome) {
  $jdkHome = Get-ChildItem (Join-Path $root "tools\jdk25") -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
}
if (-not $jdkHome) { throw "JDK 25 not found under tools\jdk25 (JossDoubleJump repo or local)." }
$javac = Join-Path $jdkHome.FullName "bin\javac.exe"
$jar = Join-Path $jdkHome.FullName "bin\jar.exe"
$hy = Join-Path $serverRoot "HytaleServer.jar"
$hyxinJar = Get-ChildItem -Path (Join-Path $serverRoot "earlyplugins"), (Join-Path $serverRoot "mods") -Filter "Hyxin*.jar" -File -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName

if (-not (Test-Path $hy)) { throw "Missing HytaleServer.jar at $hy" }
if (-not $hyxinJar) { throw "Hyxin JAR not found next to HytaleServer.jar." }
if (-not (Test-Path $sourcesDir)) { throw "Missing sources: $sourcesDir" }

Remove-Item -Recurse -Force $outClasses -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $outClasses | Out-Null

$cp = "$hy;$hyxinJar"
$javaFiles = Get-ChildItem -Path $sourcesDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
& $javac -encoding UTF-8 -cp $cp -d $outClasses @javaFiles
if ($LASTEXITCODE -ne 0) { throw "javac failed ($LASTEXITCODE)." }

$manifestDir = Join-Path $root "build\manifest"
New-Item -ItemType Directory -Force -Path $manifestDir | Out-Null
@"
Manifest-Version: 1.0
Created-By: AmoreServerCommFramework

"@ | Set-Content -Path (Join-Path $manifestDir "MANIFEST.MF") -Encoding ASCII

$outJar = Join-Path $root "dist\AmoreServerCommCore.jar"
New-Item -ItemType Directory -Force -Path (Split-Path $outJar -Parent) | Out-Null
if (Test-Path $outJar) { Remove-Item -Force $outJar }
Push-Location $outClasses
& $jar cfm $outJar (Join-Path $manifestDir "MANIFEST.MF") -C . .
Pop-Location
Write-Host "Built: $outJar"
