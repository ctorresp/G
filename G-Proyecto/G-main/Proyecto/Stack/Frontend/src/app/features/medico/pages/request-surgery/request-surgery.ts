import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { Paciente } from '../../../../core/interfaces';

@Component({
  selector: 'app-request-surgery',
  imports: [CommonModule, FormsModule],
  templateUrl: './request-surgery.html',
  styleUrl: './request-surgery.scss',
})
export class RequestSurgeryComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private pacienteService = inject(PacienteService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  pacientes: Paciente[] = [];

  medicoNombre = '';
  especialidadNombre = '';

  solicitudCirugia = {
    pacienteRut: '',
    especialidadId: null as number | null,
    medicoRut: '',
    fechaProgramada: '',
  };

  ngOnInit() {
    this.cargarPerfilMedico();
    this.cargarPacientes();
  }

  cargarPerfilMedico() {
    const rut = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (rut) {
      this.solicitudCirugia.medicoRut = rut;
    }
    this.authService.obtenerPerfilMedico().subscribe({
      next: (perfil) => {
        this.medicoNombre = perfil.nombre;
        this.solicitudCirugia.especialidadId = perfil.especialidadId;
        this.especialidadNombre = perfil.especialidadNombre;
      },
      error: () => {
        this.toastService.mostrar('Error al cargar perfil médico', 'error');
      },
    });
  }

  cargarPacientes() {
    this.pacienteService.listarTodos().subscribe({
      next: (data: any) => {
        const raw: any[] = Array.isArray(data) ? data : data.value ?? [];
        this.pacientes = raw;
      },
    });
  }

  solicitarCirugiaMedico() {
    const payload = {
      pacienteRut: this.solicitudCirugia.pacienteRut,
      especialidad: { idEspecialidad: this.solicitudCirugia.especialidadId! },
      medicoRut: this.solicitudCirugia.medicoRut,
      fechaProgramada: this.solicitudCirugia.fechaProgramada || null,
    };

    this.cirugiaService.solicitar(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Solicitud enviada al coordinador', 'success');
        this.router.navigate(['/medico/cirugias']);
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al solicitar cirugía';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/medico/cirugias']);
  }
}
