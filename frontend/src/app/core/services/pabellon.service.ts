import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PabellonService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarTodos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/pabellones`);
  }

  listarDisponibles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/pabellones/disponibles`);
  }

  listarEspecialidades(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/especialidades`);
  }

  obtenerEspecialidadesMedico(rut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/medico-especialidad/${rut}`);
  }

  crear(data: { numero: string; estado: string }): Observable<any> {
    return this.http.post<any>(`${this.api}/pabellon/pabellones`, data);
  }

  actualizar(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.api}/pabellon/pabellones/${id}`, data);
  }

  cambiarEstado(id: number, estado: string): Observable<any> {
    return this.http.put<any>(`${this.api}/pabellon/pabellones/${id}/estado`, { estado });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/pabellon/pabellones/${id}`);
  }

  asignarEspecialidadAMedico(rut: string, especialidadIds: number[]): Observable<void> {
    return this.http.post<void>(`${this.api}/pabellon/medico-especialidad/${rut}`, { especialidadIds });
  }
}
