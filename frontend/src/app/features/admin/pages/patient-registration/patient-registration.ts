import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { PacienteRegistroPayload, Medico } from '../../../../core/interfaces';

@Component({
  selector: 'app-patient-registration',
  imports: [CommonModule, FormsModule],
  templateUrl: './patient-registration.html',
  styleUrl: './patient-registration.scss',
})
export class PatientRegistrationComponent implements OnInit {
  @ViewChild('registroForm') registroForm!: NgForm;

  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  medicos = signal<Medico[]>([]);
  medicosFiltrados: Medico[] = [];
  medicoSeleccionado: Medico | null = null;
  medicoBusqueda = '';
  mostrarDropdownMedico = false;

  nuevoPaciente = {
    rut: '', nombre: '', email: '', prevision: '',
    pesoKg: null as number | null, alturaCm: null as number | null, grupoSanguineo: '',
    contactoEmergenciaNombre: '', contactoEmergenciaTelefono: '',
  };

  alergiasRegList: string[] = [];
  enfermedadesRegList: string[] = [];
  nuevaAlergiaReg = '';
  nuevaEnfermedadReg = '';

  erroresRegistro = {
    rut: '', nombre: '', email: '', pesoKg: '', alturaCm: '',
    prevision: '', grupoSanguineo: '',
    contactoEmergenciaNombre: '', contactoEmergenciaTelefono: '',
    idMedico: '',
  };

  ngOnInit() {
    this.cargarMedicos();
  }

  cargarMedicos() {
    this.authService.listarMedicos().subscribe({
      next: (data) => this.medicos.set(data),
      error: () => this.toastService.mostrar('Error al cargar médicos', 'error'),
    });
  }

  buscarMedico() {
    const termino = this.medicoBusqueda.toLowerCase().trim();
    if (!termino) {
      this.medicosFiltrados = this.medicos();
    } else {
      this.medicosFiltrados = this.medicos().filter(m =>
        m.nombre.toLowerCase().includes(termino)
      );
    }
    this.mostrarDropdownMedico = true;
  }

  seleccionarMedico(m: Medico) {
    this.medicoSeleccionado = m;
    this.medicoBusqueda = `${m.nombre} (ID: ${m.idUsuario})`;
    this.mostrarDropdownMedico = false;
  }

  limpiarMedico() {
    this.medicoSeleccionado = null;
    this.medicoBusqueda = '';
    this.medicosFiltrados = [];
  }

  cerrarDropdown() {
    setTimeout(() => this.mostrarDropdownMedico = false, 200);
  }

