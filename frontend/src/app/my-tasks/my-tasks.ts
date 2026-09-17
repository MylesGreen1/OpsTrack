import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { RouterLink } from '@angular/router';

import {
  MaintenanceTask,
  MaintenanceTaskService
} from '../maintenance/maintenance-task.service';

@Component({
  selector: 'app-my-tasks',
  imports: [
    RouterLink
  ],
  templateUrl: './my-tasks.html',
  styleUrl: './my-tasks.css'
})
export class MyTasks implements OnInit {

  maintenanceTasks: MaintenanceTask[] = [];

  loading = true;

  errorMessage = '';

  constructor(
    private readonly maintenanceTaskService:
    MaintenanceTaskService,
    private readonly changeDetectorRef:
    ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadMyTasks();
  }


  // =========================
  // LOAD MY ASSIGNED TASKS
  // =========================

  loadMyTasks(): void {

    this.loading = true;
    this.errorMessage = '';

    this.maintenanceTaskService
      .getMyTasks()
      .subscribe({

        next: (tasks) => {

          this.maintenanceTasks = tasks;

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading assigned maintenance tasks:',
            error
          );

          this.errorMessage =
            'Unable to load your assigned tasks. Please try again.';

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
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
