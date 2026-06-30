import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { STORAGE_KEYS } from '../../core/constants';
import { storage } from '../../core/storage';


@Component({
  selector: 'app-triajer-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './triajer-layout.html',
  styleUrl: './triajer-layout.scss',
})
export class TriajerLayout implements OnInit {
  private router = inject(Router);
  usuarioRut: string = '';
  sidebarAbierta = false;

  toggleSidebar() {
    this.sidebarAbierta = !this.sidebarAbierta;
  }

  ngOnInit() {
    const token = storage.getItem(STORAGE_KEYS.TOKEN);
    console.log('[TriajerLayout] token en init:', !!token, 'rut:', storage.getItem(STORAGE_KEYS.USUARIO_RUT));
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.usuarioRut = storage.getItem(STORAGE_KEYS.USUARIO_RUT) || '';
  }

  cerrarSesion() {
    storage.removeItem(STORAGE_KEYS.TOKEN);
    storage.removeItem(STORAGE_KEYS.PORTAL);
    storage.removeItem(STORAGE_KEYS.USUARIO_RUT);
    storage.removeItem(STORAGE_KEYS.USUARIO_ID);
    storage.removeItem(STORAGE_KEYS.USUARIO_NOMBRE);
    storage.removeItem(STORAGE_KEYS.ROLES);
    this.router.navigate(['/']);
  }
}
