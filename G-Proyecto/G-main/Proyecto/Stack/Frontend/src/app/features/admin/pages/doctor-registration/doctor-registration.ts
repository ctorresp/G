import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Especialidad } from '../../../../core/interfaces';

@Component({
  selector: 'app-doctor-registration',
  imports: [CommonModule, FormsModule],
  templateUrl: './doctor-registration.html',
  styleUrl: './doctor-registration.scss',
})
export class DoctorRegistrationComponent implements OnInit {
  @ViewChild('medicoForm') medicoForm!: NgForm;

  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  nuevoMedico = this.crearNuevoMedico();
  especialidades: Especialidad[] = [];
  especialidadSeleccionada: number | null = null;

  erroresMedico = { rut: '', nombre: '', email: '', especialidad: '', contrasena: '' };
  mostrarPass = false;

  ngOnInit() {
    this.cargarEspecialidades();
  }

  cargarEspecialidades() {
    this.authService.listarEspecialidades().subscribe({
      next: (data) => { this.especialidades = data; },
      error: () => this.toastService.mostrar('Error al cargar especialidades', 'error'),
    });
  }

  crearNuevoMedico() {
    return { rut: '', nombre: '', email: '', contrasena: '' };
  }

  formatearRutIngresoMedico(valor: string) {
    this.nuevoMedico.rut = this._formatRut(valor);
  }

  private _formatRut(valor: string): string {
    if (!valor) return '';
    let rutLimpio = valor.replace(/[^0-9kK]/g, '').toUpperCase();
    if (rutLimpio.length > 1) {
      const cuerpo = rutLimpio.slice(0, -1);
      const dv = rutLimpio.slice(-1);
      return `${cuerpo}-${dv}`;
    }
    return rutLimpio;
  }

  registrarMedico() {
    this.erroresMedico = { rut: '', nombre: '', email: '', especialidad: '', contrasena: '' };
    let hasError = false;

    const rutLimpio = this.nuevoMedico.rut ? this.nuevoMedico.rut.trim() : '';
    if (!rutLimpio) { this.erroresMedico.rut = 'El RUT es obligatorio.'; hasError = true; }
    else if (!/^\d{7,8}-[\dkK]$/.test(rutLimpio)) { this.erroresMedico.rut = 'Formato inválido (Ej: 11111111-1)'; hasError = true; }
    else this.nuevoMedico.rut = rutLimpio;

    const nombreLimpio = this.nuevoMedico.nombre ? this.nuevoMedico.nombre.trim() : '';
    if (!nombreLimpio) { this.erroresMedico.nombre = 'El nombre es obligatorio.'; hasError = true; }
    else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/.test(nombreLimpio)) { this.erroresMedico.nombre = 'Solo letras.'; hasError = true; }

    const emailLimpio = this.nuevoMedico.email ? this.nuevoMedico.email.trim() : '';
    if (!emailLimpio) { this.erroresMedico.email = 'El correo es obligatorio.'; hasError = true; }
    else if (!/^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/.test(emailLimpio)) { this.erroresMedico.email = 'Correo inválido.'; hasError = true; }

    if (!this.especialidadSeleccionada) { this.erroresMedico.especialidad = 'Selecciona una especialidad.'; hasError = true; }

    const pass = this.nuevoMedico.contrasena || '';
    if (!pass) { this.erroresMedico.contrasena = 'La contraseña es obligatoria.'; hasError = true; }
    else if (pass.length < 8) { this.erroresMedico.contrasena = 'Mínimo 8 caracteres.'; hasError = true; }
    else if (!/[A-Z]/.test(pass)) { this.erroresMedico.contrasena = 'Debe contener al menos 1 mayúscula.'; hasError = true; }

    if (hasError) return;

    const espId = this.especialidadSeleccionada!;

    this.authService.registrarMedicoCompleto(this.nuevoMedico, espId).subscribe({
      next: () => {
        this.nuevoMedico = this.crearNuevoMedico();
        this.especialidadSeleccionada = null;
        this.erroresMedico = { rut: '', nombre: '', email: '', especialidad: '', contrasena: '' };
        setTimeout(() => this.medicoForm?.resetForm(), 0);
        this.toastService.mostrar('Médico registrado exitosamente', 'success');
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al registrar médico';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }
}