  formatearRutIngreso(valor: string) {
    this.nuevoPaciente.rut = this._formatRut(valor);
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

  agregarAlergiaReg() {
    if (this.nuevaAlergiaReg.trim()) {
      this.alergiasRegList.push(this.nuevaAlergiaReg.trim());
      this.nuevaAlergiaReg = '';
    }
  }

  removerAlergiaReg(index: number) {
    this.alergiasRegList.splice(index, 1);
  }

  agregarEnfermedadReg() {
    if (this.nuevaEnfermedadReg.trim()) {
      this.enfermedadesRegList.push(this.nuevaEnfermedadReg.trim());
      this.nuevaEnfermedadReg = '';
    }
  }

  removerEnfermedadReg(index: number) {
    this.enfermedadesRegList.splice(index, 1);
  }

  registrar() {
    this.erroresRegistro = {
      rut: '', nombre: '', email: '', pesoKg: '', alturaCm: '',
      prevision: '', grupoSanguineo: '',
      contactoEmergenciaNombre: '', contactoEmergenciaTelefono: '',
      idMedico: '',
    };
    let hasError = false;

    const rutLimpio = this.nuevoPaciente.rut ? this.nuevoPaciente.rut.trim() : '';
    if (!rutLimpio) { this.erroresRegistro.rut = 'El RUT es obligatorio.'; hasError = true; }
    else if (!/^([0-9]{7,8}-[0-9kK]{1})$/.test(rutLimpio)) { this.erroresRegistro.rut = 'Formato: 12345678-9'; hasError = true; }

    const nombreLimpio = this.nuevoPaciente.nombre ? this.nuevoPaciente.nombre.trim() : '';
    if (!nombreLimpio) { this.erroresRegistro.nombre = 'El nombre es obligatorio.'; hasError = true; }
    else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/.test(nombreLimpio)) { this.erroresRegistro.nombre = 'Solo letras.'; hasError = true; }

    if (!this.nuevoPaciente.email || !this.nuevoPaciente.email.includes('@')) {
      this.erroresRegistro.email = 'Correo electrónico válido.'; hasError = true;
    }

    if (this.nuevoPaciente.pesoKg == null) {
      this.erroresRegistro.pesoKg = 'El peso es obligatorio.'; hasError = true;
    } else {
      const pesoStr = this.nuevoPaciente.pesoKg.toString();
      if (this.nuevoPaciente.pesoKg <= 0 || this.nuevoPaciente.pesoKg >= 1000) { this.erroresRegistro.pesoKg = 'Debe ser mayor a 0 y menor a 1000 kg.'; hasError = true; }
      else if (!/^\d+(\.\d{1})?$/.test(pesoStr)) { this.erroresRegistro.pesoKg = 'Solo un decimal (Ej: 78.7).'; hasError = true; }
    }

    if (this.nuevoPaciente.alturaCm == null) {
      this.erroresRegistro.alturaCm = 'La altura es obligatoria.'; hasError = true;
    } else {
      const altura = Number(this.nuevoPaciente.alturaCm);
      if (altura < 2 || altura > 250) { this.erroresRegistro.alturaCm = 'Mínimo 2 cm, máximo 250 cm.'; hasError = true; }
      else if (!Number.isInteger(altura)) { this.erroresRegistro.alturaCm = 'Número entero en cm (Ej: 175).'; hasError = true; }
    }

    if (!this.nuevoPaciente.prevision) { this.erroresRegistro.prevision = 'Selecciona una previsión.'; hasError = true; }
    if (!this.nuevoPaciente.grupoSanguineo) { this.erroresRegistro.grupoSanguineo = 'Selecciona un grupo sanguíneo.'; hasError = true; }

    if (!this.medicoSeleccionado) { this.erroresRegistro.idMedico = 'Debes seleccionar un médico.'; hasError = true; }

    const contactoNombre = this.nuevoPaciente.contactoEmergenciaNombre?.trim() || '';
    if (!contactoNombre) { this.erroresRegistro.contactoEmergenciaNombre = 'El nombre de contacto es obligatorio.'; hasError = true; }
    else if (!/^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/.test(contactoNombre)) { this.erroresRegistro.contactoEmergenciaNombre = 'Solo letras.'; hasError = true; }

    const contactoTel = this.nuevoPaciente.contactoEmergenciaTelefono?.trim() || '';
    if (!contactoTel) { this.erroresRegistro.contactoEmergenciaTelefono = 'El teléfono de contacto es obligatorio.'; hasError = true; }
    else if (!/^\+?\d{1,4}[\s-]?\d{1,4}[\s-]?\d{4,8}$/.test(contactoTel)) { this.erroresRegistro.contactoEmergenciaTelefono = 'Formato inválido (Ej: +56912345678).'; hasError = true; }

    if (hasError) return;

    const payload = {
      rut: rutLimpio, nombre: this.nuevoPaciente.nombre, email: this.nuevoPaciente.email,
      contrasena: 'Paciente1234!', prevision: this.nuevoPaciente.prevision,
      grupoSanguineo: this.nuevoPaciente.grupoSanguineo,
      pesoKg: this.nuevoPaciente.pesoKg,
      alturaCm: this.nuevoPaciente.alturaCm,
      alergias: this.alergiasRegList.join(','),
      enfermedadesCronicas: this.enfermedadesRegList.join(','),
      idMedico: this.medicoSeleccionado?.idUsuario,
      contactoEmergenciaNombre: this.nuevoPaciente.contactoEmergenciaNombre || undefined,
      contactoEmergenciaTelefono: this.nuevoPaciente.contactoEmergenciaTelefono || undefined,
    };

    this.authService.registrarPaciente(payload).subscribe({
      next: () => {
        this.toastService.mostrar('Paciente registrado exitosamente', 'success');
        this.nuevoPaciente = { rut: '', nombre: '', email: '', prevision: '', pesoKg: null, alturaCm: null, grupoSanguineo: '', contactoEmergenciaNombre: '', contactoEmergenciaTelefono: '' };
        this.alergiasRegList = []; this.enfermedadesRegList = [];
        this.limpiarMedico();
        setTimeout(() => this.registroForm?.resetForm(), 0);
      },
      error: (err) => {
        let mensaje = 'Error al registrar. RUT o Correo ya ocupados.';
        if (err.error) {
          if (typeof err.error === 'string') mensaje = err.error;
          else if (err.error.errors && Array.isArray(err.error.errors)) mensaje = err.error.errors.map((e: any) => e.defaultMessage || 'Dato inválido').join('\n');
          else if (err.error.mensaje) mensaje = err.error.mensaje;
          else if (err.error.error) mensaje = err.error.error;
        }
        this.toastService.mostrar(mensaje, 'error');
      },
    });
  }
}
