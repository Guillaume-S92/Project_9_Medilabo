import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { Patients } from './pages/patients/patients';
import { PatientDetail } from './pages/patient-detail/patient-detail';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'patients', component: Patients, canActivate: [authGuard] },
  { path: 'patients/new', component: PatientDetail, canActivate: [authGuard] },
  { path: 'patients/:id', component: PatientDetail, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
