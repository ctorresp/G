export interface Triaje {
  idTriaje: number;
  pacienteRut: string;
  triajerRut: string;
  nivelUrgencia: number;
  sintomas: string;
  presionArterial?: string;
  frecuenciaCardiaca?: number;
  temperatura?: number;
  observaciones?: string;
  fecha: string;
}

export interface TriajeGuardarPayload {
  pacienteRut: string;
  triajerRut: string;
  nivelUrgencia: number;
  sintomas: string;
  presionArterial?: string;
  frecuenciaCardiaca?: number | null;
  temperatura?: number | null;
  observaciones?: string;
}
