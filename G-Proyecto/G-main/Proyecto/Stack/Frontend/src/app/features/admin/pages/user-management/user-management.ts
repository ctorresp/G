import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminService } from '../../../../core/services/admin.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Usuario } from '../../../../core/interfaces';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule, StatCardComponent],
  templateUrl: './user-management.html',
  styleUrl: './user-management.scss',
})
export class UserManagementComponent implements OnInit {
  private adminService = inject(AdminService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  usuarios: Usuario[] = [];
  busquedaUsuarios = '';
  filtroRolUsuarios = '';
  loading = signal(false);

  get totalUsuarios() { return this.usuarios.length; }
  get usuariosActivos() { return this.usuarios.filter(u => u.estado).length; }
  get usuariosBloqueados() { return this.usuarios.filter(u => !u.estado).length; }

  get usuariosFiltrados() {
    const busqueda = this.busquedaUsuarios.toLowerCase().trim();
    const filtroRol = this.filtroRolUsuarios;
    return this.usuarios.filter(u => {
      const matchTexto = !busqueda || u.rut.toLowerCase().includes(busqueda) || u.nombre.toLowerCase().includes(busqueda) || u.email.toLowerCase().includes(busqueda);
      const matchRol = !filtroRol || u.rol === filtroRol;
      return matchTexto && matchRol;
    });
  }

  ngOnInit() {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    if (this.loading()) return;
    this.loading.set(true);
    this.adminService.listarUsuarios().subscribe({
      next: (data) => { this.usuarios = data; this.loading.set(false); },
      error: () => { this.toastService.mostrar('Error al cargar usuarios', 'error'); this.loading.set(false); },
    });
  }

  toggleEstadoUsuario(usuario: Usuario) {
    const nuevoEstado = !usuario.estado;
    const accion = nuevoEstado ? 'activar' : 'bloquear';
    this.adminService.cambiarEstadoUsuario(usuario.idUsuario, nuevoEstado).subscribe({
      next: () => { this.cargarUsuarios(); this.toastService.mostrar(`Usuario ${accion}do exitosamente`, 'success'); },
      error: () => this.toastService.mostrar('Error al cambiar estado del usuario', 'error'),
    });
  }

  esPaciente(usuario: Usuario): boolean {
    return usuario.rol === 'ROLE_PACIENTE';
  }

  esMedico(usuario: Usuario): boolean {
    return usuario.rol === 'ROLE_MEDICO';
  }

  verDetalle(usuario: Usuario) {
    this.router.navigate(['/admin/pacientes', usuario.rut]);
  }

  editarMedico(usuario: Usuario) {
    this.router.navigate(['/admin/medicos', usuario.idUsuario, 'edit']);
  }
}
