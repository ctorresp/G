import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';

@Component({
  selector: 'app-solicitudes',
  imports: [CommonModule],
  templateUrl: './solicitudes.html',
  styleUrl: './solicitudes.scss',
})
export class SolicitudesComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  pendientes: any[] = [];

  ngOnInit() {
    this.cargarPendientes();
  }

  cargarPendientes() {
    this.cirugiaService.listarPendientesTriaje().subscribe({
      next: (data) => {
        this.pendientes = data;
        this.cdr.detectChanges();
      },
      error: () => {
        this.pendientes = [];
        this.cdr.detectChanges();
      },
    });
  }

  verSolicitud(c: any) {
    this.router.navigate(['/triajer/triaje', c.pacienteRut, c.idCirugia], {
      state: { solicitud: c },
    });
  }
}
