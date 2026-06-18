package cl.duoc.rednorte.auth_service.controller;

import cl.duoc.rednorte.auth_service.dto.MedicoUpdateRequestDTO;
import cl.duoc.rednorte.auth_service.model.Rol;
import cl.duoc.rednorte.usuarios.model.Usuario;
import cl.duoc.rednorte.auth_service.service.AdminMedicoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMedicoControllerTest {

    @Mock private AdminMedicoService adminMedicoService;
    @InjectMocks private AdminMedicoController adminMedicoController;

    @Test
    void listarMedicos_deberiaRetornarLista() {
        Rol rol = new Rol();
        rol.setNombreRol("ROLE_MEDICO");
        Usuario medico = new Usuario();
        medico.setIdUsuario(1L);
        medico.setNombre("Dr. Test");
        medico.setEmail("test@test.cl");
        medico.setRol(rol);

        when(adminMedicoService.listarMedicos()).thenReturn(List.of(medico));

        ResponseEntity<List<Usuario>> response = adminMedicoController.listarMedicos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void actualizarMedico_conExito_deberiaRetornar200() {
        MedicoUpdateRequestDTO request = new MedicoUpdateRequestDTO();
        request.setNombre("Dr. Actualizado");

        Usuario actualizado = new Usuario();
        actualizado.setIdUsuario(1L);
        actualizado.setNombre("Dr. Actualizado");

        when(adminMedicoService.actualizarMedico(1L, request)).thenReturn(actualizado);

        ResponseEntity<?> response = adminMedicoController.actualizarMedico(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void actualizarMedico_conError_deberiaRetornar400() {
        MedicoUpdateRequestDTO request = new MedicoUpdateRequestDTO();
        when(adminMedicoService.actualizarMedico(999L, request))
                .thenThrow(new RuntimeException("Médico no encontrado"));

        ResponseEntity<?> response = adminMedicoController.actualizarMedico(999L, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
