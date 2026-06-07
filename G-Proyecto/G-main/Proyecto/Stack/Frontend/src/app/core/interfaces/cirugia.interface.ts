export type CirugiaEstado = 'SOLICITADA' | 'PROGRAMADA' | 'REASIGNADA' | 'COMPLETADA' | 'CANCELADA' | 'NO_SHOW';

export interface Cirugia {
  idCirugia: number;
  pacienteRut: string;
  especialidadId?: number;
  medicoRut?: string;
  notas?: string;
  fechaProgramada?: string;
  estado: CirugiaEstado;
  pabellonId?: number;
  horaInicio?: string;
  horaFin?: string;
}

export interface CirugiaSolicitarPayload {
  pacienteRut: string;
  especialidad: { idEspecialidad: number };
  medicoRut: string;
  notas?: string;
  fechaProgramada?: string;
}

export interface CirugiaProgramarPayload {
  pabellonId: number | string;
  fechaProgramada: string;
  horaInicio: string;
  horaFin: string;
}
