import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.scss',
})
export class ResetPasswordComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  mensaje: string = '';
  error: string = '';
  exito: boolean = false;
  cargando: boolean = false;

  ngOnInit() {
    this.token = this.route.snapshot.paramMap.get('token') || '';
    if (!this.token) {
      this.error = 'Token inválido o ausente. Solicita un nuevo restablecimiento.';
      this.cdr.detectChanges();
    }
  }

  restablecer() {
    this.error = '';
    this.mensaje = '';

    if (!this.token) {
      this.error = 'Token inválido. Solicita un nuevo restablecimiento.';
      this.cdr.detectChanges();
      return;
    }

    if (this.newPassword.length < 6) {
      this.error = 'La contraseña debe tener al menos 6 caracteres.';
      this.cdr.detectChanges();
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.error = 'Las contraseñas no coinciden.';
      this.cdr.detectChanges();
      return;
    }

    this.cargando = true;

    this.authService.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.exito = true;
        this.mensaje = 'Contraseña restablecida exitosamente.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.error = err.error?.mensaje || 'Error al restablecer la contraseña. El token puede haber expirado.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }
}
