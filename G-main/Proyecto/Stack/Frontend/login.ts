import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  // Variables para guardar lo que el usuario escribe
  emailOrRut: string = '';
  password: string = '';

  // Variable para mostrar el título correcto
  portalSeleccionado: string = 'Administrador'; // por defecto

  // Variable para mostrar errores si pone mal la clave
  mensajeError: string = '';

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit() {
    // Cuando la pantalla carga, leemos qué opción eligió en el Home
    const guardado = localStorage.getItem('portalSeleccionado');
    if (guardado) this.portalSeleccionado = guardado;
  }

  // Función que se ejecuta al hacer clic en el botón
  iniciarSesion() {
    this.mensajeError = '';
    
    const inputLimpio = this.emailOrRut.trim();
    // 1. Armamos el paquete de datos para Spring Boot
    const body = {
      email: inputLimpio.includes('@') ? inputLimpio : null,
      rut: !inputLimpio.includes('@') ? inputLimpio : null,
      contrasena: this.password
    };

    // 2. Disparamos la petición real al Backend
    this.http.post('http://localhost:8080/api/auth/login', body).subscribe({
      next: (respuesta: any) => {
        console.log('¡Conectado exitosamente!', respuesta);
        // Guardamos el TOKEN real de seguridad en el navegador
        localStorage.setItem('token_clinica', respuesta.token);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('El backend rechazó el acceso:', error);
        this.mensajeError = 'Credenciales incorrectas. Revisa tu usuario y contraseña.';
      }
    });
  }
}
