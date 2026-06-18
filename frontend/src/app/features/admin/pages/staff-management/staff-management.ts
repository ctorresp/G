import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../../../core/services/admin.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Usuario } from '../../../../core/interfaces';
import { StatCardComponent } from '../../../../shared/stat-card/stat-card';

@Component({
  selector: 'app-staff-management',
  imports: [CommonModule, FormsModule, RouterLink, StatCardComponent],
  templateUrl: './staff-management.html',
  styleUrl: './staff-management.scss',
})
export class StaffManagementComponent implements OnInit {
  private adminService = inject(AdminService);
  private toastService = inject(ToastService);
  usuarios: Usuario[] = [];
  busqueda = '';
  filtroRol = '';
  loading = signal(false);

  get personal() {
    return this.usuarios.filter(u => u.rol !== 'ROLE_PACIENTE');
  }

  get totalPersonal() { return this.personal.length; }
  get activos() { return this.personal.filter(u => u.estado).length; }
  get bloqueados() { return this.personal.filter(u => !u.estado).length; }

  get personalFiltrado() {
    const q = this.busqueda.toLowerCase().trim();
    const r = this.filtroRol;
    return this.personal.filter(u => {
      const matchTexto = !q || u.rut.toLowerCase().includes(q) || u.nombre.toLowerCase().includes(q) || u.email.toLowerCase().includes(q);
      const matchRol = !r || u.rol === r;
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
      error: () => { this.toastService.mostrar('Error al cargar personal', 'error'); this.loading.set(false); },
    });
  }

  toggleEstado(usuario: Usuario) {
    const nuevoEstado = !usuario.estado;
    const accion = nuevoEstado ? 'activar' : 'bloquear';
    this.adminService.cambiarEstadoUsuario(usuario.idUsuario, nuevoEstado).subscribe({
      next: () => { this.cargarUsuarios(); this.toastService.mostrar(`Usuario ${accion}do exitosamente`, 'success'); },
      error: () => this.toastService.mostrar('Error al cambiar estado', 'error'),
    });
  }

  rolLabel(rol: string): string {
    const map: Record<string, string> = {
      ROLE_ADMIN: 'Administrador',
      ROLE_MEDICO: 'Médico',
      ROLE_TRIAJER: 'Triajer',
      ROLE_COORDINADOR: 'Coordinador',
    };
    return map[rol] || rol;
  }

  rolClass(rol: string): string {
    const map: Record<string, string> = {
      ROLE_ADMIN: 'fonasa',
      ROLE_MEDICO: 'isapre',
      ROLE_TRIAJER: 'particular',
      ROLE_COORDINADOR: 'particular',
    };
    return map[rol] || '';
  }
}
