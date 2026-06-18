import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Paciente } from '../../../../core/interfaces';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-patient-list',
  imports: [CommonModule, FormsModule, RouterLink, StatCardComponent],
  templateUrl: './patient-list.html',
  styleUrl: './patient-list.scss',
})
export class PatientListComponent implements OnInit {
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  pacientes: Paciente[] = [];
  busqueda = '';
  loading = signal(false);

  get pacientesFiltrados() {
    const q = this.busqueda.toLowerCase().trim();
    if (!q) return this.pacientes;
    return this.pacientes.filter(p =>
      p.rut.toLowerCase().includes(q) ||
      p.nombre.toLowerCase().includes(q) ||
      p.email.toLowerCase().includes(q)
    );
  }

  ngOnInit() {
    this.cargarPacientes();
  }

  cargarPacientes() {
    if (this.loading()) return;
    this.loading.set(true);
    this.pacienteService.listarTodos().subscribe({
      next: (data) => { this.pacientes = data; this.loading.set(false); },
      error: () => { this.toastService.mostrar('Error al cargar pacientes', 'error'); this.loading.set(false); },
    });
  }

  toggleEstado(paciente: Paciente) {
    const nuevoEstado = !paciente.activo;
    const accion = nuevoEstado ? 'activar' : 'bloquear';
    this.pacienteService.cambiarEstado(paciente.rut, nuevoEstado).subscribe({
      next: () => { this.cargarPacientes(); this.toastService.mostrar(`Paciente ${accion}do exitosamente`, 'success'); },
      error: () => this.toastService.mostrar('Error al cambiar estado', 'error'),
    });
  }

  verDetalle(rut: string) {
    this.router.navigate(['/admin/pacientes', rut]);
  }
}
