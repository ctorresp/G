import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  listarUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/admin/usuarios`);
  }

  cambiarEstadoUsuario(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.api}/admin/usuarios/${id}/estado`, { estado });
  }
}
