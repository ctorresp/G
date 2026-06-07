import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PacienteService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pacientes`);
  }

  listarPorMedico(idMedico: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pacientes/por-medico/${idMedico}`);
  }

  listarPorMedicoRut(rutMedico: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pacientes/por-medico-rut/${rutMedico}`);
  }

  obtenerPorRut(rut: string): Observable<any> {
    return this.http.get<any>(`${this.api}/pacientes/rut/${rut}`);
  }

  actualizarDatosClinicos(rut: string, payload: any): Observable<any> {
    return this.http.put(`${this.api}/pacientes/rut/${rut}/clinicos`, payload);
  }

  actualizarContactoEmergencia(rut: string, payload: any): Observable<any> {
    return this.http.put(`${this.api}/pacientes/rut/${rut}/contacto`, payload);
  }

  actualizarObservacion(rut: string, observacion: string): Observable<any> {
    return this.http.put(`${this.api}/pacientes/rut/${rut}/observacion`, { observacionMedico: observacion });
  }

  eliminar(rut: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/pacientes/rut/${rut}`);
  }
}
