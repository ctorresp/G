import { Routes } from '@angular/router';
import { Login } from './features/login/login';
import { Home } from './features/home/home';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard, medicoGuard, coordinadorGuard, triajerGuard } from './core/guards/role.guard';

import { AdminLayout } from './features/admin/admin-layout';
import { PatientListComponent } from './features/admin/pages/patient-list/patient-list';
import { StaffManagementComponent } from './features/admin/pages/staff-management/staff-management';
import { UserManagementComponent } from './features/admin/pages/user-management/user-management';
import { PatientRegistrationComponent } from './features/admin/pages/patient-registration/patient-registration';
import { PatientDetailComponent } from './features/admin/pages/patient-detail/patient-detail';
import { DoctorRegistrationComponent } from './features/admin/pages/doctor-registration/doctor-registration';
import { DoctorEditComponent } from './features/admin/pages/doctor-edit/doctor-edit';
import { MedicoLayout } from './features/medico/medico-layout';
import { CoordinadorLayout } from './features/coordinador/coordinador-layout';
import { TriajerLayout } from './features/triajer/triajer-layout';
import { TriageComponent } from './features/triajer/pages/triage/triage';
import { MySurgeriesComponent } from './features/medico/pages/my-surgeries/my-surgeries';
import { PatientsComponent } from './features/medico/pages/patients/patients';
import { PatientDetailComponent as MedicoPatientDetailComponent } from './features/medico/pages/patient-detail/patient-detail';
import { PatientRequestSurgeryComponent } from './features/medico/pages/patient-request-surgery/patient-request-surgery';
import { RequestSurgeryComponent } from './features/medico/pages/request-surgery/request-surgery';
import { PendingRequestsComponent } from './features/coordinador/pages/pending-requests/pending-requests';
import { SurgeryMetricsComponent } from './features/coordinador/pages/surgery-metrics/surgery-metrics';
import { ReportsComponent } from './features/coordinador/pages/reports/reports';

export const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'admin',
    component: AdminLayout,
    canActivate: [authGuard, adminGuard],
    children: [
      { path: '', redirectTo: 'pacientes', pathMatch: 'full' },
      { path: 'pacientes', component: PatientListComponent },
      { path: 'personal', component: StaffManagementComponent },
      { path: 'usuarios', component: UserManagementComponent },
      { path: 'pacientes/registro', component: PatientRegistrationComponent },
      { path: 'pacientes/:rut', component: PatientDetailComponent },
      { path: 'medicos/registro', component: DoctorRegistrationComponent },
      { path: 'medicos/:id/edit', component: DoctorEditComponent },
    ],
  },
  {
    path: 'medico',
    component: MedicoLayout,
    canActivate: [authGuard, medicoGuard],
    children: [
      { path: '', redirectTo: 'pacientes', pathMatch: 'full' },
      { path: 'pacientes', component: PatientsComponent },
      { path: 'pacientes/:rut', component: MedicoPatientDetailComponent },
      { path: 'pacientes/:rut/solicitar-cirugia', component: PatientRequestSurgeryComponent },
      { path: 'cirugias', component: MySurgeriesComponent },
      { path: 'cirugias/solicitar', component: RequestSurgeryComponent },
    ],
  },
  {
    path: 'coordinador',
    component: CoordinadorLayout,
    canActivate: [authGuard, coordinadorGuard],
    children: [
      { path: '', redirectTo: 'pendientes', pathMatch: 'full' },
      { path: 'pendientes', component: PendingRequestsComponent },
      { path: 'metricas', component: SurgeryMetricsComponent },
      { path: 'reportes', component: ReportsComponent },
    ],
  },
  {
    path: 'triajer',
    component: TriajerLayout,
    canActivate: [authGuard, triajerGuard],
    children: [
      { path: '', redirectTo: 'triaje', pathMatch: 'full' },
      { path: 'triaje', component: TriageComponent },
    ],
  },
];
