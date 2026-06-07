import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CirugiaService } from '../../../../core/services/cirugia.service';
import { PacienteService } from '../../../../core/services/paciente.service';
import { AuthService } from '../../../../core/services/auth.service';
import { PabellonService } from '../../../../core/services/pabellon.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Paciente, Medico, Especialidad } from '../../../../core/interfaces';

@Component({
  selector: 'app-request-surgery',
  imports: [CommonModule, FormsModule],
  templateUrl: './request-surgery.html',
  styleUrl: './request-surgery.scss',
})
export class RequestSurgeryComponent implements OnInit {
  private cirugiaService = inject(CirugiaService);
  private pacienteService = inject(PacienteService);
  private authService = inject(AuthService);
  private pabellonService = inject(PabellonService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  pacientes: Paciente[] = [];
  medicos: Medico[] = [];
  especialidades: Especialidad[] = [];

  solicitudCirugia = {
    pacienteRut: '',
    especialidadId: null as number | null,
    medicoRut: '',
    notas: '',
    fechaProgramada: '',
  };

  ngOnInit() {
    this.cargarPacientes();
    this.cargarMedicos();
    this.cargarEspecialidades();
  }

  cargarPacientes() {
    this.pacienteService.listarTodos().subscribe({
      next: (data: any) => {
        const raw: any[] = Array.isArray(data) ? data : data.value ?? [];
        this.pacientes = raw;
      },
    });
  }

  cargarMedicos() {
    this.authService.listarMedicos().subscribe({
      next: (data) => {
        this.medicos = data;
      },
    });
  }

  cargarEspecialidades() {
    this.pabellonService.listarEspecialidades().subscribe({
      next: (data) => {
        this.especialidades = data;
      },
    });
  }

  solicitarCirugiaMedico() {
    const payload = {
      pacienteRut: this.solicitudCirugia.pacienteRut,
      especialidad: { idEspecialidad: this.solicitudCirugia.especialidadId! },
      medicoRut: this.solicitudCirugia.medicoRut,
      notas: this.solicitudCirugia.notas,
      fechaProgramada: this.solicitudCirugia.fechaProgramada || null,
    };

    this.cirugiaService.solicitar(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Solicitud enviada al coordinador', 'success');
        this.router.navigate(['/medico/cirugias']);
      },
      error: (err) => {
        const mensaje = err.error?.mensaje || 'Error al solicitar cirugía';
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }

  volver() {
    this.router.navigate(['/medico/cirugias']);
  }
}
