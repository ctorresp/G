import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Paciente, PacienteClinicosPayload } from '../../../../core/interfaces';

@Component({
  selector: 'app-patient-detail',
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.scss',
})
export class PatientDetailComponent {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  paciente: Paciente | null = null;
  loading = signal(true);
  observacion = '';
  guardando = false;

  modoEdicion = false;

  alergiasEditList: string[] = [];
  enfermedadesEditList: string[] = [];
  nuevaAlergia = '';
  nuevaEnfermedad = '';

  erroresEdicion = { pesoKg: '', alturaCm: '' };

  constructor() {
    const rut = this.route.snapshot.paramMap.get('rut');
    if (rut) {
      this.cargarPaciente(rut);
    }
  }

  cargarPaciente(rut: string) {
    this.loading.set(true);
    this.pacienteService.obtenerPorRut(rut).subscribe({
      next: (data) => {
        this.paciente = {
          rut: data.rut,
          nombre: data.nombre,
          email: data.email,
          prevision: data.prevision,
          activo: data.activo,
          idMedico: data.idMedicoAsignado,
          nombreMedicoAsignado: data.nombreMedicoAsignado,
          especialidadNombre: data.especialidadNombre,
          contactoEmergenciaNombre: data.contactoEmergenciaNombre,
          contactoEmergenciaTelefono: data.contactoEmergenciaTelefono,
          datosClinicosSensibles: data.datosClinicosSensibles || {
            grupoSanguineo: '',
            pesoKg: null,
            alturaCm: null,
            alergias: [],
            enfermedadesCronicas: [],
          },
        };
        this.observacion = data.observacionMedico || '';
        this.loading.set(false);
      },
      error: () => {
        this.toastService.mostrar('Error al cargar paciente', 'error');
        this.loading.set(false);
      },
    });
  }

  activarEdicion() {
    this.modoEdicion = true;
    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
    this.alergiasEditList = this.paciente!.datosClinicosSensibles?.alergias
      ? [...this.paciente!.datosClinicosSensibles.alergias]
      : [];
    this.enfermedadesEditList = this.paciente!.datosClinicosSensibles?.enfermedadesCronicas
      ? [...this.paciente!.datosClinicosSensibles.enfermedadesCronicas]
      : [];
    this.nuevaAlergia = '';
    this.nuevaEnfermedad = '';
  }

  cancelarEdicion() {
    this.modoEdicion = false;
    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
  }

  agregarAlergia() {
    if (this.nuevaAlergia.trim()) {
      this.alergiasEditList.push(this.nuevaAlergia.trim());
      this.nuevaAlergia = '';
    }
  }

  removerAlergia(index: number) {
    this.alergiasEditList.splice(index, 1);
  }

  agregarEnfermedad() {
    if (this.nuevaEnfermedad.trim()) {
      this.enfermedadesEditList.push(this.nuevaEnfermedad.trim());
      this.nuevaEnfermedad = '';
    }
  }

  removerEnfermedad(index: number) {
    this.enfermedadesEditList.splice(index, 1);
  }

  guardarEdicion() {
    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
    let hasError = false;

    if (this.paciente!.datosClinicosSensibles!.pesoKg != null) {
      const peso = this.paciente!.datosClinicosSensibles!.pesoKg!;
      if (peso <= 0 || peso >= 1000) {
        this.erroresEdicion.pesoKg = 'El peso debe ser mayor a 0 y menor a 1000 kg.';
        hasError = true;
      }
    }

    if (this.paciente!.datosClinicosSensibles!.alturaCm != null) {
      const altura = Number(this.paciente!.datosClinicosSensibles!.alturaCm);
      if (altura < 2 || altura > 250) {
        this.erroresEdicion.alturaCm = 'La altura debe ser mínimo 2 cm y máximo 250 cm.';
        hasError = true;
      }
    }

    if (hasError) return;

    this.paciente!.datosClinicosSensibles!.alergias = [...this.alergiasEditList];
    this.paciente!.datosClinicosSensibles!.enfermedadesCronicas = [...this.enfermedadesEditList];

    const clinicosPayload: PacienteClinicosPayload = {
      prevision: this.paciente!.prevision,
      email: this.paciente!.email,
      datosClinicosSensibles: {
        grupoSanguineo: this.paciente!.datosClinicosSensibles!.grupoSanguineo,
        pesoKg: this.paciente!.datosClinicosSensibles!.pesoKg,
        alturaCm: this.paciente!.datosClinicosSensibles!.alturaCm,
        alergias: this.paciente!.datosClinicosSensibles!.alergias,
        enfermedadesCronicas: this.paciente!.datosClinicosSensibles!.enfermedadesCronicas,
      },
    };

    const contactoPayload = {
      contactoEmergenciaNombre: this.paciente!.contactoEmergenciaNombre,
      contactoEmergenciaTelefono: this.paciente!.contactoEmergenciaTelefono,
    };

    this.modoEdicion = false;

    this.pacienteService.actualizarContactoEmergencia(this.paciente!.rut, contactoPayload).subscribe({
      next: () => {
        this.pacienteService.actualizarDatosClinicos(this.paciente!.rut, clinicosPayload).subscribe({
          next: () => {
            this.toastService.mostrar('Datos actualizados correctamente', 'success');
          },
          error: () => {
            this.toastService.mostrar('Error al guardar datos clínicos.', 'error');
          },
        });
      },
      error: () => {
        this.toastService.mostrar('Error al guardar contacto de emergencia.', 'error');
      },
    });
  }

  guardarObservacion() {
    if (!this.paciente?.rut) return;
    this.guardando = true;
    this.pacienteService.actualizarObservacion(this.paciente.rut, this.observacion).subscribe({
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
    if (this.paciente?.rut) {
      this.router.navigate(['/medico/pacientes', this.paciente.rut, 'solicitar-cirugia']);
    }
  }

  volver() {
    this.router.navigate(['/medico/pacientes']);
  }
}
