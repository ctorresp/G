import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PabellonService } from '../../../../core/services/pabellon.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-surgery-metrics',
  imports: [CommonModule, FormsModule, StatCardComponent],
  templateUrl: './surgery-metrics.html',
  styleUrl: './surgery-metrics.scss',
})
export class SurgeryMetricsComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private pabellonService = inject(PabellonService);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  cirugias: any[] = [];
  pabellones: any[] = [];
  pacientes: any[] = [];
  pacienteNombreMap = new Map<string, string>();
  medicoNombreMap = new Map<string, string>();
  loadingCirugias = signal(false);
  loadingPabellones = signal(false);

  mostrarFormCrearPabellon = false;
  mostrarFormEditarPabellon = false;
  nuevoPabellon = { numero: '', estado: 'DISPONIBLE' };
  pabellonEditando: any = null;

  get cirugiasProgramadas() { return this.cirugias.filter(c => c.estado === 'PROGRAMADA').length; }
  get cirugiasCanceladas() { return this.cirugias.filter(c => c.estado === 'CANCELADA' || c.estado === 'NO_SHOW').length; }
  get totalPacientes() { return this.pacientes.length; }
  get pabellonesOcupados() { return this.pabellones.filter(p => p.estado === 'OCUPADO').length; }

  ngOnInit() {
    this.cargarCirugias();
    this.cargarPabellones();
    this.cargarPacientes();
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

  cargarCirugias() {
    if (this.loadingCirugias()) return;
    this.loadingCirugias.set(true);
    this.cirugiaService.listarTodas().subscribe({
      next: (data) => {
        this.cirugias = data;
        data.forEach((c: any) => {
          if (c.pacienteRut && c.pacienteNombre) this.pacienteNombreMap.set(c.pacienteRut, c.pacienteNombre);
        });
        this.loadingCirugias.set(false);
      },
      error: () => { this.toastService.mostrar('Error al cargar cirugías', 'error'); this.loadingCirugias.set(false); },
    });
  }

  cargarPabellones() {
    if (this.loadingPabellones()) return;
    this.loadingPabellones.set(true);
    this.pabellonService.listarTodos().subscribe({
      next: (data) => { this.pabellones = data; this.loadingPabellones.set(false); },
      error: () => { this.toastService.mostrar('Error al cargar pabellones', 'error'); this.loadingPabellones.set(false); },
    });
  }

  getNombrePaciente(rut: string): string {
    return this.pacienteNombreMap.get(rut) || rut;
  }

  getNombreMedico(rut: string): string {
    return this.medicoNombreMap.get(rut) || rut;
  }

  cambiarEstadoPabellon(pabellon: any, estado: string) {
    this.pabellonService.cambiarEstado(pabellon.idPabellon, estado).subscribe({
      next: () => { this.cargarPabellones(); this.toastService.mostrar('Estado actualizado', 'success'); },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al cambiar estado', 'error'),
    });
  }

  abrirEditarPabellon(pabellon: any) {
    this.pabellonEditando = { ...pabellon };
    this.mostrarFormEditarPabellon = true;
  }

  guardarEdicionPabellon() {
    if (!this.pabellonEditando.numero) return;
    this.pabellonService.actualizar(this.pabellonEditando.idPabellon, this.pabellonEditando).subscribe({
      next: () => {
        this.mostrarFormEditarPabellon = false;
        this.pabellonEditando = null;
        this.cargarPabellones();
        this.toastService.mostrar('Pabellón actualizado', 'success');
      },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al actualizar pabellón', 'error'),
    });
  }

  eliminarPabellon(pabellon: any) {
    this.pabellonService.eliminar(pabellon.idPabellon).subscribe({
      next: () => { this.cargarPabellones(); this.toastService.mostrar('Pabellón eliminado', 'success'); },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al eliminar pabellón', 'error'),
    });
  }

  crearPabellon() {
    if (!this.nuevoPabellon.numero) return;
    this.pabellonService.crear(this.nuevoPabellon).subscribe({
      next: () => {
        this.mostrarFormCrearPabellon = false;
        this.nuevoPabellon = { numero: '', estado: 'DISPONIBLE' };
        this.cargarPabellones();
        this.toastService.mostrar('Pabellón creado', 'success');
      },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al crear pabellón', 'error'),
    });
  }

  completarCirugia(cirugia: any) {
    this.cirugiaService.completar(cirugia.idCirugia).subscribe({
      next: () => { this.cargarCirugias(); this.toastService.mostrar('Cirugía completada', 'success'); },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al completar cirugía', 'error'),
    });
  }

  marcarNoShow(cirugia: any) {
    this.cirugiaService.marcarNoShow(cirugia.idCirugia).subscribe({
      next: () => { this.cargarCirugias(); this.toastService.mostrar('Cirugía marcada como NO SHOW', 'success'); },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al marcar NO SHOW', 'error'),
    });
  }

  cancelarCirugia(cirugia: any) {
    const motivo = prompt('Motivo de cancelación:', 'CANCELADO_POR_COORDINADOR');
    if (!motivo) return;
    this.cirugiaService.cancelar(cirugia.idCirugia, motivo).subscribe({
      next: () => { this.cargarCirugias(); this.toastService.mostrar('Cirugía cancelada', 'success'); },
      error: (err) => this.toastService.mostrar(err.error?.mensaje || 'Error al cancelar cirugía', 'error'),
    });
  }
}
