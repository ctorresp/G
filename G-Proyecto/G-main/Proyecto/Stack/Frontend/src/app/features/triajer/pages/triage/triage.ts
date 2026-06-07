import { Component, OnInit, OnDestroy, AfterViewInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { TriajeService } from '../../../../core/services/triaje.service';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { ToastService } from '../../../../core/services/toast.service';
import { STORAGE_KEYS } from '../../../../core/constants';
import { Triaje, TriajeGuardarPayload } from '../../../../core/interfaces';

@Component({
  selector: 'app-triage',
  imports: [CommonModule, FormsModule],
  templateUrl: './triage.html',
  styleUrl: './triage.scss',
})
export class TriageComponent implements OnInit, OnDestroy, AfterViewInit {
  private triajeService = inject(TriajeService);
  private cirugiaService = inject(CirugiaService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);

  triajes: Triaje[] = [];
  triajeBusquedaRut: string = '';
  pendientesTriaje: any[] = [];
  selectedCirugiaId: number | null = null;
  private pollingSub: Subscription | null = null;

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
    this.nuevoTriaje.triajerRut = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT) || '';
    this.cargarPendientes();
  }

  ngAfterViewInit() {
    this.cargarPendientes();
    this.pollingSub = interval(5000).subscribe(() => this.cargarPendientes());
  }

  ngOnDestroy() {
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
    }
  }

  cargarPendientes() {
    this.cirugiaService.listarPendientesTriaje().subscribe({
      next: (data) => {
        this.pendientesTriaje = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando pendientes triaje:', err);
        this.pendientesTriaje = [];
        this.cdr.detectChanges();
      },
    });
  }

  seleccionarPendiente(c: any) {
    this.selectedCirugiaId = c.idCirugia;
    this.nuevoTriaje.pacienteRut = c.pacienteRut;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cargarTriajes(rut: string) {
    this.triajeService.listarPorPaciente(rut).subscribe({
      next: (data) => {
        this.triajes = data;
      },
      error: () => {
        this.triajes = [];
      },
    });
  }

  buscarTriajes() {
    if (this.triajeBusquedaRut) {
      this.cargarTriajes(this.triajeBusquedaRut);
    }
  }

  guardarTriaje() {
    this.triajeService.guardar(this.nuevoTriaje).subscribe({
      next: () => {
        const rutGuardado = this.nuevoTriaje.pacienteRut;
        const cirugiaId = this.selectedCirugiaId;
        this.nuevoTriaje = {
          pacienteRut: '',
          triajerRut: localStorage.getItem(STORAGE_KEYS.USUARIO_RUT) || '',
          nivelUrgencia: 3,
          sintomas: '',
          presionArterial: '',
          frecuenciaCardiaca: null,
          temperatura: null,
          observaciones: '',
        };
        this.selectedCirugiaId = null;
        this.toastService.mostrar('Triaje guardado exitosamente', 'success');
        this.triajeBusquedaRut = rutGuardado;
        this.buscarTriajes();
        if (cirugiaId) {
          this.cirugiaService.marcarTriajeCompletado(cirugiaId).subscribe({
            next: () => {
              this.cargarPendientes();
            },
          });
        }
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al guardar triaje';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }
}
