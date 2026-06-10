import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { TriajeService } from '../../../../core/services/triaje.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { storage } from '../../../../core/storage';
import { TriajeGuardarPayload } from '../../../../core/interfaces';

@Component({
  selector: 'app-triage',
  imports: [CommonModule, FormsModule],
  templateUrl: './triage.html',
  styleUrl: './triage.scss',
})
export class TriageComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private triajeService = inject(TriajeService);
  private toastService = inject(ToastService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  pacienteRut: string = '';
  solicitud: any = null;
  idCirugia: number | null = null;

  nuevoTriaje: TriajeGuardarPayload = {
    pacienteRut: '',
    triajerRut: '',
    nivelUrgencia: 3,
    sintomas: '',
    presionArterial: '',
    frecuenciaCardiaca: null,
    temperatura: null,
    observaciones: '',
  };

  ngOnInit() {
    this.nuevoTriaje.triajerRut = storage.getItem(STORAGE_KEYS.USUARIO_RUT) || '';
    const rutParam = this.route.snapshot.paramMap.get('rut');
    const idParam = this.route.snapshot.paramMap.get('idCirugia');
    if (rutParam) {
      this.pacienteRut = rutParam;
      this.nuevoTriaje.pacienteRut = rutParam;
    }
    const state = history.state as any;
    if (state?.solicitud) {
      this.solicitud = state.solicitud;
      this.idCirugia = this.solicitud.idCirugia;
    } else if (idParam) {
      this.idCirugia = Number(idParam);
      this.cargarSolicitud(this.idCirugia);
    }
  }

  cargarSolicitud(id: number) {
    this.cirugiaService.obtenerPorId(id).subscribe({
      next: (data) => { this.solicitud = data; },
      error: () => { this.solicitud = null; },
    });
  }

  guardarTriaje() {
    this.triajeService.guardar(this.nuevoTriaje).subscribe({
      next: () => {
        if (this.idCirugia) {
          this.cirugiaService.marcarTriajeCompletado(this.idCirugia).subscribe();
        }
        this.nuevoTriaje = {
          pacienteRut: '',
          triajerRut: storage.getItem(STORAGE_KEYS.USUARIO_RUT) || '',
          nivelUrgencia: 3,
          sintomas: '',
          presionArterial: '',
          frecuenciaCardiaca: null,
          temperatura: null,
          observaciones: '',
        };
        this.toastService.mostrar('Triaje guardado exitosamente', 'success');
        this.router.navigate(['/triajer/solicitudes']);
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al guardar triaje';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/triajer/solicitudes']);
  }
}
