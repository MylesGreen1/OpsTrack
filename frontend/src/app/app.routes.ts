import { Routes } from '@angular/router';

import { Dashboard } from './dashboard/dashboard';
import { AircraftComponent } from './aircraft/aircraft';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';

import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: '',
    component: Dashboard,
    canActivate: [authGuard]
  },
  {
    path: 'aircraft',
    component: AircraftComponent,
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
