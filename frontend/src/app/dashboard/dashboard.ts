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

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {

  aircraft: Aircraft[] = [];

  maintenanceTasks: MaintenanceTask[] = [];

  technicians: Technician[] = [];

  inspections: Inspection[] = [];

  loading = true;

  errorMessage = '';

  constructor(
    private readonly aircraftService: AircraftService,
    private readonly maintenanceTaskService: MaintenanceTaskService,
    private readonly technicianService: TechnicianService,
    private readonly inspectionService: InspectionService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAircraft();
    this.loadMaintenanceTasks();
    this.loadTechnicians();
    this.loadInspections();
  }

  loadAircraft(): void {

    this.aircraftService
      .getAllAircraft()
      .subscribe({

        next: (aircraft) => {

          this.aircraft = aircraft;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard aircraft:',
            error
          );

          this.errorMessage =
            'Unable to load aircraft information.';

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }

  loadMaintenanceTasks(): void {

    this.maintenanceTaskService
      .getAllTasks()
      .subscribe({

        next: (tasks) => {

          this.maintenanceTasks = tasks;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard maintenance tasks:',
            error
          );

          this.errorMessage =
            'Unable to load maintenance task information.';

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }

  loadTechnicians(): void {

    this.technicianService
      .getAllTechnicians()
      .subscribe({

        next: (technicians) => {

          this.technicians = technicians;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard technicians:',
            error
          );

          this.errorMessage =
            'Unable to load technician information.';

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }

  loadInspections(): void {

    this.inspectionService
      .getAllInspections()
      .subscribe({

        next: (inspections) => {

          this.inspections = inspections;

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading dashboard inspections:',
            error
          );

          this.errorMessage =
            'Unable to load inspection information.';

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }

  get totalAircraft(): number {
    return this.aircraft.length;
  }

  get openTasks(): number {

    return this.maintenanceTasks
      .filter(task =>
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
