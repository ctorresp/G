$apiUrl = "http://localhost:8080/api"
$nginxUrl = "http://localhost:4200/api"
$ErrorActionPreference = "Stop"

Write-Host "===== SETUP COMPLETO PARA PRUEBA DE REASIGNACION =====" -ForegroundColor Cyan
Write-Host ""

# 1. Login como admin
Write-Host "[1/8] Login usuarios..." -ForegroundColor Yellow
$adminBody = @{ email = "admin@rednorte.cl"; contrasena = "Admin1234!" } | ConvertTo-Json
$adminToken = (Invoke-RestMethod -Uri "$apiUrl/auth/login" -Method Post -Body $adminBody -ContentType "application/json").token
$adminHeaders = @{ Authorization = "Bearer $adminToken" }

# Login as Dr. Traumatologo
$traumaBody = @{ email = "trauma@rednorte.cl"; contrasena = "Trauma1234!" } | ConvertTo-Json
$traumaLogin = Invoke-RestMethod -Uri "$apiUrl/auth/login" -Method Post -Body $traumaBody -ContentType "application/json"
$traumaToken = $traumaLogin.token
$traumaRut = $traumaLogin.rut
$traumaHeaders = @{ Authorization = "Bearer $traumaToken" }

# Login as Triajer
$triajerBody = @{ email = "triajer@rednorte.cl"; contrasena = "Triajer1234!" } | ConvertTo-Json
$triajerLogin = Invoke-RestMethod -Uri "$apiUrl/auth/login" -Method Post -Body $triajerBody -ContentType "application/json"
$triajerToken = $triajerLogin.token
$triajerRut = $triajerLogin.rut
$triajerHeaders = @{ Authorization = "Bearer $triajerToken" }

# Login as Coordinador
$coordBody = @{ email = "coordinador@rednorte.cl"; contrasena = "Coordi1234!" } | ConvertTo-Json
$coordLogin = Invoke-RestMethod -Uri "$apiUrl/auth/login" -Method Post -Body $coordBody -ContentType "application/json"
$coordToken = $coordLogin.token
$coordRut = $coordLogin.rut
$coordHeaders = @{ Authorization = "Bearer $coordToken" }

Write-Host "  OK - Todos los tokens obtenidos" -ForegroundColor Green

# 2. Limpiar datos existentes en pabellon
Write-Host "[2/8] Limpiando datos previos en pabellon-service..." -ForegroundColor Yellow
try {
  Invoke-RestMethod -Uri "$nginxUrl/pabellon/cirugias" -Headers $coordHeaders -ErrorAction SilentlyContinue | Out-Null
  docker exec rednorte-db-pabellon psql -U rednorte -d db_pabellon -c "DELETE FROM reasignaciones; DELETE FROM triajes; DELETE FROM cirugias; UPDATE pabellones SET estado = 'DISPONIBLE';" 2>&1 | Out-Null
  Write-Host "  OK - Datos limpiados" -ForegroundColor Green
} catch {
  Write-Host "  OK - (sin datos previos)" -ForegroundColor Green
}

# 3. Crear solicitudes de cirugia (como Dr. Traumatologo)
Write-Host "[3/8] Creando solicitudes de cirugia..." -ForegroundColor Yellow
$pacientes = @(
  @{ rut = "66666666-6"; nombre = "Roberto Diaz"; prioridad = 1 },
  @{ rut = "55555555-5"; nombre = "Ana Lopez"; prioridad = 2 },
  @{ rut = "44444444-4"; nombre = "Carlos Munoz"; prioridad = 3 }
)

$cirugias = @()
foreach ($pac in $pacientes) {
  $body = @{
    pacienteRut = $pac.rut
    especialidad = @{ idEspecialidad = 2 }
    medicoRut = $traumaRut
    fechaProgramada = $null
  } | ConvertTo-Json -Depth 10

  try {
    $resp = Invoke-RestMethod -Uri "$nginxUrl/pabellon/cirugias/solicitar" -Method Post -Body $body -ContentType "application/json" -Headers $traumaHeaders
    $cirugias += $resp
    Write-Host "  OK - Solicitud $($pac.nombre) -> ID $($resp.idCirugia)" -ForegroundColor Green
  } catch {
    Write-Host "  Fallo solicitud $($pac.nombre): $($_.Exception.Message)" -ForegroundColor Red
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    Write-Host "  Body: $($reader.ReadToEnd())" -ForegroundColor Red
  }
}

# 4. Obtener RUT del triajer
Write-Host "[4/8] Obteniendo RUT del triajer..." -ForegroundColor Yellow
try {
  $triajerPerfil = Invoke-RestMethod -Uri "$apiUrl/auth/usuario/medico/perfil" -Headers $triajerHeaders -ErrorAction SilentlyContinue
  if (-not $triajerPerfil) { throw "No profile" }
} catch {
  # El triajer no es medico, obtener RUT desde usuarios
  $triajerInfo = Invoke-RestMethod -Uri "$apiUrl/admin/usuarios" -Headers $adminHeaders | Where-Object { $_.email -eq "triajer@rednorte.cl" }
  $triajerRut = $triajerInfo.rut
}
Write-Host "  OK - Triajer RUT: $triajerRut" -ForegroundColor Green

