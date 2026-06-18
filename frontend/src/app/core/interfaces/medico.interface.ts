export interface Medico {
  idUsuario: number;
  rut: string;
  nombre: string;
  email: string;
  estado: boolean;
  roles: string[];
}

export interface MedicoRegistroPayload {
  rut: string;
  nombre: string;
  email: string;
  contrasena: string;
}

export interface MedicoUpdatePayload {
  rut?: string;
  nombre?: string;
  email?: string;
}
