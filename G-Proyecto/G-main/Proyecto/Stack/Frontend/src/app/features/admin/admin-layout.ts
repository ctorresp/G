import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { STORAGE_KEYS } from '../../core/constants';


@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss',
})
export class AdminLayout implements OnInit {
  private router = inject(Router);
  usuarioRut: string = '';
  usuarioNombre: string = '';

  ngOnInit() {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.usuarioRut = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT) || '';
    this.usuarioNombre = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT) || 'Admin';
  }

  cerrarSesion() {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.PORTAL);
    localStorage.removeItem(STORAGE_KEYS.USUARIO_RUT);
    localStorage.removeItem(STORAGE_KEYS.USUARIO_ID);
    localStorage.removeItem(STORAGE_KEYS.USUARIO_NOMBRE);
    localStorage.removeItem(STORAGE_KEYS.ROLES);
    this.router.navigate(['/']);
  }
}
