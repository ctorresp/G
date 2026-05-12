import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  // Variable para controlar qué pantalla se ve
  vistaActual: 'listado' | 'registro' | 'detalle' = 'listado';

  // Variable para saber quién inició sesión
  rolActual: string = 'Administrador';

  // Variable para guardar el paciente que estamos mirando
  pacienteSeleccionado: any = null;

  // Variable para controlar si estamos editando la ficha
  modoEdicion: boolean = false;

  alergiasEditList: string[] = [];
  enfermedadesEditList: string[] = [];
  nuevaAlergia: string = '';
  nuevaEnfermedad: string = '';

  alergiasRegList: string[] = [];
  enfermedadesRegList: string[] = [];
  nuevaAlergiaReg: string = '';
  nuevaEnfermedadReg: string = '';

  // Datos simulados (Mock) para poder diseñar la tabla visualmente
  pacientes: any[] = [
    { rut: '11111111-1', nombre: 'María González', email: 'paciente@rednorte.cl', prevision: 'FONASA', estado: true, grupoSanguineo: 'O+', pesoKg: 65.5, alturaCm: 168, alergias: ['Penicilina', 'Lactosa'], enfermedadesCronicas: [] },
    { rut: '22222222-2', nombre: 'Pedro Soto', email: 'paciente2@rednorte.cl', prevision: 'ISAPRE', estado: true, grupoSanguineo: 'A+', pesoKg: 82.0, alturaCm: 175.5, alergias: [], enfermedadesCronicas: ['Hipertensión'] },
    { rut: '33333333-3', nombre: 'Carlos Santana', email: 'carlos@rednorte.cl', prevision: 'PARTICULAR', estado: false, grupoSanguineo: 'B-', pesoKg: 78.0, alturaCm: 170, alergias: ['Ibuprofeno'], enfermedadesCronicas: ['Diabetes'] }
  ];

  // Objeto temporal para guardar los datos del formulario
  nuevoPaciente: any = {
    rut: '',
    nombre: '',
    email: '',
    prevision: 'FONASA',
    pesoKg: null,
    alturaCm: null,
    grupoSanguineo: '',
    alergias: '',
    enfermedadesCronicas: ''
  };

  // Calculan automáticamente las estadísticas para el panel superior
  get totalPacientes() {
    return this.pacientes.length;
  }

  get pacientesActivos() {
    return this.pacientes.filter(p => p.estado).length;
  }

  ngOnInit() {
    // Leemos el rol temporal que guardamos en la pantalla de Inicio
    const guardado = localStorage.getItem('portalSeleccionado');
    if (guardado) {
      this.rolActual = guardado;
    }
  }

  cambiarVista(vista: 'listado' | 'registro' | 'detalle') {
    this.vistaActual = vista;
  }

  verDetalle(paciente: any) {
    console.log('Clic en el ojo. Abriendo ficha médica de:', paciente.nombre);
    this.pacienteSeleccionado = paciente;
    this.modoEdicion = false; // Siempre que abrimos, entra en modo lectura
    this.cambiarVista('detalle');
  }

  editarPrevision(paciente: any) {
    const nuevaPrevision = prompt(`Editar previsión de ${paciente.nombre} (FONASA, ISAPRE, PARTICULAR):`, paciente.prevision);
    if (nuevaPrevision) {
      paciente.prevision = nuevaPrevision.toUpperCase();
      console.log('Previsión actualizada:', paciente);
    }
  }

  activarEdicion() {
    this.modoEdicion = true;
    // Copiamos las listas para poder editarlas sin afectar el original hasta guardar
    this.alergiasEditList = this.pacienteSeleccionado.alergias ? [...this.pacienteSeleccionado.alergias] : [];
    this.enfermedadesEditList = this.pacienteSeleccionado.enfermedadesCronicas ? [...this.pacienteSeleccionado.enfermedadesCronicas] : [];
    this.nuevaAlergia = '';
    this.nuevaEnfermedad = '';
  }

  agregarAlergia() {
    if (this.nuevaAlergia.trim()) {
      this.alergiasEditList.push(this.nuevaAlergia.trim());
      this.nuevaAlergia = '';
    }
  }

  removerAlergia(index: number) {
    this.alergiasEditList.splice(index, 1);
  }

  agregarEnfermedad() {
    if (this.nuevaEnfermedad.trim()) {
      this.enfermedadesEditList.push(this.nuevaEnfermedad.trim());
      this.nuevaEnfermedad = '';
    }
  }

  removerEnfermedad(index: number) {
    this.enfermedadesEditList.splice(index, 1);
  }

  guardarEdicion() {
    this.modoEdicion = false;
    this.pacienteSeleccionado.alergias = [...this.alergiasEditList];
    this.pacienteSeleccionado.enfermedadesCronicas = [...this.enfermedadesEditList];
    console.log('Ficha médica actualizada:', this.pacienteSeleccionado);
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
    console.log('Registrando paciente:', this.nuevoPaciente);
    // Simulamos que se guardó agregándolo a la lista temporal
    this.pacientes.push({
      rut: this.nuevoPaciente.rut,
      nombre: this.nuevoPaciente.nombre,
      email: this.nuevoPaciente.email,
      prevision: this.nuevoPaciente.prevision,
      estado: true,
      grupoSanguineo: this.nuevoPaciente.grupoSanguineo,
      pesoKg: this.nuevoPaciente.pesoKg,
      alturaCm: this.nuevoPaciente.alturaCm,
      alergias: [...this.alergiasRegList],
      enfermedadesCronicas: [...this.enfermedadesRegList]
    });
    // Limpiamos el formulario y volvemos al listado
    this.nuevoPaciente = { rut: '', nombre: '', email: '', prevision: 'FONASA', pesoKg: null, alturaCm: null, grupoSanguineo: '' };
    this.alergiasRegList = [];
    this.enfermedadesRegList = [];
    this.cambiarVista('listado');
  }
}
