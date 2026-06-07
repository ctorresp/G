import { Injectable, signal } from '@angular/core';

export interface ToastState {
  mensaje: string;
  tipo: 'success' | 'error';
  visible: boolean;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly toastSignal = signal<ToastState>({
    mensaje: '',
    tipo: 'success',
    visible: false,
  });

  readonly toast = this.toastSignal.asReadonly();

  private timeoutId: ReturnType<typeof setTimeout> | null = null;

  mostrar(mensaje: string, tipo: 'success' | 'error' = 'success') {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
    this.toastSignal.set({ mensaje, tipo, visible: true });
    this.timeoutId = setTimeout(() => this.cerrar(), 5000);
  }

  cerrar() {
    this.toastSignal.update((s) => ({ ...s, visible: false }));
  }
}
