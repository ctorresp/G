import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CirugiaService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarTodas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/cirugias`);
  }

  listarPorMedico(rut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/cirugias/medico/${rut}`);
  }

  listarSolicitadas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/cirugias/solicitadas`);
  }

  listarPendientesTriaje(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/cirugias/pendientes-triaje`);
  }

  marcarTriajeCompletado(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/pabellon/cirugias/${id}/triaje-completado`, {});
  }

  solicitar(payload: any): Observable<any> {
    return this.http.post(`${this.api}/pabellon/cirugias/solicitar`, payload);
  }

  programar(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.api}/pabellon/cirugias/${id}/programar`, payload);
  }

  cancelar(id: number, motivo: string): Observable<void> {
    return this.http.put<void>(`${this.api}/pabellon/cirugias/${id}/cancelar`, { motivo });
  }

  marcarNoShow(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/pabellon/cirugias/${id}/no-show`, {});
  }

  completar(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/pabellon/cirugias/${id}/completar`, {});
  }
}
