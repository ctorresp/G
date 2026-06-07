import { Component, signal, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-patient-detail',
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.scss',
})
export class PatientDetailComponent implements AfterViewInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  paciente = signal<any>(null);
  loading = signal(true);
  observacion: string = '';
  guardando = false;

  ngAfterViewInit() {
    const rut = this.route.snapshot.paramMap.get('rut');
    if (rut) {
      this.cargarPaciente(rut);
    }
  }

  cargarPaciente(rut: string) {
    this.loading.set(true);
    this.pacienteService.obtenerPorRut(rut).subscribe({
      next: (data) => {
        this.paciente.set(data);
        this.observacion = data.observacionMedico || '';
        this.loading.set(false);
      },
      error: () => {
        this.toastService.mostrar('Error al cargar paciente', 'error');
        this.loading.set(false);
      },
    });
  }

  guardarObservacion() {
    const p = this.paciente();
    if (!p?.rut) return;
    this.guardando = true;
    this.pacienteService.actualizarObservacion(p.rut, this.observacion).subscribe({
      next: () => {
        this.toastService.mostrar('Observación guardada', 'success');
        this.guardando = false;
      },
      error: () => {
        this.toastService.mostrar('Error al guardar observación', 'error');
        this.guardando = false;
      },
    });
  }

  solicitarCirugia() {
    this.router.navigate(['/medico/pacientes', this.paciente().rut, 'solicitar-cirugia']);
  }

  volver() {
    this.router.navigate(['/medico/pacientes']);
  }
}
