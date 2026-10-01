param([switch]$BackendOnly)

$ErrorActionPreference = 'Stop'
$backendDirectory = Join-Path $PSScriptRoot 'C#\FitnessApp'

$dockerCommand = Get-Command docker -ErrorAction SilentlyContinue
$dockerExecutable = if ($dockerCommand) { $dockerCommand.Source } else {
    $candidates = @(
        (Join-Path $env:LOCALAPPDATA 'Programs\DockerDesktop\resources\bin\docker.exe'),
        (Join-Path $env:ProgramFiles 'Docker\Docker\resources\bin\docker.exe')
    )
    $candidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
}
if (!$dockerExecutable) { throw 'Docker Desktop nu este instalat sau nu poate fi gasit.' }
if (!(Test-Path -LiteralPath (Join-Path $backendDirectory '.env'))) {
    throw 'Copiaza C#\FitnessApp\.env.example in .env si completeaza setarile SQL, JWT si SMTP.'
}

Push-Location $backendDirectory
try {
    & $dockerExecutable compose up -d --build --wait --wait-timeout 180
    if ($LASTEXITCODE -ne 0) { throw 'Docker nu a pornit complet. Verifica Docker Desktop si docker compose logs api db.' }
    $health = Invoke-RestMethod 'http://127.0.0.1:5068/api/health' -TimeoutSec 10
    if ($health.api -ne 'ok' -or $health.database -ne 'ok') { throw 'API-ul sau baza de date nu raspunde corect.' }
    Write-Host 'Backend si SQL Server: OK.'
} finally { Pop-Location }

if ($BackendOnly) { return }

$adbCommand = Get-Command adb -ErrorAction SilentlyContinue
$adbExecutable = if ($adbCommand) { $adbCommand.Source } else {
    $sdkDirectory = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
    Join-Path $sdkDirectory 'platform-tools\adb.exe'
}
if (!(Test-Path -LiteralPath $adbExecutable)) {
    throw 'Nu gasesc adb. Configureaza ANDROID_HOME sau adauga Android SDK platform-tools in PATH.'
}
$deviceOutput = & $adbExecutable devices
if ($LASTEXITCODE -ne 0) { throw 'Nu pot citi dispozitivele Android conectate.' }
$connectedDevices = @($deviceOutput | ForEach-Object {
    if ($_ -match '^(\S+)\s+device$') { $Matches[1] }
})
if ($connectedDevices.Count -eq 0) {
    throw 'Conecteaza telefonul in Android Studio prin Wireless debugging sau USB, apoi ruleaza din nou scriptul.'
}
foreach ($deviceSerial in $connectedDevices) {
    & $adbExecutable -s $deviceSerial reverse tcp:5068 tcp:5068
    if ($LASTEXITCODE -ne 0) { throw "Nu pot conecta dispozitivul $deviceSerial la backend." }
}
Write-Host 'Telefon/emulator conectat la backend. Ruleaza varianta debug a aplicatiei din Android Studio.'
Write-Host 'Ruleaza din nou acest script dupa reconectarea telefonului sau repornirea calculatorului.'
