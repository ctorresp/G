import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  reportePerdidas(desde?: string, hasta?: string): Observable<any> {
    let params = '';
    if (desde) params += `?desde=${desde}`;
    if (hasta) params += (params ? '&' : '?') + `hasta=${hasta}`;
    return this.http.get<any>(`${this.api}/pabellon/reportes/perdidas${params}`);
  }
}
