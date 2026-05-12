import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Home } from './home';
import { PacientesList } from './pacientes-list/pacientes-list';
import { PacienteRegistro } from './paciente-registro/paciente-registro';
import { PacienteDetalle } from './paciente-detalle/paciente-detalle';

export const routes: Routes = [
    {path: '', component: Home, pathMatch: 'full'},
    {path: 'login', component: Login},
    {
        path: 'dashboard', 
        component: Dashboard,
        children: [
            { path: '', redirectTo: 'listado', pathMatch: 'full' },
            { path: 'listado', component: PacientesList },
            { path: 'registro', component: PacienteRegistro },
            { path: 'detalle', component: PacienteDetalle }
        ]
    }
];
