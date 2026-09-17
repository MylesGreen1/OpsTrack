import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { RouterLink } from '@angular/router';

import {
  Aircraft,
  AircraftService
} from '../aircraft/aircraft.service';

import {
  MaintenanceTask,
  MaintenanceTaskService
} from '../maintenance/maintenance-task.service';

import {
  Technician,
  TechnicianService
} from '../technician/technician.service';

import {
  Inspection,
  InspectionService
} from '../inspection/inspection.service';

import {
  AuthService,
  CurrentUser,
  UserRole
} from '../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  aircraft: Aircraft[] = [];
  maintenanceTasks: MaintenanceTask[] = [];
  technicians: Technician[] = [];
  inspections: Inspection[] = [];

  currentUser: CurrentUser | null = null;

  loading = true;
  errorMessage = '';

  constructor(
    private readonly aircraftService: AircraftService,
    private readonly maintenanceTaskService: MaintenanceTaskService,
    private readonly technicianService: TechnicianService,
    private readonly inspectionService: InspectionService,
    private readonly authService: AuthService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {

    this.authService
      .getCurrentUser()
      .subscribe({

        next: (user: CurrentUser) => {

          this.currentUser = user;

          this.loadAllowedDashboardData();

          this.changeDetectorRef.markForCheck();
        },

        error: (error: unknown) => {

          console.error(
            'Error loading current user:',
            error
          );

          this.errorMessage =
            'Unable to load current user information.';

          this.loading = false;

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  loadAllowedDashboardData(): void {

    if (this.canViewAircraft) {
      this.loadAircraft();
    }

    if (this.canViewMaintenanceTasks) {
      this.loadMaintenanceTasks();
    }

    if (this.canViewTechnicians) {
      this.loadTechnicians();
    }

    if (this.canViewInspections) {
      this.loadInspections();
    }

    this.loading = false;

    this.changeDetectorRef.markForCheck();
  }

  loadAircraft(): void {

    this.aircraftService
      .getAllAircraft()
      .subscribe({

        next: (aircraft) => {

          this.aircraft = aircraft;

          this.changeDetectorRef.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard aircraft:',
            error
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  loadMaintenanceTasks(): void {

    this.maintenanceTaskService
      .getAllTasks()
      .subscribe({

        next: (tasks) => {

          this.maintenanceTasks = tasks;

          this.changeDetectorRef.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard maintenance tasks:',
            error
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  loadTechnicians(): void {

    this.technicianService
      .getAllTechnicians()
      .subscribe({

        next: (technicians) => {

          this.technicians = technicians;

          this.changeDetectorRef.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard technicians:',
            error
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  loadInspections(): void {

    this.inspectionService
      .getAllInspections()
      .subscribe({

        next: (inspections) => {

          this.inspections = inspections;

          this.changeDetectorRef.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard inspections:',
            error
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  hasRole(
    ...roles: UserRole[]
  ): boolean {

    if (!this.currentUser) {
      return false;
    }

    return roles.includes(
      this.currentUser.role
    );
  }

  get canViewAircraft(): boolean {

    return this.hasRole(
      'TECHNICIAN',
      'SUPERVISOR',
      'QA_INSPECTOR',
      'ADMIN'
    );
  }

  get canViewMaintenanceTasks(): boolean {

    return this.hasRole(
      'TECHNICIAN',
      'SUPERVISOR',
      'QA_INSPECTOR',
      'ADMIN'
    );
  }

  get canViewTechnicians(): boolean {

    return this.hasRole(
      'TECHNICIAN',
      'SUPERVISOR',
      'QA_INSPECTOR',
      'ADMIN'
    );
  }

  get canViewInspections(): boolean {

    return this.hasRole(
      'QA_INSPECTOR',
      'ADMIN'
    );
  }

  get canViewWorkNotes(): boolean {

    return this.hasRole(
      'TECHNICIAN',
      'SUPERVISOR',
      'ADMIN'
    );
  }

  get canViewMyTasks(): boolean {

    return this.hasRole(
      'TECHNICIAN'
    );
  }

  get totalAircraft(): number {
    return this.aircraft.length;
  }

  get openTasks(): number {

    return this.maintenanceTasks
      .filter(
        task =>
          task.status !== 'COMPLETED'
      )
      .length;
  }

  get totalTechnicians(): number {
    return this.technicians.length;
  }

  get totalInspections(): number {
    return this.inspections.length;
  }
}
