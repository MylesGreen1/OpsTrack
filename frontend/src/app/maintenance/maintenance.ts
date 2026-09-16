import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  Aircraft,
  AircraftService
} from '../aircraft/aircraft.service';

import {
  MaintenancePriority,
  MaintenanceStatus,
  MaintenanceTask,
  MaintenanceTaskRequest,
  MaintenanceTaskService
} from './maintenance-task.service';

@Component({
  selector: 'app-maintenance',
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './maintenance.html',
  styleUrl: './maintenance.css'
})
export class Maintenance implements OnInit {

  maintenanceTasks: MaintenanceTask[] = [];

  aircraft: Aircraft[] = [];

  loading = true;

  saving = false;

  errorMessage = '';

  formErrorMessage = '';

  showAddTaskForm = false;

  editingTaskId: number | null = null;

  selectedAircraftId: number | null = null;

  newTask = {
    title: '',
    description: '',
    status: 'OPEN' as MaintenanceStatus,
    priority: 'MEDIUM' as MaintenancePriority
  };

  constructor(
    private readonly maintenanceTaskService: MaintenanceTaskService,
    private readonly aircraftService: AircraftService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadMaintenanceTasks();
    this.loadAircraft();
  }


  // =========================
  // LOAD MAINTENANCE TASKS
  // =========================

  loadMaintenanceTasks(): void {

    this.loading = true;
    this.errorMessage = '';

    this.maintenanceTaskService
      .getAllTasks()
      .subscribe({

        next: (tasks) => {

          this.maintenanceTasks = tasks;

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading maintenance tasks:',
            error
          );

          this.errorMessage =
            'Unable to load maintenance tasks. Please try again.';

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // LOAD AIRCRAFT
  // =========================

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
            'Error loading aircraft for maintenance task form:',
            error
          );

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // OPEN ADD TASK FORM
  // =========================

  openAddTaskForm(): void {

    this.resetTaskForm();

    this.showAddTaskForm = true;

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // OPEN EDIT TASK FORM
  // =========================

  openEditTaskForm(
    task: MaintenanceTask
  ): void {

    this.editingTaskId = task.id;

    this.selectedAircraftId =
      task.aircraft.id;

    this.newTask = {
      title: task.title,
      description: task.description,
      status: task.status,
      priority: task.priority
    };

    this.formErrorMessage = '';

    this.showAddTaskForm = true;

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // CLOSE TASK FORM
  // =========================

  closeAddTaskForm(): void {

    this.showAddTaskForm = false;

    this.resetTaskForm();

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // SAVE TASK
  // =========================

  saveTask(): void {

    this.formErrorMessage = '';

    const title =
      this.newTask.title.trim();

    const description =
      this.newTask.description.trim();

    if (!title) {

      this.formErrorMessage =
        'Task title is required.';

      return;
    }

    if (this.selectedAircraftId === null) {

      this.formErrorMessage =
        'Please select an aircraft.';

      return;
    }

    const selectedAircraft =
      this.aircraft.find(
        aircraft =>
          aircraft.id === this.selectedAircraftId
      );

    if (!selectedAircraft) {

      this.formErrorMessage =
        'The selected aircraft could not be found.';

      return;
    }

    const request: MaintenanceTaskRequest = {
      title,
      description,
      status: this.newTask.status,
      priority: this.newTask.priority,
      aircraft: selectedAircraft
    };

    this.saving = true;

    if (this.editingTaskId !== null) {

      this.updateTask(
        this.editingTaskId,
        request
      );

      return;
    }

    this.createTask(request);
  }


  // =========================
  // CREATE TASK
  // =========================

  createTask(
    request: MaintenanceTaskRequest
  ): void {

    this.maintenanceTaskService
      .createTask(request)
      .subscribe({

        next: (createdTask) => {

          this.maintenanceTasks = [
            ...this.maintenanceTasks,
            createdTask
          ];

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error creating maintenance task:',
            error
          );

          this.formErrorMessage =
            'Unable to create maintenance task. Please try again.';

          this.saving = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // UPDATE TASK
  // =========================

  updateTask(
    taskId: number,
    request: MaintenanceTaskRequest
  ): void {

    this.maintenanceTaskService
      .updateTask(
        taskId,
        request
      )
      .subscribe({

        next: (updatedTask) => {

          this.maintenanceTasks =
            this.maintenanceTasks.map(
              task =>
                task.id === updatedTask.id
                  ? updatedTask
                  : task
            );

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error updating maintenance task:',
            error
          );

          this.formErrorMessage =
            'Unable to update maintenance task. Please try again.';

          this.saving = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // FINISH SAVE
  // =========================

  finishSave(): void {

    this.saving = false;

    this.showAddTaskForm = false;

    this.resetTaskForm();

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // RESET FORM
  // =========================

  resetTaskForm(): void {

    this.editingTaskId = null;

    this.selectedAircraftId = null;

    this.newTask = {
      title: '',
      description: '',
      status: 'OPEN',
      priority: 'MEDIUM'
    };

    this.formErrorMessage = '';
  }


  // =========================
  // SUMMARY COUNTS
  // =========================

  get totalTasks(): number {
    return this.maintenanceTasks.length;
  }

  get openTasks(): number {

    return this.maintenanceTasks
      .filter(task =>
        task.status === 'OPEN'
      )
      .length;
  }

  get inProgressTasks(): number {

    return this.maintenanceTasks
      .filter(task =>
        task.status === 'IN_PROGRESS'
      )
      .length;
  }

  get completedTasks(): number {

    return this.maintenanceTasks
      .filter(task =>
        task.status === 'COMPLETED'
      )
      .length;
  }
}
