export interface DatosClinicos {
  grupoSanguineo: string;
  pesoKg: number | null;
  alturaCm: number | null;
  alergias: string[];
  enfermedadesCronicas: string[];
}

export interface Paciente {
  rut: string;
  nombre: string;
  email: string;
  prevision: string;
  estado: boolean;
  idMedico?: number;
  nombreMedicoAsignado?: string;
  contactoEmergenciaNombre?: string;
  contactoEmergenciaTelefono?: string;
  datosClinicosSensibles?: DatosClinicos;
}

export interface PacienteRegistroPayload {
  rut: string;
  nombre: string;
  email: string;
  contrasena: string;
  prevision: string;
  idMedico?: number;
  contactoEmergenciaNombre?: string;
  contactoEmergenciaTelefono?: string;
  datosClinicosSensibles: {
    grupoSanguineo: string;
    pesoKg: number | null;
    alturaCm: number | null;
    alergias: string[];
    enfermedadesCronicas: string[];
  };
}

export interface PacienteClinicosPayload {
  prevision: string;
  email: string;
  idMedico?: number;
  contactoEmergenciaNombre?: string;
  contactoEmergenciaTelefono?: string;
  datosClinicosSensibles: {
    grupoSanguineo: string;
    pesoKg: number | null;
    alturaCm: number | null;
    alergias: string[];
    enfermedadesCronicas: string[];
  };
}
