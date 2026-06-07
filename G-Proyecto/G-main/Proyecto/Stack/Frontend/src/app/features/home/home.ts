import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { STORAGE_KEYS } from '../../core/constants';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  constructor(private router: Router) {}

  irAlLogin(rol: string) {
    localStorage.setItem(STORAGE_KEYS.PORTAL, rol);
    this.router.navigate(['/login']);
  }
}