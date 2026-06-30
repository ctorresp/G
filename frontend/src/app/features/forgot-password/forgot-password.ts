import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.scss',
})
export class ForgotPasswordComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email: string = '';
  error: string = '';
  cargando: boolean = false;

  enviarLink() {
    if (!this.email || !this.email.includes('@')) {
      this.error = 'Ingresa un correo electrónico válido.';
      return;
    }

    this.cargando = true;
    this.error = '';

    this.authService.forgotPassword(this.email).subscribe({
      next: (res: any) => {
        this.cargando = false;
        if (res.token) {
          this.router.navigate(['/reset-password', res.token]);
        }
      },
      error: () => {
        this.error = 'Error al procesar la solicitud. Intenta nuevamente.';
        this.cargando = false;
      },
    });
  }
}
