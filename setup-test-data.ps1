$apiUrl = "http://localhost:8080/api"
$ErrorActionPreference = "Stop"

Write-Host "===== REDNORTE - SETUP DATOS DE PRUEBA (REASIGNACION) =====" -ForegroundColor Cyan
Write-Host ""

# 1. Login como admin
Write-Host "[1/6] Login como admin..." -ForegroundColor Yellow
$loginBody = @{ email = "admin@rednorte.cl"; contrasena = "Admin1234!" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$apiUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$adminToken = $loginResponse.token
Write-Host "  OK - Token obtenido: $($adminToken.Substring(0, 20))..." -ForegroundColor Green

# Headers para requests autenticadas
$headers = @{ Authorization = "Bearer $adminToken" }

# 2. Obtener especialidades para confirmar ID de Traumatología
Write-Host "[2/6] Verificando especialidades..." -ForegroundColor Yellow
$especialidades = Invoke-RestMethod -Uri "$apiUrl/usuarios/especialidades" -Headers $headers
$traumaEsp = $especialidades | Where-Object { $_.nombre -eq "Traumatología" }
if (-not $traumaEsp) {
    Write-Host "  ERROR: No se encontró especialidad Traumatología" -ForegroundColor Red
    exit 1
}
$traumaId = $traumaEsp.idEspecialidad
Write-Host "  OK - Traumatología ID = $traumaId" -ForegroundColor Green

# 3. Registrar nuevo médico con Traumatología (si no existe)
Write-Host "[3/6] Registrando médico Traumatología..." -ForegroundColor Yellow
$medicoBody = @{
    rut = "77777777-7"
    nombre = "Dr. Traumatologo"
    email = "trauma@rednorte.cl"
    contrasena = "Trauma1234!"
} | ConvertTo-Json
try {
    $medicoResponse = Invoke-RestMethod -Uri "$apiUrl/auth/admin/registro-medico?especialidadId=$traumaId" -Method Post -Body $medicoBody -ContentType "application/json" -Headers $headers
    Write-Host "  $medicoResponse" -ForegroundColor Green
} catch {
    Write-Host "  Ya existe (se reutiliza)" -ForegroundColor Yellow
}

# Obtener el ID del nuevo médico
$usuarios = Invoke-RestMethod -Uri "$apiUrl/admin/usuarios" -Headers $headers
$nuevoMedico = $usuarios | Where-Object { $_.rut -eq "77777777-7" }
$nuevoMedicoId = $nuevoMedico.idUsuario
Write-Host "  ID del nuevo médico: $nuevoMedicoId" -ForegroundColor Green

# 4-6. Registrar los 3 pacientes
Write-Host "[4/6] Registrando Paciente A - Carlos Muñoz (FONASA)..." -ForegroundColor Yellow
$pacienteABody = @{
    rut = "44444444-4"
    nombre = "Carlos Munoz"
    email = "carlos@test.cl"
    prevision = "FONASA"
    grupoSanguineo = "O+"
    pesoKg = 78.5
    alturaCm = 172.0
    alergias = "ninguna"
    contactoEmergenciaNombre = "Maria Munoz"
    contactoEmergenciaTelefono = "912345678"
    idMedico = $nuevoMedicoId
} | ConvertTo-Json
try { $respA = Invoke-RestMethod -Uri "$apiUrl/auth/registro" -Method Post -Body $pacienteABody -ContentType "application/json" -Headers $headers; Write-Host "  OK - $respA" -ForegroundColor Green } catch { Write-Host "  Ya existe (se reutiliza)" -ForegroundColor Yellow }

Write-Host "[5/6] Registrando Paciente B - Ana López (ISAPRE)..." -ForegroundColor Yellow
$pacienteBBody = @{
    rut = "55555555-5"
    nombre = "Ana Lopez"
    email = "ana@test.cl"
    prevision = "ISAPRE"
    grupoSanguineo = "A+"
    pesoKg = 62.0
    alturaCm = 165.0
    alergias = "penicilina"
    contactoEmergenciaNombre = "Pedro Lopez"
    contactoEmergenciaTelefono = "998765432"
    idMedico = $nuevoMedicoId
} | ConvertTo-Json
try { $respB = Invoke-RestMethod -Uri "$apiUrl/auth/registro" -Method Post -Body $pacienteBBody -ContentType "application/json" -Headers $headers; Write-Host "  OK - $respB" -ForegroundColor Green } catch { Write-Host "  Ya existe (se reutiliza)" -ForegroundColor Yellow }

Write-Host "[6/6] Registrando Paciente C - Roberto Díaz (FONASA)..." -ForegroundColor Yellow
$pacienteCBody = @{
    rut = "66666666-6"
    nombre = "Roberto Diaz"
    email = "roberto@test.cl"
    prevision = "FONASA"
    grupoSanguineo = "B+"
    pesoKg = 85.0
    alturaCm = 180.0
    enfermedadesCronicas = "diabetes tipo 2"
    contactoEmergenciaNombre = "Laura Diaz"
    contactoEmergenciaTelefono = "955566677"
    idMedico = $nuevoMedicoId
} | ConvertTo-Json
try { $respC = Invoke-RestMethod -Uri "$apiUrl/auth/registro" -Method Post -Body $pacienteCBody -ContentType "application/json" -Headers $headers; Write-Host "  OK - $respC" -ForegroundColor Green } catch { Write-Host "  Ya existe (se reutiliza)" -ForegroundColor Yellow }

# 7. Resumen final
Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  DATOS CREADOS EXITOSAMENTE" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "NUEVO MEDICO (Traumatología):" -ForegroundColor Yellow
Write-Host "  Email:    trauma@rednorte.cl"
Write-Host "  Password: Trauma1234!"
Write-Host "  RUT:      77777777-7"
Write-Host "  Nombre:   Dr. Traumatologo"
Write-Host ""
Write-Host "PACIENTES (asignados al nuevo médico):" -ForegroundColor Yellow
Write-Host "  A - Carlos Munoz  | 44444444-4 | FONASA"
Write-Host "  B - Ana Lopez     | 55555555-5 | ISAPRE"
Write-Host "  C - Roberto Diaz  | 66666666-6 | FONASA"
Write-Host ""
Write-Host "FLUJO MANUAL EN FRONTEND (http://localhost:4200):" -ForegroundColor Cyan
Write-Host ""
Write-Host "  1. Login como Dr. Traumatologo (trauma@rednorte.cl / Trauma1234!)"
Write-Host "  2. Ir a 'Mis Pacientes' -> ver los 3 pacientes"
Write-Host "  3. Para CADA paciente: 'Solicitar Cirugía' (Traumatología)"
Write-Host "     (asigna distintos niveles de prioridad al hacer triaje)"
Write-Host "  4. Login como Triajer (triajer@rednorte.cl / Triajer1234!)"
Write-Host "  5. Ir a 'Solicitudes Pendientes' -> hacer triaje a cada una"
Write-Host "  6. Login como Coordinador (coordinador@rednorte.cl / Coordi1234!)"
Write-Host "  7. Ir a 'Solicitudes Pendientes' -> ver lista de espera"
Write-Host "  8. Programar 1 cirugía en un pabellón"
Write-Host "  9. Ir a 'Métricas' -> marcar 'No Apto' -> ver reasignación automatica"
Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
