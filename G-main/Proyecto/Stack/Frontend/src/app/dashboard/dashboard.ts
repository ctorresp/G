import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';

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

  // Término de búsqueda para filtrar la tabla
  terminoBusqueda: string = '';

  // Objeto temporal para guardar los datos del formulario
  nuevoPaciente: any = {
    rut: '',
    nombre: '',
    email: '',
    prevision: '',
    pesoKg: null,
    alturaCm: null,
    grupoSanguineo: '',
    alergias: '',
    enfermedadesCronicas: ''
  };

  // Variables para mostrar errores debajo de los campos de texto
  erroresRegistro = {
    rut: '',
    nombre: '',
    email: '',
    pesoKg: '',
    alturaCm: '',
    prevision: '',
    grupoSanguineo: ''
  };
  erroresEdicion = {
    pesoKg: '',
    alturaCm: ''
  };

  // Calculan automáticamente las estadísticas para el panel superior
  get totalPacientes() {
    return this.pacientes.length;
  }

  get pacientesActivos() {
    return this.pacientes.filter(p => p.estado).length;
  }

  // Devuelve la lista filtrada según lo que se escriba en el buscador
  get pacientesFiltrados() {
    if (!this.terminoBusqueda.trim()) return this.pacientes;
    const busqueda = this.terminoBusqueda.toLowerCase().trim();
    return this.pacientes.filter(p => 
      p.rut.toLowerCase().includes(busqueda) || 
      p.nombre.toLowerCase().includes(busqueda)
    );
  }

  // Obtenemos el token guardado en el login para enviarlo en cada petición
  private getHttpOptions() {
    const token = localStorage.getItem('token_clinica');
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef, private router: Router) {}

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
    this.http.get<any[]>('http://localhost:8080/api/pacientes', this.getHttpOptions()).subscribe({
      next: (data) => {
        // Adaptamos la estructura del backend a la estructura visual de tu tabla
        this.pacientes = data.map(p => ({
          rut: p.rut,
          nombre: p.nombre,
          email: p.email,
          prevision: p.prevision,
          estado: p.estado,
          grupoSanguineo: p.datosClinicosSensibles.grupoSanguineo || '',
          pesoKg: p.datosClinicosSensibles.pesoKg || null,
          alturaCm: p.datosClinicosSensibles.alturaCm || null,
          alergias: p.datosClinicosSensibles.alergias || [],
          enfermedadesCronicas: p.datosClinicosSensibles.enfermedadesCronicas || []
        }));
        console.log('Pacientes cargados con éxito:', this.pacientes);
        // Forzar a Angular a dibujar la tabla inmediatamente
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar pacientes:', error);
        this.mensajeError = 'Error al cargar pacientes';
      }
    });
  }

  cambiarVista(vista: 'listado' | 'registro' | 'detalle') {
    this.vistaActual = vista;
    if (vista === 'listado') {
      this.cargarPacientes();
    } else if (vista === 'registro') {
      // Limpiamos los errores al entrar a la pantalla
      this.erroresRegistro = { rut: '', nombre: '', email: '', pesoKg: '', alturaCm: '', prevision: '', grupoSanguineo: '' };
    }
  }

  // Formatea el RUT automáticamente (ej: 123456789 -> 12345678-9)
  formatearRutIngreso(valor: string) {
    if (!valor) {
      this.nuevoPaciente.rut = '';
      return;
    }
    // Quita todo lo que no sea número o K, y lo pasa a mayúscula
    let rutLimpio = valor.replace(/[^0-9kK]/g, '').toUpperCase();
    
    if (rutLimpio.length > 1) {
      const cuerpo = rutLimpio.slice(0, -1);
      const dv = rutLimpio.slice(-1);
      this.nuevoPaciente.rut = `${cuerpo}-${dv}`;
    } else {
      this.nuevoPaciente.rut = rutLimpio;
    }
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
    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
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
    this.erroresEdicion = { pesoKg: '', alturaCm: '' };
    let hasError = false;

    // Validaciones de Peso y Altura al Editar
    if (this.pacienteSeleccionado.pesoKg === null || this.pacienteSeleccionado.pesoKg === undefined || this.pacienteSeleccionado.pesoKg === '') {
      this.erroresEdicion.pesoKg = 'Falta información: El peso es obligatorio.';
      hasError = true;
    } else {
      const pesoStrEdit = this.pacienteSeleccionado.pesoKg.toString();
      if (this.pacienteSeleccionado.pesoKg <= 0 || this.pacienteSeleccionado.pesoKg >= 1000) {
        this.erroresEdicion.pesoKg = 'Formato incorrecto: El peso debe ser mayor a 0 y menor a 1000 kg.';
        hasError = true;
      } else if (!/^\d+(\.\d{1})?$/.test(pesoStrEdit)) {
        this.erroresEdicion.pesoKg = 'Formato incorrecto: El peso solo puede tener hasta un decimal (Ejemplo: 78.7).';
        hasError = true;
      }
    }

    if (this.pacienteSeleccionado.alturaCm === null || this.pacienteSeleccionado.alturaCm === undefined || this.pacienteSeleccionado.alturaCm === '') {
      this.erroresEdicion.alturaCm = 'Falta información: La altura es obligatoria.';
      hasError = true;
    } else {
      const alturaEdit = Number(this.pacienteSeleccionado.alturaCm);
      if (alturaEdit < 2 || alturaEdit > 250) {
        this.erroresEdicion.alturaCm = 'Formato incorrecto: La altura debe ser mínimo 2 cm y máximo 250 cm.';
        hasError = true;
      } else if (!Number.isInteger(alturaEdit)) {
        this.erroresEdicion.alturaCm = 'Formato incorrecto: La altura debe ser un número entero en centímetros, sin decimales (Ejemplo: 175).';
        hasError = true;
      }
    }

    if (hasError) return;

    this.modoEdicion = false;
    this.pacienteSeleccionado.alergias = [...this.alergiasEditList];
    this.pacienteSeleccionado.enfermedadesCronicas = [...this.enfermedadesEditList];
    console.log('Ficha médica actualizada:', this.pacienteSeleccionado);

    // Preparamos los datos
    const payload = {
      prevision: this.pacienteSeleccionado.prevision,
      email: this.pacienteSeleccionado.email,
      datosClinicosSensibles: {
        grupoSanguineo: this.pacienteSeleccionado.grupoSanguineo,
        pesoKg: this.pacienteSeleccionado.pesoKg,
        alturaCm: this.pacienteSeleccionado.alturaCm,
        alergias: this.pacienteSeleccionado.alergias,
        enfermedadesCronicas: this.pacienteSeleccionado.enfermedadesCronicas
      }
    };

    // Enviamos los cambios al Backend
    this.http.put(`http://localhost:8080/api/pacientes/rut/${this.pacienteSeleccionado.rut}/clinicos`, payload, this.getHttpOptions()).subscribe({
      next: (res) => {
        this.cargarPacientes(); // Recargamos para reflejar cambios
      },
      error: (err) => alert('Hubo un error al guardar los cambios en la BD.')
    });
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
    this.erroresRegistro = { rut: '', nombre: '', email: '', pesoKg: '', alturaCm: '', prevision: '', grupoSanguineo: '' };
    let hasError = false;
    
    const rutLimpio = this.nuevoPaciente.rut ? this.nuevoPaciente.rut.trim() : '';
    if (rutLimpio === '') {
      this.erroresRegistro.rut = 'Falta información: El RUT es obligatorio.';
      hasError = true;
    } else {
      const rutRegex = /^([0-9]{7,8}-[0-9kK]{1})$/;
      if (!rutRegex.test(rutLimpio)) {
        this.erroresRegistro.rut = 'Formato incorrecto: El RUT debe escribirse sin puntos y con guión (Ejemplo: 12345678-9).';
        hasError = true;
      }
    }

    const nombreLimpio = this.nuevoPaciente.nombre ? this.nuevoPaciente.nombre.trim() : '';
    if (nombreLimpio === '') {
      this.erroresRegistro.nombre = 'Falta información: El nombre es obligatorio.';
      hasError = true;
    } else {
      const nombreRegex = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;
      if (!nombreRegex.test(nombreLimpio)) {
        this.erroresRegistro.nombre = 'Formato incorrecto: El nombre solo debe contener letras.';
        hasError = true;
      }
    }

    if (!this.nuevoPaciente.email || !this.nuevoPaciente.email.includes('@')) {
      this.erroresRegistro.email = 'Formato incorrecto: Debes ingresar un correo electrónico válido.';
      hasError = true;
    }

    if (this.nuevoPaciente.pesoKg === null || this.nuevoPaciente.pesoKg === undefined || this.nuevoPaciente.pesoKg === '') {
      this.erroresRegistro.pesoKg = 'Falta información: El peso es obligatorio.';
      hasError = true;
    } else {
      const pesoStr = this.nuevoPaciente.pesoKg.toString();
      if (this.nuevoPaciente.pesoKg <= 0 || this.nuevoPaciente.pesoKg >= 1000) {
        this.erroresRegistro.pesoKg = 'Formato incorrecto: El peso debe ser mayor a 0 y menor a 1000 kg.';
        hasError = true;
      } else if (!/^\d+(\.\d{1})?$/.test(pesoStr)) {
        this.erroresRegistro.pesoKg = 'Formato incorrecto: El peso solo puede tener hasta un decimal (Ejemplo: 78.7).';
        hasError = true;
      }
    }

    if (this.nuevoPaciente.alturaCm === null || this.nuevoPaciente.alturaCm === undefined || this.nuevoPaciente.alturaCm === '') {
      this.erroresRegistro.alturaCm = 'Falta información: La altura es obligatoria.';
      hasError = true;
    } else {
      const altura = Number(this.nuevoPaciente.alturaCm);
      if (altura < 2 || altura > 250) {
        this.erroresRegistro.alturaCm = 'Formato incorrecto: La altura debe ser mínimo 2 cm y máximo 250 cm.';
        hasError = true;
      } else if (!Number.isInteger(altura)) {
        this.erroresRegistro.alturaCm = 'Formato incorrecto: La altura debe ser un número entero en centímetros, sin decimales (Ejemplo: 175).';
        hasError = true;
      }
    }

    if (!this.nuevoPaciente.prevision || this.nuevoPaciente.prevision === '') {
      this.erroresRegistro.prevision = 'Falta información: Selecciona una previsión médica.';
      hasError = true;
    }

    if (!this.nuevoPaciente.grupoSanguineo || this.nuevoPaciente.grupoSanguineo === '') {
      this.erroresRegistro.grupoSanguineo = 'Falta información: Selecciona un grupo sanguíneo.';
      hasError = true;
    }

    if (hasError) return; // Detenemos el envío si hay algún error

    // Preparamos los datos tal como los exige tu backend
    const payload = {
      rut: rutLimpio,
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
    this.http.post('http://localhost:8080/api/auth/registro', payload, this.getHttpOptions()).subscribe({
      next: (res) => {
        console.log('¡Paciente guardado exitosamente en BD!', res);
        this.cargarPacientes(); // Pedimos al backend la lista actualizada de inmediato
        
        // Limpiamos el formulario y volvemos a la vista principal
        this.nuevoPaciente = { rut: '', nombre: '', email: '', prevision: '', pesoKg: null, alturaCm: null, grupoSanguineo: '' };
        this.alergiasRegList = [];
        this.enfermedadesRegList = [];
        this.cambiarVista('listado');
      },
      error: (err) => {
        console.error('Error al guardar en BD:', err);
        
        // 2. Extraer el mensaje exacto de error que manda el Backend
        let mensajeDetallado = 'Hubo un error al registrar. Es posible que el RUT o Correo ya estén ocupados.';
        if (err.error) {
          if (typeof err.error === 'string') {
            mensajeDetallado = err.error;
          } else if (err.error.errors && Array.isArray(err.error.errors)) {
            // Atrapa los errores de la anotación @Valid del Backend de Spring Boot
            mensajeDetallado = err.error.errors.map((e: any) => e.defaultMessage || 'Dato inválido').join('\n');
          } else if (err.error.mensaje) {
            mensajeDetallado = err.error.mensaje;
          } else if (err.error.error) {
            mensajeDetallado = err.error.error;
          } else if (typeof err.error === 'object') {
            const erroresExtraidos = Object.values(err.error).filter(val => typeof val === 'string').join('\n');
            if (erroresExtraidos) mensajeDetallado = erroresExtraidos;
          }
        }
        
        alert('Error al registrar:\n' + mensajeDetallado);
      }
    });
  }

  cerrarSesion() {
    // Borramos el token de seguridad para cerrar la sesión real
    localStorage.removeItem('token_clinica');
    // Devolvemos al usuario a la pantalla de inicio
    this.router.navigate(['/']);
  }
}
