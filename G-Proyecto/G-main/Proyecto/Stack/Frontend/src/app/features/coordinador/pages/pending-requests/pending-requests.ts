import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PabellonService } from '../../../../core/services/pabellon.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-pending-requests',
  imports: [CommonModule, FormsModule, StatCardComponent],
  templateUrl: './pending-requests.html',
  styleUrl: './pending-requests.scss',
})
export class PendingRequestsComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private pabellonService = inject(PabellonService);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  solicitudesPendientes: any[] = [];
  pacientes: any[] = [];
  pabellonesDisponibles: any[] = [];
  loading = signal(false);

  cirugiaAProgramar: any = null;
  programarData: any = { fechaProgramada: '', horaInicio: '', horaFin: '', fechaSeleccionada: 'principal', pabellonId: '' };

  pacienteNombreMap = new Map<string, string>();
  medicoNombreMap = new Map<string, string>();

  ngOnInit() {
    this.cargarPacientes();
    this.cargarSolicitudesPendientes();
  }

  cargarPacientes() {
    this.pacienteService.listarTodos().subscribe({
      next: (data: any) => {
        const lista: any[] = Array.isArray(data) ? data : data.value ?? [];
        this.pacientes = lista;
        lista.forEach((p: any) => this.pacienteNombreMap.set(p.rut, p.nombre));
      },
      error: () => this.toastService.mostrar('Error al cargar pacientes', 'error'),
    });
  }

  cargarSolicitudesPendientes() {
    if (this.loading()) return;
    this.loading.set(true);
    this.cirugiaService.listarSolicitadas().subscribe({
      next: (data) => {
        this.solicitudesPendientes = data;
        data.forEach((c: any) => {
          if (c.pacienteRut && c.pacienteNombre) this.pacienteNombreMap.set(c.pacienteRut, c.pacienteNombre);
        });
        this.loading.set(false);
      },
      error: () => { this.toastService.mostrar('Error al cargar solicitudes', 'error'); this.loading.set(false); },
    });
  }

  getNombrePaciente(rut: string): string {
    return this.pacienteNombreMap.get(rut) || rut;
  }

  getNombreMedico(rut: string): string {
    return this.medicoNombreMap.get(rut) || rut;
  }

  abrirProgramar(c: any) {
    this.cirugiaAProgramar = c;
    this.programarData = { fechaProgramada: '', horaInicio: '', horaFin: '', pabellonId: '', fechaSeleccionada: 'principal' };
    this.cargarPabellonesDisponibles();
  }

  cargarPabellonesDisponibles() {
    this.pabellonService.listarDisponibles().subscribe({
      next: (data) => { this.pabellonesDisponibles = data; },
      error: () => this.toastService.mostrar('Error al cargar pabellones disponibles', 'error'),
    });
  }

  confirmarProgramar() {
    if (!this.cirugiaAProgramar || !this.programarData.fechaProgramada || !this.programarData.pabellonId) return;

    const payload = {
      pabellonId: this.programarData.pabellonId,
      fechaProgramada: this.programarData.fechaProgramada,
      horaInicio: this.programarData.horaInicio,
      horaFin: this.programarData.horaFin,
    };

    this.cirugiaService.programar(this.cirugiaAProgramar.idCirugia, payload).subscribe({
      next: () => {
        this.cirugiaAProgramar = null;
        this.cargarSolicitudesPendientes();
        this.toastService.mostrar('Cirugía programada exitosamente', 'success');
      },
      error: (err) => this.toastService.mostrar(err.error?.message || err.error?.mensaje || 'Error al programar', 'error'),
    });
  }

  cancelarProgramar() {
    this.cirugiaAProgramar = null;
  }
}
