import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TriajeService } from '../../../../core/services/triaje.service';

@Component({
  selector: 'app-historial',
  imports: [CommonModule],
  templateUrl: './historial.html',
  styleUrl: './historial.scss',
})
export class HistorialComponent implements OnInit {
  private triajeService = inject(TriajeService);

  triajes: any[] = [];

  ngOnInit() {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.triajeService.listarTodos().subscribe({
      next: (data) => {
        this.triajes = data;
      },
      error: () => {
        this.triajes = [];
      },
    });
  }

  nivelLabel(n: number): string {
    const labels: Record<number, string> = {
      1: 'Resucitación',
      2: 'Emergencia',
      3: 'Urgencia',
      4: 'Urgencia Menor',
      5: 'No Urgente',
    };
    return labels[n] || '';
  }
}
