import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { storage } from '../../../../core/storage';
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
  private toastService = inject(ToastService);
  private authService = inject(AuthService);
  private router = inject(Router);

  pacientes: Paciente[] = [];

  medicoNombre = '';
  especialidadNombre = '';

  solicitudCirugia = {
    pacienteRut: '',
    especialidadNombre: '',
    medicoRut: '',
    fechaProgramada: '',
  };

  ngOnInit() {
    const rut = storage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (rut) {
      this.solicitudCirugia.medicoRut = rut;
    }
    this.medicoNombre = storage.getItem(STORAGE_KEYS.USUARIO_NOMBRE) || '';
    this.especialidadNombre = storage.getItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_NOMBRE) || '';
    this.solicitudCirugia.especialidadNombre = this.especialidadNombre;
    this.cargarPacientes();
    this.cargarEspecialidadFallback();
  }

  private cargarEspecialidadFallback() {
    if (this.especialidadNombre) return;
    this.authService.obtenerPerfilMedico().subscribe({
      next: (res) => {
        const nombre = res?.especialidad?.nombre || res?.especialidadNombre || '';
        if (nombre) {
          this.especialidadNombre = nombre;
          this.solicitudCirugia.especialidadNombre = nombre;
          storage.setItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_NOMBRE, nombre);
        }
      },
      error: () => {},
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
      especialidad: { nombre: this.solicitudCirugia.especialidadNombre },
      medicoRut: this.solicitudCirugia.medicoRut,
      fechaProgramada: this.solicitudCirugia.fechaProgramada || null,
    };

    this.cirugiaService.solicitar(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Solicitud enviada al coordinador', 'success');
        this.router.navigate(['/medico/cirugias']);
      },
      error: (err) => {
        const mensaje = err.error?.message || err.error?.mensaje || 'Error al solicitar cirugía';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/medico/cirugias']);
  }
}
