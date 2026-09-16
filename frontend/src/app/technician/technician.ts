import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import {
  Technician,
  TechnicianRequest,
  TechnicianService
} from './technician.service';

@Component({
  selector: 'app-technician',
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './technician.html',
  styleUrl: './technician.css'
})
export class TechnicianComponent implements OnInit {

  technicians: Technician[] = [];

  loading = true;

  saving = false;

  deletingTechnicianId: number | null = null;

  errorMessage = '';

  formErrorMessage = '';

  showTechnicianForm = false;

  editingTechnicianId: number | null = null;

  technicianForm = {
    firstName: '',
    lastName: '',
    employeeNumber: '',
    specialty: '',
    active: true
  };

  constructor(
    private readonly technicianService: TechnicianService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadTechnicians();
  }


  // =========================
  // LOAD TECHNICIANS
  // =========================

  loadTechnicians(): void {

    this.loading = true;
    this.errorMessage = '';

    this.technicianService
      .getAllTechnicians()
      .subscribe({

        next: (technicians) => {

          this.technicians = technicians;

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading technicians:',
            error
          );

          this.errorMessage =
            'Unable to load technicians. Please try again.';

          this.loading = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // OPEN ADD FORM
  // =========================

  openAddTechnicianForm(): void {

    this.resetTechnicianForm();

    this.showTechnicianForm = true;

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // OPEN EDIT FORM
  // =========================

  openEditTechnicianForm(
    technician: Technician
  ): void {

    this.editingTechnicianId =
      technician.id;

    this.technicianForm = {
      firstName: technician.firstName,
      lastName: technician.lastName,
      employeeNumber: technician.employeeNumber,
      specialty: technician.specialty,
      active: technician.active
    };

    this.formErrorMessage = '';

    this.showTechnicianForm = true;

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // CLOSE FORM
  // =========================

  closeTechnicianForm(): void {

    this.showTechnicianForm = false;

    this.resetTechnicianForm();

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // SAVE TECHNICIAN
  // =========================

  saveTechnician(): void {

    this.formErrorMessage = '';

    const firstName =
      this.technicianForm.firstName.trim();

    const lastName =
      this.technicianForm.lastName.trim();

    const employeeNumber =
      this.technicianForm.employeeNumber.trim();

    const specialty =
      this.technicianForm.specialty.trim();

    if (!firstName) {
      this.formErrorMessage =
        'First name is required.';
      return;
    }

    if (!lastName) {
      this.formErrorMessage =
        'Last name is required.';
      return;
    }

    if (!employeeNumber) {
      this.formErrorMessage =
        'Employee number is required.';
      return;
    }

    if (!specialty) {
      this.formErrorMessage =
        'Specialty is required.';
      return;
    }

    const request: TechnicianRequest = {
      firstName,
      lastName,
      employeeNumber,
      specialty,
      active: this.technicianForm.active
    };

    this.saving = true;

    if (this.editingTechnicianId !== null) {

      this.updateTechnician(
        this.editingTechnicianId,
        request
      );

      return;
    }

    this.createTechnician(request);
  }


  // =========================
  // CREATE TECHNICIAN
  // =========================

  createTechnician(
    request: TechnicianRequest
  ): void {

    this.technicianService
      .createTechnician(request)
      .subscribe({

        next: (createdTechnician) => {

          this.technicians = [
            ...this.technicians,
            createdTechnician
          ];

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error creating technician:',
            error
          );

          this.formErrorMessage =
            'Unable to create technician. Please try again.';

          this.saving = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // UPDATE TECHNICIAN
  // =========================

  updateTechnician(
    id: number,
    request: TechnicianRequest
  ): void {

    this.technicianService
      .updateTechnician(
        id,
        request
      )
      .subscribe({

        next: (updatedTechnician) => {

          this.technicians =
            this.technicians.map(
              technician =>
                technician.id === updatedTechnician.id
                  ? updatedTechnician
                  : technician
            );

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error updating technician:',
            error
          );

          this.formErrorMessage =
            'Unable to update technician. Please try again.';

          this.saving = false;

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // CHANGE ACTIVE STATUS
  // =========================

  updateActiveStatus(
    technician: Technician
  ): void {

    const newStatus =
      !technician.active;

    this.technicianService
      .updateTechnicianActiveStatus(
        technician.id,
        newStatus
      )
      .subscribe({

        next: (updatedTechnician) => {

          this.technicians =
            this.technicians.map(
              currentTechnician =>
                currentTechnician.id === updatedTechnician.id
                  ? updatedTechnician
                  : currentTechnician
            );

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error changing technician status:',
            error
          );

          this.errorMessage =
            'Unable to change technician status. Please try again.';

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }


  // =========================
  // DELETE TECHNICIAN
  // =========================

  deleteTechnician(
    technician: Technician
  ): void {

    const confirmed = window.confirm(
      `Delete technician "${technician.firstName} ${technician.lastName}"?`
    );

    if (!confirmed) {
      return;
    }

    this.deletingTechnicianId =
      technician.id;

    this.errorMessage = '';

    this.technicianService
      .deleteTechnician(technician.id)
      .subscribe({

        next: () => {

          this.technicians =
            this.technicians.filter(
              currentTechnician =>
                currentTechnician.id !== technician.id
            );

          this.deletingTechnicianId = null;

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error deleting technician:',
            error
          );

          this.errorMessage =
            'Unable to delete technician. Please try again.';

          this.deletingTechnicianId = null;

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

    this.showTechnicianForm = false;

    this.resetTechnicianForm();

    this.changeDetectorRef
      .markForCheck();
  }


  // =========================
  // RESET FORM
  // =========================

  resetTechnicianForm(): void {

    this.editingTechnicianId = null;

    this.technicianForm = {
      firstName: '',
      lastName: '',
      employeeNumber: '',
      specialty: '',
      active: true
    };

    this.formErrorMessage = '';
  }


  // =========================
  // SUMMARY COUNTS
  // =========================

  get totalTechnicians(): number {
    return this.technicians.length;
  }

  get activeTechnicians(): number {

    return this.technicians
      .filter(technician =>
        technician.active
      )
      .length;
  }

  get inactiveTechnicians(): number {

    return this.technicians
      .filter(technician =>
        !technician.active
      )
      .length;
  }
}
