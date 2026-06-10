import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { storage } from '../../../../core/storage';
import { Cirugia } from '../../../../core/interfaces';

@Component({
  selector: 'app-my-surgeries',
  imports: [CommonModule],
  templateUrl: './my-surgeries.html',
  styleUrl: './my-surgeries.scss',
})
export class MySurgeriesComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private pacienteService = inject(PacienteService);
  private toastService = inject(ToastService);

  cirugias: Cirugia[] = [];
  pacienteNombreMap = new Map<string, string>();
  loading = false;

  get misProximasCirugias() {
    return this.cirugias
      .filter((c) => c.estado === 'PROGRAMADA' || c.estado === 'REASIGNADA')
      .sort((a, b) => new Date(a.fechaProgramada!).getTime() - new Date(b.fechaProgramada!).getTime());
  }

  ngOnInit() {
    this.cargarPacientes();
    this.cargarCirugias();
  }

  cargarPacientes() {
    this.pacienteService.listarTodos().subscribe({
      next: (data: any) => {
        const lista: any[] = Array.isArray(data) ? data : data.value ?? [];
        lista.forEach((p: any) => this.pacienteNombreMap.set(p.rut, p.nombre));
      },
    });
  }

  cargarCirugias() {
    if (this.loading) return;
    this.loading = true;
    const rut = storage.getItem(STORAGE_KEYS.USUARIO_RUT);
    if (!rut) { this.loading = false; return; }

    this.cirugiaService.listarPorMedico(rut).subscribe({
      next: (data) => {
        this.cirugias = data;
        this.loading = false;
      },
      error: () => {
        this.toastService.mostrar('Error al cargar cirugías', 'error');
        this.loading = false;
      },
    });
  }

  getNombrePaciente(rut: string): string {
    return this.pacienteNombreMap.get(rut) || rut;
  }

  marcarNoShow(cirugia: Cirugia) {
    if (!confirm(`¿Confirmar que el paciente NO ASISTIÓ a la cirugía #${cirugia.idCirugia}?`)) return;
    this.cirugiaService.marcarNoShow(cirugia.idCirugia).subscribe({
      next: () => {
        this.cargarCirugias();
        this.toastService.mostrar('Cirugía marcada como NO SHOW', 'success');
      },
      error: () => this.toastService.mostrar('Error al marcar NO SHOW', 'error'),
    });
  }

  completarCirugia(cirugia: Cirugia) {
    if (!confirm(`¿Confirmar que la cirugía #${cirugia.idCirugia} se realizó correctamente?`)) return;
    this.cirugiaService.completar(cirugia.idCirugia).subscribe({
      next: () => {
        this.cargarCirugias();
        this.toastService.mostrar('Cirugía completada exitosamente', 'success');
      },
      error: () => this.toastService.mostrar('Error al completar cirugía', 'error'),
    });
  }

  cancelarCirugia(cirugia: Cirugia) {
    const motivo = prompt('Motivo de cancelación:', 'CANCELADO_POR_MEDICO');
    if (!motivo) return;
    if (!confirm(`¿Cancelar la cirugía #${cirugia.idCirugia}?`)) return;
    this.cirugiaService.cancelar(cirugia.idCirugia, motivo).subscribe({
      next: () => {
        this.cargarCirugias();
        this.toastService.mostrar('Cirugía cancelada', 'success');
      },
      error: () => this.toastService.mostrar('Error al cancelar cirugía', 'error'),
    });
  }
}
