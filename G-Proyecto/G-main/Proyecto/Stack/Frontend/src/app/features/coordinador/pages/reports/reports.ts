import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../../../core/services/reporte.service';
import { ToastService } from '../../../../core/services/toast.service';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-reports',
  imports: [CommonModule, FormsModule, StatCardComponent],
  templateUrl: './reports.html',
  styleUrl: './reports.scss',
})
export class ReportsComponent implements OnInit {
  private reporteService = inject(ReporteService);
  private toastService = inject(ToastService);

  reporte: any = null;
  loading = signal(false);
  desde = '';
  hasta = '';

  ngOnInit() {
    this.cargarReporte();
  }

  cargarReporte() {
    if (this.loading()) return;
    this.loading.set(true);
    this.reporteService.reportePerdidas(this.desde || undefined, this.hasta || undefined).subscribe({
      next: (data) => { this.reporte = data; this.loading.set(false); },
      error: () => { this.toastService.mostrar('Error al cargar reporte', 'error'); this.loading.set(false); },
    });
  }
}