# 5. Crear triaje para cada cirugia y marcarla como completada
Write-Host "[5/8] Realizando triaje de cada solicitud..." -ForegroundColor Yellow
$niveles = @(1, 2, 3) # 1=urgente, 2=medio, 3=bajo
for ($i = 0; $i -lt $cirugias.Count; $i++) {
  $cirugia = $cirugias[$i]
  $pac = $pacientes[$i]

  $triajeBody = @{
    pacienteRut = $cirugia.pacienteRut
    triajerRut = $triajerRut
    nivelUrgencia = $niveles[$i]
    sintomas = "Dolor en articulaciones - $($pac.nombre)"
    presionArterial = "120/80"
    frecuenciaCardiaca = 75
    temperatura = 36.5
    observaciones = "Paciente evaluado para cirugia de traumatologia"
    fechaTriaje = (Get-Date -Format "yyyy-MM-ddTHH:mm:ss")
  } | ConvertTo-Json

  try {
    $triajeResp = Invoke-RestMethod -Uri "$nginxUrl/pabellon/triajes" -Method Post -Body $triajeBody -ContentType "application/json" -Headers $triajerHeaders
    Write-Host "  OK - Triaje $($pac.nombre) (nivel $($niveles[$i])) creado" -ForegroundColor Green
  } catch {
    Write-Host "  Fallo triaje $($pac.nombre): $($_.Exception.Message)" -ForegroundColor Red
    continue
  }

  # Marcar triaje como completado en la cirugia
  try {
    $null = Invoke-RestMethod -Uri "$nginxUrl/pabellon/cirugias/$($cirugia.idCirugia)/triaje-completado" -Method Put -Headers $triajerHeaders
    Write-Host "  OK - Triaje $($pac.nombre) marcado completado" -ForegroundColor Green
  } catch {
    Write-Host "  Fallo marcar triaje $($pac.nombre): $($_.Exception.Message)" -ForegroundColor Red
  }
}

# 6. Verificar lista de espera
Write-Host "[6/8] Verificando lista de espera..." -ForegroundColor Yellow
try {
  $listaEspera = Invoke-RestMethod -Uri "$nginxUrl/pabellon/cirugias/lista-espera" -Headers $coordHeaders
  Write-Host "  Pacientes en espera: $($listaEspera.Count)" -ForegroundColor Green
  foreach ($le in $listaEspera) {
    Write-Host "    - $($le.pacienteRut) | Nivel urgencia: $($le.nivelUrgencia) | Fecha: $($le.fechaSolicitud)" -ForegroundColor Gray
  }
} catch {
  Write-Host "  Fallo al obtener lista de espera: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. Programar la cirugia de mayor prioridad (Roberto - nivel 1)
Write-Host "[7/8] Programando cirugia de mayor prioridad..." -ForegroundColor Yellow
$cirugiaAProgramar = $cirugias[0] # Roberto Diaz - nivel 1
$pabellones = Invoke-RestMethod -Uri "$nginxUrl/pabellon/pabellones" -Headers $coordHeaders
$pabellonId = $pabellones[0].idPabellon
$programarBody = @{
  pabellonId = $pabellonId
  fechaProgramada = (Get-Date).AddDays(1).ToString("yyyy-MM-dd")
  horaInicio = "08:00"
  horaFin = "10:00"
} | ConvertTo-Json

try {
  $progResp = Invoke-RestMethod -Uri "$nginxUrl/pabellon/cirugias/$($cirugiaAProgramar.idCirugia)/programar" -Method Put -Body $programarBody -ContentType "application/json" -Headers $coordHeaders
  Write-Host "  OK - Programada: $($cirugiaAProgramar.pacienteRut) en Pabellon ID $pabellonId" -ForegroundColor Green
} catch {
  Write-Host "  Fallo programacion: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. Resumen final
Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  SETUP COMPLETADO EXITOSAMENTE" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "DATOS CREADOS:" -ForegroundColor Yellow
Write-Host "  1 cirugia PROGRAMADA (Roberto Diaz - Pabellon ID $pabellonId)" -ForegroundColor White
Write-Host "  2 cirugias en LISTA DE ESPERA (Ana Lopez - nivel 2, Carlos Munoz - nivel 3)" -ForegroundColor White
Write-Host ""
Write-Host "FLUJO DE PRUEBA (http://localhost:4200):" -ForegroundColor Cyan
Write-Host "  1. Login como COORDINADOR (coordinador@rednorte.cl / Coordi1234!)" -ForegroundColor Yellow
Write-Host "  2. Ir a 'Métricas de Cirugía'" -ForegroundColor Yellow
Write-Host "  3. Buscar la cirugía PROGRAMADA de Roberto Diaz" -ForegroundColor Yellow
Write-Host "  4. Click 'No Apto'" -ForegroundColor Yellow
Write-Host "  5. En el modal: ver que Ana Lopez (nivel 2) es la siguiente en espera" -ForegroundColor Yellow
Write-Host "  6. Click 'Confirmar No Apto y Reasignar'" -ForegroundColor Yellow
Write-Host "  7. Verificar que: Roberto queda cancelado, Ana recibe el slot" -ForegroundColor Yellow
Write-Host "  8. Volver a Métricas -> deberia aparecer Ana Lopez PROGRAMADA" -ForegroundColor Yellow
Write-Host "  9. Opcional: repetir No Apto con Ana para reasignar a Carlos" -ForegroundColor Yellow
Write-Host ""
Write-Host "CREDENCIALES:" -ForegroundColor Magenta
Write-Host "  Admin:       admin@rednorte.cl / Admin1234!" -ForegroundColor Gray
Write-Host "  Dr. Trauma:  trauma@rednorte.cl / Trauma1234!" -ForegroundColor Gray
Write-Host "  Triajer:     triajer@rednorte.cl / Triajer1234!" -ForegroundColor Gray
Write-Host "  Coordinador: coordinador@rednorte.cl / Coordi1234!" -ForegroundColor Gray
Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
