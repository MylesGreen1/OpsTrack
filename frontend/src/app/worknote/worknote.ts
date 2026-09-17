import { CommonModule } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  WorkNote,
  WorkNoteService
} from './worknote.service';

import {
  MaintenanceTask,
  MaintenanceTaskService
} from '../maintenance/maintenance-task.service';

import {
  Technician,
  TechnicianService
} from '../technician/technician.service';

@Component({
  selector: 'app-worknote',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './worknote.html',
  styleUrl: './worknote.css'
})
export class WorkNoteComponent implements OnInit {

  workNotes: WorkNote[] = [];
  maintenanceTasks: MaintenanceTask[] = [];
  technicians: Technician[] = [];

  selectedMaintenanceTaskId: number | null = null;
  selectedTechnicianId: number | null = null;
  noteText = '';

  showCreateModal = false;
  isSubmitting = false;

  errorMessage = '';

  constructor(
    private readonly workNoteService: WorkNoteService,
    private readonly maintenanceTaskService: MaintenanceTaskService,
    private readonly technicianService: TechnicianService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}
  ngOnInit(): void {
    this.loadWorkNotes();
    this.loadMaintenanceTasks();
    this.loadTechnicians();
  }

  loadWorkNotes(): void {
    this.workNoteService
      .getAllWorkNotes()
      .subscribe({
        next: (workNotes) => {
          this.workNotes = workNotes;
          this.changeDetectorRef.markForCheck();
        },
        error: (error) => {
          console.error(
            'Failed to load work notes:',
            error
          );

          this.errorMessage =
            'Unable to load work notes.';
        }
      });
  }

  loadMaintenanceTasks(): void {
    this.maintenanceTaskService
      .getAllTasks()
      .subscribe({
        next: (tasks) => {
          this.maintenanceTasks = tasks;
        },
        error: (error) => {
          console.error(
            'Failed to load maintenance tasks:',
            error
          );
        }
      });
  }

  loadTechnicians(): void {
    this.technicianService
      .getAllTechnicians()
      .subscribe({
        next: (technicians) => {
          this.technicians =
            technicians.filter(
              technician => technician.active
            );
        },
        error: (error) => {
          console.error(
            'Failed to load technicians:',
            error
          );
        }
      });
  }

  openCreateModal(): void {
    this.resetForm();
    this.errorMessage = '';
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    if (this.isSubmitting) {
      return;
    }

    this.showCreateModal = false;
    this.resetForm();
  }

  createWorkNote(): void {

    this.errorMessage = '';

    if (this.selectedMaintenanceTaskId === null) {
      this.errorMessage =
        'Please select a maintenance task.';
      return;
    }

    if (this.selectedTechnicianId === null) {
      this.errorMessage =
        'Please select a technician.';
      return;
    }

    const trimmedNote = this.noteText.trim();

    if (!trimmedNote) {
      this.errorMessage =
        'Please enter a work note.';
      return;
    }

    this.isSubmitting = true;

    this.workNoteService
      .createWorkNote(
        this.selectedMaintenanceTaskId,
        this.selectedTechnicianId,
        trimmedNote
      )
      .subscribe({
        next: (createdWorkNote) => {

          this.workNotes = [
            createdWorkNote,
            ...this.workNotes
          ];

          this.isSubmitting = false;
          this.showCreateModal = false;

          this.resetForm();

          this.changeDetectorRef.markForCheck();
        },
        error: (error) => {

          console.error(
            'Failed to create work note:',
            error
          );

          this.errorMessage =
            'Unable to create the work note.';

          this.isSubmitting = false;
        }
      });
  }

  deleteWorkNote(workNote: WorkNote): void {

    const confirmed = window.confirm(
      'Are you sure you want to delete this work note?'
    );

    if (!confirmed) {
      return;
    }

    this.workNoteService
      .deleteWorkNote(workNote.id)
      .subscribe({
        next: () => {

          this.workNotes =
            this.workNotes.filter(
              note => note.id !== workNote.id
            );

          this.changeDetectorRef.markForCheck();
        },
        error: (error) => {

          console.error(
            'Failed to delete work note:',
            error
          );

          this.errorMessage =
            'Unable to delete the work note.';

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  resetForm(): void {
    this.selectedMaintenanceTaskId = null;
    this.selectedTechnicianId = null;
    this.noteText = '';
  }

  get totalWorkNotes(): number {
    return this.workNotes.length;
  }

  get uniqueTasks(): number {
    return new Set(
      this.workNotes
        .map(workNote =>
          workNote.maintenanceTask?.id
        )
        .filter(id => id !== undefined)
    ).size;
  }

  get uniqueTechnicians(): number {
    return new Set(
      this.workNotes
        .map(workNote =>
          workNote.technician?.id
        )
        .filter(id => id !== undefined)
    ).size;
  }

  get latestWorkNote(): WorkNote | null {

    if (this.workNotes.length === 0) {
      return null;
    }

    return [...this.workNotes]
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
      )[0];
  }
}
