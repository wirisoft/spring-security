# Script para generar claves JWT y convertirlas a Base64 para Render
# Uso: .\generar-claves-jwt.ps1

Write-Host "🔐 Generando claves JWT..." -ForegroundColor Cyan

# Crear directorio si no existe
$jwtDir = "src/main/resources/jwtKeys"
if (-not (Test-Path $jwtDir)) {
    New-Item -ItemType Directory -Force -Path $jwtDir | Out-Null
    Write-Host "✅ Directorio creado: $jwtDir" -ForegroundColor Green
}

# Verificar si openssl está disponible
$opensslAvailable = Get-Command openssl -ErrorAction SilentlyContinue
if (-not $opensslAvailable) {
    Write-Host "❌ OpenSSL no está disponible. Instálalo o usa WSL." -ForegroundColor Red
    Write-Host "💡 Alternativa: Usa WSL o instala OpenSSL para Windows" -ForegroundColor Yellow
    exit 1
}

# Generar clave privada
Write-Host "📝 Generando clave privada..." -ForegroundColor Yellow
$privateKeyPath = Join-Path $jwtDir "private_key.pem"
openssl genpkey -algorithm RSA -out $privateKeyPath -pkeyopt rsa_keygen_bits:2048

if (Test-Path $privateKeyPath) {
    Write-Host "✅ Clave privada generada: $privateKeyPath" -ForegroundColor Green
} else {
    Write-Host "❌ Error al generar clave privada" -ForegroundColor Red
    exit 1
}

# Generar clave pública
Write-Host "📝 Generando clave pública..." -ForegroundColor Yellow
$publicKeyPath = Join-Path $jwtDir "public_key.pem"
openssl rsa -pubout -in $privateKeyPath -out $publicKeyPath

if (Test-Path $publicKeyPath) {
    Write-Host "✅ Clave pública generada: $publicKeyPath" -ForegroundColor Green
} else {
    Write-Host "❌ Error al generar clave pública" -ForegroundColor Red
    exit 1
}

# Convertir a Base64
Write-Host "`n🔐 Convirtiendo claves a Base64 para Render..." -ForegroundColor Cyan
Write-Host "`n" + "="*80 -ForegroundColor Gray

$privateKeyBytes = [System.IO.File]::ReadAllBytes($privateKeyPath)
$publicKeyBytes = [System.IO.File]::ReadAllBytes($publicKeyPath)

$privateKeyB64 = [Convert]::ToBase64String($privateKeyBytes)
$publicKeyB64 = [Convert]::ToBase64String($publicKeyBytes)

Write-Host "`n📋 Variables de entorno para Render:" -ForegroundColor Green
Write-Host "`n" + "-"*80 -ForegroundColor Gray
Write-Host "JWT_PRIVATE_KEY_B64" -ForegroundColor Yellow
Write-Host $privateKeyB64 -ForegroundColor White
Write-Host "`n" + "-"*80 -ForegroundColor Gray
Write-Host "JWT_PUBLIC_KEY_B64" -ForegroundColor Yellow
Write-Host $publicKeyB64 -ForegroundColor White
Write-Host "`n" + "="*80 -ForegroundColor Gray

Write-Host "`n✅ Claves generadas exitosamente!" -ForegroundColor Green
Write-Host "`n📝 Instrucciones:" -ForegroundColor Cyan
Write-Host "1. Copia los valores de JWT_PRIVATE_KEY_B64 y JWT_PUBLIC_KEY_B64" -ForegroundColor White
Write-Host "2. Ve a Render Dashboard → Tu Web Service → Environment" -ForegroundColor White
Write-Host "3. Agrega estas dos variables de entorno" -ForegroundColor White
Write-Host "4. Guarda y reinicia el servicio" -ForegroundColor White

# Guardar en archivo para referencia
$outputFile = "claves-jwt-base64.txt"
@"
JWT_PRIVATE_KEY_B64
$privateKeyB64

JWT_PUBLIC_KEY_B64
$publicKeyB64
"@ | Out-File -FilePath $outputFile -Encoding UTF8

Write-Host "`n💾 Valores guardados en: $outputFile" -ForegroundColor Green
Write-Host "⚠️  IMPORTANTE: No subas este archivo a Git!" -ForegroundColor Red

