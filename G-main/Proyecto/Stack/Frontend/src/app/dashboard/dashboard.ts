import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  // Variable para controlar qué pantalla se ve
  vistaActual: 'listado' | 'registro' | 'detalle' = 'listado';

  mensajeError: string = '';

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

  // Lista de pacientes real desde el backend
  pacientes: any[] = [];

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

  constructor(private http: HttpClient) {}

  ngOnInit() {
    // Leemos el rol temporal que guardamos en la pantalla de Inicio
    const guardado = localStorage.getItem('portalSeleccionado');
    if (guardado) {
      this.rolActual = guardado;
    }
    
    // Cargar pacientes reales al iniciar el Dashboard
    this.cargarPacientes();
  }

  cargarPacientes() {
    this.http.get<any[]>('http://localhost:8080/api/pacientes').subscribe({
      next: (data) => {
        // Adaptamos la estructura del backend a la estructura visual de tu tabla
        this.pacientes = data.map(p => ({
          rut: p.rut,
          nombre: p.nombre,
          email: p.email,
          prevision: p.prevision,
          estado: p.estado,
          grupoSanguineo: p.datosClinicosSensibles?.grupoSanguineo || '',
          pesoKg: p.datosClinicosSensibles?.pesoKg || null,
          alturaCm: p.datosClinicosSensibles?.alturaCm || null,
          alergias: p.datosClinicosSensibles?.alergias || [],
          enfermedadesCronicas: p.datosClinicosSensibles?.enfermedadesCronicas || []
        }));
      },
      error: (error) => this.mensajeError = 'Error al cargar pacientes'
    });
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
    
    // Preparamos los datos tal como los exige tu backend
    const payload = {
      rut: this.nuevoPaciente.rut,
      nombre: this.nuevoPaciente.nombre,
      email: this.nuevoPaciente.email,
      contrasena: 'Paciente1234!', // Contraseña temporal por defecto
      prevision: this.nuevoPaciente.prevision,
      datosClinicosSensibles: {
        grupoSanguineo: this.nuevoPaciente.grupoSanguineo,
        pesoKg: this.nuevoPaciente.pesoKg,
        alturaCm: this.nuevoPaciente.alturaCm,
        alergias: this.alergiasRegList,
        enfermedadesCronicas: this.enfermedadesRegList
      }
    };

    // Enviamos el paciente a Spring Boot para guardarlo en la Base de Datos
    this.http.post('http://localhost:8080/api/auth/registro', payload).subscribe({
      next: (res) => {
        console.log('¡Paciente guardado exitosamente en BD!', res);
        this.cargarPacientes(); // Pedimos al backend la lista actualizada de inmediato
        
        // Limpiamos el formulario y volvemos a la vista principal
        this.nuevoPaciente = { rut: '', nombre: '', email: '', prevision: 'FONASA', pesoKg: null, alturaCm: null, grupoSanguineo: '' };
        this.alergiasRegList = [];
        this.enfermedadesRegList = [];
        this.cambiarVista('listado');
      },
      error: (err) => {
        console.error('Error al guardar en BD:', err);
        alert('Hubo un error al registrar. Es posible que el RUT o Correo ya estén ocupados.');
      }
    });
  }
}
