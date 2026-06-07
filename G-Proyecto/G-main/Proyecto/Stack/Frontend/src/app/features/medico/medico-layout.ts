import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { STORAGE_KEYS } from '../../core/constants';


@Component({
  selector: 'app-medico-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './medico-layout.html',
  styleUrl: './medico-layout.scss',
})
export class MedicoLayout implements OnInit {
  private router = inject(Router);
  usuarioRut: string = '';

  ngOnInit() {
    const token = localStorage.getItem(STORAGE_KEYS.TOKEN);
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.usuarioRut = localStorage.getItem(STORAGE_KEYS.USUARIO_RUT) || '';
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
