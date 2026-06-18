export type PabellonEstado = 'DISPONIBLE' | 'OCUPADO' | 'MANTENCION';

export interface Pabellon {
  idPabellon: number;
  numero: string;
  nombre: string;
  descripcion?: string;
  estado: PabellonEstado;
}

export interface PabellonCrearPayload {
  numero: string;
  nombre: string;
  descripcion: string;
  estado: PabellonEstado;
}
