import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-doctor-edit',
  imports: [CommonModule, FormsModule],
  templateUrl: './doctor-edit.html',
  styleUrl: './doctor-edit.scss',
})
export class DoctorEditComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);

  medicoEditando: any = null;
  cargando = true;

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.toastService.mostrar('ID de médico no válido', 'error');
      this.router.navigate(['/admin/usuarios']);
      return;
    }
    this.cargarMedico(id);
  }

  cargarMedico(id: number) {
    this.authService.listarMedicos().subscribe({
      next: (medicos) => {
        const medico = medicos.find((m: any) => m.idUsuario === id);
        if (medico) {
          this.medicoEditando = { ...medico };
          this.cargando = false;
        } else {
          this.toastService.mostrar('Médico no encontrado', 'error');
          this.router.navigate(['/admin/usuarios']);
        }
      },
      error: () => {
        this.toastService.mostrar('Error al cargar médico', 'error');
        this.cargando = false;
      },
    });
  }

  volver() {
    this.router.navigate(['/admin/usuarios']);
  }

  guardarEdicionMedico() {
    const payload: any = {};
    const original = this.medicoEditando;
    if (this.medicoEditando.rut !== original.rut) payload.rut = this.medicoEditando.rut;
    if (this.medicoEditando.nombre !== original.nombre) payload.nombre = this.medicoEditando.nombre;
    if (this.medicoEditando.email !== original.email) payload.email = this.medicoEditando.email;
    if (Object.keys(payload).length === 0) {
      this.router.navigate(['/admin/usuarios']);
      return;
    }
    this.authService.actualizarMedico(this.medicoEditando.idUsuario, payload).subscribe({
      next: () => {
        this.toastService.mostrar('Médico actualizado exitosamente', 'success');
        this.router.navigate(['/admin/usuarios']);
      },
      error: (err) => {
        const mensaje = err.error?.error || 'Error al actualizar médico';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }
}
