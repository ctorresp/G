import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Dashboard } from './dashboard/dashboard';
import { Home } from './home';

export const routes: Routes = [
    {path: '', component: Home, pathMatch: 'full'},
    {path: 'login', component: Login},
    {path: 'dashboard', component: Dashboard}
];
