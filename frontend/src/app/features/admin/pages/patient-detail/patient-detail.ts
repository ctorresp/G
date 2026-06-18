import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Paciente, PacienteClinicosPayload } from '../../../../core/interfaces';
import { storage } from '../../../../core/storage';
import { STORAGE_KEYS } from '../../../../core/constants';

@Component({
  selector: 'app-patient-detail',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.scss',
})
export class PatientDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  paciente: Paciente | null = null;
  modoEdicion = false;
  loading = signal(false);
  esAdmin = false;

  alergiasEditList: string[] = [];
  enfermedadesEditList: string[] = [];
  nuevaAlergia = '';
  nuevaEnfermedad = '';

  erroresEdicion = { pesoKg: '', alturaCm: '' };

  ngOnInit() {
    const role = storage.getItem(STORAGE_KEYS.ROLES);
    this.esAdmin = role === 'ROLE_ADMIN';

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
          datosClinicosSensibles: data.datosClinicosSensibles || {
            grupoSanguineo: '',
            pesoKg: null,
            alturaCm: null,
            alergias: [],
            enfermedadesCronicas: [],
          },
        };
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
    this.alergiasEditList = this.paciente!.datosClinicosSensibles?.alergias ? [...this.paciente!.datosClinicosSensibles.alergias] : [];
    this.enfermedadesEditList = this.paciente!.datosClinicosSensibles?.enfermedadesCronicas ? [...this.paciente!.datosClinicosSensibles.enfermedadesCronicas] : [];
    this.nuevaAlergia = '';
    this.nuevaEnfermedad = '';
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
    this.modoEdicion = false;

    if (this.esAdmin) {
      const payload = {
        prevision: this.paciente!.prevision,
        email: this.paciente!.email,
      };
      this.pacienteService.actualizarDatosClinicos(this.paciente!.rut, payload).subscribe({
        next: () => {
          this.toastService.mostrar('Datos actualizados', 'success');
        },
        error: () => {
          this.toastService.mostrar('Error al guardar los cambios.', 'error');
        },
      });
      return;
    }

    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
    let hasError = false;

    if (this.paciente!.datosClinicosSensibles!.pesoKg == null) {
      this.erroresEdicion.pesoKg = 'Falta información: El peso es obligatorio.';
      hasError = true;
    } else {
      const peso = this.paciente!.datosClinicosSensibles!.pesoKg!;
      if (peso <= 0 || peso >= 1000) {
        this.erroresEdicion.pesoKg = 'El peso debe ser mayor a 0 y menor a 1000 kg.';
        hasError = true;
      }
    }

    if (this.paciente!.datosClinicosSensibles!.alturaCm == null) {
      this.erroresEdicion.alturaCm = 'Falta información: La altura es obligatoria.';
      hasError = true;
    } else {
      const altura = Number(this.paciente!.datosClinicosSensibles!.alturaCm);
      if (altura < 2 || altura > 250) {
        this.erroresEdicion.alturaCm = 'La altura debe ser mínimo 2 cm y máximo 250 cm.';
        hasError = true;
      }
    }

    if (hasError) return;

    this.paciente!.datosClinicosSensibles!.alergias = [...this.alergiasEditList];
    this.paciente!.datosClinicosSensibles!.enfermedadesCronicas = [...this.enfermedadesEditList];

    const payload: PacienteClinicosPayload = {
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

    this.pacienteService.actualizarDatosClinicos(this.paciente!.rut, payload).subscribe({
      next: () => {
        this.toastService.mostrar('Datos clínicos actualizados', 'success');
      },
      error: () => {
        this.toastService.mostrar('Error al guardar los cambios.', 'error');
      },
    });
  }
}
