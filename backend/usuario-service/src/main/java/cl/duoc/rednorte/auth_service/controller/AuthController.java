package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.ForgotPasswordRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginRequestDTO;
import cl.duoc.rednorte.auth_service.dto.LoginResponseDTO;
import cl.duoc.rednorte.auth_service.dto.RegistroRequestDTO;
import cl.duoc.rednorte.auth_service.dto.ResetPasswordRequestDTO;
import cl.duoc.rednorte.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Controlador REST público y protegido para autenticación y registro
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Inicia sesión y retorna un token JWT
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
                            "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
                    ));
        }
    }

    // Registra un nuevo paciente y su ficha médica
    @PostMapping("/registro")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
                            "mensaje", e.getMessage() != null ? e.getMessage() : "Error al guardar en base de datos"
                    ));
        }
    }

    // Registra personal administrativo (Solo accesible por ROLE_ADMIN)
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
                            "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
                    ));
        }
    }

    // Registra un médico completo (Usuario + Medico + Especialidad)
    @PostMapping("/admin/registro-medico")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> registrarMedicoCompleto(
            @RequestBody RegistroRequestDTO request,
            @RequestParam Long especialidadId) {
        try {
            String mensaje = authService.registrarMedicoCompleto(request, especialidadId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Error en el registro de médico",
                            "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
                    ));
        }
    }

    // Valida tokens externamente (uso interno para microservicios)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        try {
            var response = authService.forgotPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Error al procesar la solicitud",
                            "mensaje", e.getMessage()
                    ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            var response = authService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Error al restablecer la contraseña",
                            "mensaje", e.getMessage()
                    ));
        }
    }

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
                            "mensaje", e.getMessage() != null ? e.getMessage() : "Error desconocido"
                    ));
        }
    }
}
