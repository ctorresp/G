import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TriajeService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarPorPaciente(rut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/triajes/paciente/${rut}`);
  }

  listarTodos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/pabellon/triajes`);
  }

  guardar(payload: any): Observable<any> {
    return this.http.post(`${this.api}/pabellon/triajes`, payload);
  }
}
