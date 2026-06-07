import { Component, signal, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';

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

  pacienteRut: string = '';
  medicoNombre: string = '';
  loading = signal(true);

  solicitud = {
    fechaProgramada: '',
    notas: '',
  };

  ngAfterViewInit() {
    this.pacienteRut = this.route.snapshot.paramMap.get('rut') || '';
    this.medicoNombre = localStorage.getItem(STORAGE_KEYS.USUARIO_NOMBRE) || 'Médico';
    if (!this.pacienteRut) {
      this.toastService.mostrar('RUT de paciente no especificado', 'error');
      this.router.navigate(['/medico/pacientes']);
      return;
    }
    this.loading.set(false);
  }

  enviarSolicitud() {
    const medicoRut = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (!medicoRut) {
      this.toastService.mostrar('Error de sesión', 'error');
      return;
    }

    const payload = {
      pacienteRut: this.pacienteRut,
      medicoRut: medicoRut,
      notas: this.solicitud.notas || undefined,
      fechaProgramada: this.solicitud.fechaProgramada || null,
    };

    this.cirugiaService.solicitar(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Solicitud de cirugía enviada correctamente', 'success');
        this.router.navigate(['/medico/pacientes', this.pacienteRut]);
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al solicitar cirugía';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/medico/pacientes', this.pacienteRut]);
  }
}
