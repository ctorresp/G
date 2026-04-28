package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST del microservicio auth-service.
 * Expone los endpoints de autenticación y registro.
 *
 * Base URL: /api/auth
 *
 * Endpoints públicos (no requieren JWT):
 *   POST /api/auth/login          — Login de usuario
 *   POST /api/auth/registro       — Registro de nuevo paciente
 *
 * Endpoints protegidos:
 *   POST /api/auth/admin/registro — Registro de usuario admin (solo ROLE_ADMIN)
 *   POST /api/auth/validar        — Validar token JWT (uso interno entre microservicios)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ─────────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────────

    /**
     * Inicia sesión con email o RUT + contraseña.
     * Retorna un token JWT válido si las credenciales son correctas.
     *
     * @param request body con email/rut y contraseña
     * @return 200 con LoginResponseDTO o 401 con mensaje de error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Credenciales inválidas",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    // ─────────────────────────────────────────────
    // REGISTRO PACIENTE (público)
    // ─────────────────────────────────────────────

    /**
     * Registra un nuevo paciente en el sistema.
     * Crea el usuario con ROLE_PACIENTE y su ficha de paciente.
     *
     * @param request body con datos del paciente
     * @return 201 con mensaje de éxito o 400 con mensaje de error
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarPaciente(@RequestBody RegistroRequestDTO request) {
        try {
            String mensaje = authService.registrarPaciente(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error en el registro",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    // ─────────────────────────────────────────────
    // REGISTRO ADMIN (solo ROLE_ADMIN)
    // ─────────────────────────────────────────────

    /**
     * Registra un usuario con rol administrativo (ROLE_ADMIN o ROLE_MEDICO).
     * Solo puede ser ejecutado por un usuario con ROLE_ADMIN.
     *
     * @param request body con datos del usuario
     * @param rol     parámetro de query con el nombre del rol a asignar
     * @return 201 con mensaje de éxito o 400/403 con mensaje de error
     */
    @PostMapping("/admin/registro")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> registrarAdmin(
            @RequestBody RegistroRequestDTO request,
            @RequestParam(defaultValue = "ROLE_MEDICO") String rol) {
        try {
            String mensaje = authService.registrarUsuarioAdmin(request, rol);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error en el registro de admin",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    // ─────────────────────────────────────────────
    // VALIDAR TOKEN (uso inter-microservicios)
    // ─────────────────────────────────────────────

    /**
     * Valida un token JWT y retorna la información del usuario.
     * Endpoint de uso interno: otros microservicios lo llaman para
     * verificar la autenticidad de tokens sin depender de Spring Security.
     *
     * @param authHeader header Authorization con el token "Bearer <token>"
     * @return 200 con LoginResponseDTO si el token es válido, o 401 si no lo es
     */
    @PostMapping("/validar")
    public ResponseEntity<?> validarToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Header Authorization inválido o ausente"));
            }
            String token = authHeader.substring(7);
            LoginResponseDTO response = authService.validarToken(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Token inválido o expirado",
                            "mensaje", e.getMessage()
                    ));
        }
    }
}
