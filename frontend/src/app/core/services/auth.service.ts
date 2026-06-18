import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  registrarPaciente(payload: any): Observable<any> {
    return this.http.post(`${this.api}/auth/registro`, payload);
  }

  registrarMedico(payload: any, rol = 'ROLE_MEDICO'): Observable<any> {
    return this.http.post(`${this.api}/auth/admin/registro?rol=${rol}`, payload);
  }

  registrarMedicoCompleto(payload: any, especialidadId: number): Observable<any> {
    return this.http.post(`${this.api}/auth/admin/registro-medico?especialidadId=${especialidadId}`, payload);
  }

  listarMedicos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/admin/medicos`);
  }

  actualizarMedico(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.api}/admin/medicos/${id}`, payload);
  }

  eliminarMedico(rut: string): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/medicos/rut/${rut}`);
  }

  obtenerPerfilMedico(): Observable<any> {
    return this.http.get(`${this.api}/auth/usuario/medico/perfil`);
  }

  listarEspecialidades(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/usuarios/especialidades`);
  }
}
