import { Routes } from '@angular/router';

import { Dashboard } from './dashboard/dashboard';
import { AircraftComponent } from './aircraft/aircraft';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { Maintenance } from './maintenance/maintenance';
import { TechnicianComponent } from './technician/technician';
import { InspectionComponent } from './inspection/inspection';
import { WorkNoteComponent } from './worknote/worknote';

import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';

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
    canActivate: [
      authGuard,
      roleGuard
    ],
    data: {
      roles: [
        'TECHNICIAN',
        'SUPERVISOR',
        'QA_INSPECTOR',
        'ADMIN'
      ]
    }
  },
  {
    path: 'maintenance',
    component: Maintenance,
    canActivate: [
      authGuard,
      roleGuard
    ],
    data: {
      roles: [
        'TECHNICIAN',
        'SUPERVISOR',
        'QA_INSPECTOR',
        'ADMIN'
      ]
    }
  },
  {
    path: 'technicians',
    component: TechnicianComponent,
    canActivate: [
      authGuard,
      roleGuard
    ],
    data: {
      roles: [
        'TECHNICIAN',
        'SUPERVISOR',
        'QA_INSPECTOR',
        'ADMIN'
      ]
    }
  },
  {
    path: 'inspections',
    component: InspectionComponent,
    canActivate: [
      authGuard,
      roleGuard
    ],
    data: {
      roles: [
        'QA_INSPECTOR',
        'ADMIN'
      ]
    }
  },
  {
    path: 'work-notes',
    component: WorkNoteComponent,
    canActivate: [
      authGuard,
      roleGuard
    ],
    data: {
      roles: [
        'TECHNICIAN',
        'SUPERVISOR',
        'ADMIN'
      ]
    }
  },
  {
    path: '**',
    redirectTo: ''
  }
];
