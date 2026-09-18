import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  Inspection,
  InspectionService,
  InspectionStatus
} from './inspection.service';

import {
  MaintenanceTask,
  MaintenanceTaskService
} from '../maintenance/maintenance-task.service';

import {
  Technician,
  TechnicianService
} from '../technician/technician.service';

@Component({
  selector: 'app-inspection',
  imports: [
    RouterLink,
    FormsModule,
    DatePipe
  ],
  templateUrl: './inspection.html',
  styleUrl: './inspection.css'
})
export class InspectionComponent implements OnInit {

  inspections: Inspection[] = [];
  maintenanceTasks: MaintenanceTask[] = [];
  technicians: Technician[] = [];

  loading = true;
  saving = false;

  errorMessage = '';
  formErrorMessage = '';

  showInspectionForm = false;

  selectedMaintenanceTaskId: number | null = null;
  selectedInspectorId: number | null = null;

  inspectionForm = {
    status: 'PENDING' as InspectionStatus,
    comments: ''
  };

  constructor(
    private readonly inspectionService: InspectionService,
    private readonly maintenanceTaskService: MaintenanceTaskService,
    private readonly technicianService: TechnicianService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadInspections();
    this.loadMaintenanceTasks();
    this.loadTechnicians();
  }

  loadInspections(): void {
    this.loading = true;
    this.errorMessage = '';

    this.inspectionService
      .getAllInspections()
      .subscribe({
        next: (inspections) => {
          this.inspections = inspections;
          this.loading = false;
          this.changeDetectorRef.markForCheck();
        },
        error: (error) => {
          console.error(
            'Error loading inspections:',
            error
          );

          this.errorMessage =
            'Unable to load inspections. Please try again.';

          this.loading = false;
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
            'Error loading maintenance tasks:',
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
            'Error loading technicians:',
            error
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  openInspectionForm(): void {
    this.resetInspectionForm();
    this.showInspectionForm = true;
    this.changeDetectorRef.markForCheck();
  }

  closeInspectionForm(): void {
    this.showInspectionForm = false;
    this.resetInspectionForm();
    this.changeDetectorRef.markForCheck();
  }

  saveInspection(): void {
    this.formErrorMessage = '';

    if (this.selectedMaintenanceTaskId === null) {
      this.formErrorMessage =
        'Please select a maintenance task.';
      return;
    }

    if (this.selectedInspectorId === null) {
      this.formErrorMessage =
        'Please select an inspector.';
      return;
    }

    const comments =
      this.inspectionForm.comments.trim();

    if (!comments) {
      this.formErrorMessage =
        'Inspection comments are required.';
      return;
    }

    this.saving = true;

    this.inspectionService
      .createInspection(
        this.selectedMaintenanceTaskId,
        this.selectedInspectorId,
        this.inspectionForm.status,
        comments
      )
      .subscribe({
        next: (createdInspection) => {
          this.inspections = [
            ...this.inspections,
            createdInspection
          ];

          this.saving = false;
          this.showInspectionForm = false;

          this.resetInspectionForm();

          /*
           * APPROVED and REJECTED inspections can change
           * the associated maintenance task status in the
           * backend, so reload the maintenance task data.
           */
          this.loadMaintenanceTasks();

          this.changeDetectorRef.markForCheck();
        },
        error: (error) => {
          console.error(
            'Error creating inspection:',
            error
          );

          this.formErrorMessage =
            'Unable to create inspection. Please try again.';

          this.saving = false;
          this.changeDetectorRef.markForCheck();
        }
      });
  }

  resetInspectionForm(): void {
    this.selectedMaintenanceTaskId = null;
    this.selectedInspectorId = null;

    this.inspectionForm = {
      status: 'PENDING',
      comments: ''
    };

    this.formErrorMessage = '';
  }

  get totalInspections(): number {
    return this.inspections.length;
  }

  get pendingInspections(): number {
    return this.inspections
      .filter(
        inspection =>
          inspection.status === 'PENDING'
      )
      .length;
  }

  get approvedInspections(): number {
    return this.inspections
      .filter(
        inspection =>
          inspection.status === 'APPROVED'
      )
      .length;
  }

  get rejectedInspections(): number {
    return this.inspections
      .filter(
        inspection =>
          inspection.status === 'REJECTED'
      )
      .length;
  }

  get activeTechnicians(): Technician[] {
    return this.technicians
      .filter(
        technician => technician.active
      );
  }
}
