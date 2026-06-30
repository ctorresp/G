import { Component, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../../core/services/auth.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { storage } from '../../../../core/storage';

@Component({
  selector: 'app-patient-request-surgery',
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-request-surgery.html',
  styleUrl: './patient-request-surgery.scss',
})
export class PatientRequestSurgeryComponent implements AfterViewInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cirugiaService = inject(CirugiaService);
  private toastService = inject(ToastService);
  private authService = inject(AuthService);

  pacienteRut: string = '';
  medicoNombre: string = '';
  especialidadNombre: string = '';

  solicitud = {
    fechaProgramada: '',
  };

  ngAfterViewInit() {
    this.pacienteRut = this.route.snapshot.paramMap.get('rut') || '';
    this.medicoNombre = storage.getItem(STORAGE_KEYS.USUARIO_NOMBRE) || 'Médico';
    if (!this.pacienteRut) {
      this.toastService.mostrar('RUT de paciente no especificado', 'error');
      this.router.navigate(['/medico/pacientes']);
      return;
    }
    this.especialidadNombre = storage.getItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_NOMBRE) || '';
    this.cargarEspecialidadFallback();
  }

  private cargarEspecialidadFallback() {
    if (this.especialidadNombre) return;
    this.authService.obtenerPerfilMedico().subscribe({
      next: (res) => {
        const nombre = res?.especialidad?.nombre || res?.especialidadNombre || '';
        if (nombre) {
          this.especialidadNombre = nombre;
          storage.setItem(STORAGE_KEYS.USUARIO_ESPECIALIDAD_NOMBRE, nombre);
        }
      },
      error: () => {},
    });
  }

  enviarSolicitud() {
    const medicoRut = storage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (!medicoRut) {
      this.toastService.mostrar('Error de sesión', 'error');
      return;
    }

    const payload = {
      pacienteRut: this.pacienteRut,
      especialidad: { nombre: this.especialidadNombre },
      medicoRut: medicoRut,
      fechaProgramada: this.solicitud.fechaProgramada || null,
    };

    this.cirugiaService.solicitar(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Solicitud de cirugía enviada correctamente', 'success');
        this.router.navigate(['/medico/pacientes', this.pacienteRut], {
          state: { cirugiaSolicitada: true }
        });
      },
      error: (err) => {
        const mensaje = err.error?.message || err.error?.mensaje || 'Error al solicitar cirugía';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/medico/pacientes', this.pacienteRut]);
  }
}
