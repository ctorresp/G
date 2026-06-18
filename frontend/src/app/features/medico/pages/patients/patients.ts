import { Component, signal, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { storage } from '../../../../core/storage';

@Component({
  selector: 'app-patients',
  imports: [CommonModule],
  templateUrl: './patients.html',
  styleUrl: './patients.scss',
})
export class PatientsComponent implements AfterViewInit {
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  pacientes = signal<any[]>([]);
  loading = signal(false);

  ngAfterViewInit() {
    this.cargarPacientes();
  }

  cargarPacientes() {
    this.loading.set(true);
    const rutMedico = storage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (rutMedico) {
      this.pacienteService.listarPorMedicoRut(rutMedico).subscribe({
        next: (data) => {
          this.pacientes.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.toastService.mostrar('Error al cargar pacientes', 'error');
          this.loading.set(false);
        },
      });
    } else {
      const idMedico = Number(storage.getItem(STORAGE_KEYS.USUARIO_ID));
      if (!idMedico) {
        this.toastService.mostrar('No se pudo identificar al médico', 'error');
        this.loading.set(false);
        return;
      }
      this.pacienteService.listarPorMedico(idMedico).subscribe({
        next: (data) => {
          this.pacientes.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.toastService.mostrar('Error al cargar pacientes', 'error');
          this.loading.set(false);
        },
      });
    }
  }

  verDetalle(rut: string) {
    this.router.navigate(['/medico/pacientes', rut]);
  }

  solicitarCirugia(rut: string) {
    this.router.navigate(['/medico/pacientes', rut, 'solicitar-cirugia']);
  }
}
