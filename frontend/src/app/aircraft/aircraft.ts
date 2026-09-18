import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  Aircraft,
  AircraftRequest,
  AircraftService
} from './aircraft.service';

import {
  AuthService
} from '../auth/auth.service';

@Component({
  selector: 'app-aircraft',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './aircraft.html',
  styleUrl: './aircraft.css'
})
export class AircraftComponent implements OnInit {

  aircraft: Aircraft[] = [];

  loading = true;
  saving = false;

  errorMessage = '';
  formErrorMessage = '';

  showAddAircraftForm = false;

  // null means we are adding a new aircraft.
  // A number means we are editing an existing aircraft.
  editingAircraftId: number | null = null;

  searchTerm = '';
  statusFilter = '';

  newAircraft: AircraftRequest = {
    tailNumber: '',
    aircraftType: '',
    status: 'MISSION_CAPABLE',
    location: '',
    notes: ''
  };

  constructor(
    private readonly aircraftService: AircraftService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {
  }

  ngOnInit(): void {
    this.loadAircraft();
  }

  loadAircraft(): void {

    this.loading = true;
    this.errorMessage = '';

    this.aircraftService
      .getAllAircraft()
      .subscribe({

        next: (aircraft) => {

          this.aircraft = aircraft;

          this.loading = false;

          this.changeDetectorRef.markForCheck();
        },

        error: (error) => {

          console.error(
            'Error loading aircraft:',
            error
          );

          this.errorMessage =
            'Unable to load aircraft records.';

          this.loading = false;

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  openAddAircraftForm(): void {

    this.formErrorMessage = '';

    this.editingAircraftId = null;

    this.resetAircraftForm();

    this.showAddAircraftForm = true;
  }

  openEditAircraftForm(
    aircraft: Aircraft
  ): void {

    this.formErrorMessage = '';

    this.editingAircraftId =
      aircraft.id;

    this.newAircraft = {
      tailNumber: aircraft.tailNumber,
      aircraftType: aircraft.aircraftType,
      status: aircraft.status,
      location: aircraft.location,
      notes: aircraft.notes ?? ''
    };

    this.showAddAircraftForm = true;
  }

  closeAddAircraftForm(): void {

    if (this.saving) {
      return;
    }

    this.showAddAircraftForm = false;

    this.editingAircraftId = null;

    this.formErrorMessage = '';

    this.resetAircraftForm();
  }

  saveAircraft(): void {

    if (
      this.editingAircraftId === null
    ) {

      this.createAircraft();

    } else {

      this.updateAircraft();
    }
  }

  createAircraft(): void {

    this.formErrorMessage = '';

    const aircraftToSave =
      this.buildAircraftRequest();

    if (!aircraftToSave) {
      return;
    }

    this.saving = true;

    this.aircraftService
      .createAircraft(aircraftToSave)
      .subscribe({

        next: () => {

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error creating aircraft:',
            error
          );

          this.saving = false;

          if (error.status === 409) {

            this.formErrorMessage =
              error.error?.message ??
              'An aircraft with that tail number already exists.';

          } else {

            this.formErrorMessage =
              'Unable to create aircraft. Please try again.';
          }

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  updateAircraft(): void {

    this.formErrorMessage = '';

    if (
      this.editingAircraftId === null
    ) {
      return;
    }

    const aircraftToSave =
      this.buildAircraftRequest();

    if (!aircraftToSave) {
      return;
    }

    const aircraftId =
      this.editingAircraftId;

    this.saving = true;

    this.aircraftService
      .updateAircraft(
        aircraftId,
        aircraftToSave
      )
      .subscribe({

        next: () => {

          this.finishSave();
        },

        error: (error) => {

          console.error(
            'Error updating aircraft:',
            error
          );

          this.saving = false;

          if (error.status === 409) {

            this.formErrorMessage =
              error.error?.message ??
              'An aircraft with that tail number already exists.';

          } else if (error.status === 404) {

            this.formErrorMessage =
              'The aircraft could not be found.';

          } else {

            this.formErrorMessage =
              'Unable to update aircraft. Please try again.';
          }

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  buildAircraftRequest():
    AircraftRequest | null {

    const tailNumber =
      this.newAircraft.tailNumber.trim();

    const aircraftType =
      this.newAircraft.aircraftType.trim();

    const location =
      this.newAircraft.location.trim();

    if (
      tailNumber.length === 0 ||
      aircraftType.length === 0 ||
      location.length === 0
    ) {

      this.formErrorMessage =
        'Tail number, aircraft type, and location are required.';

      return null;
    }

    return {
      tailNumber,
      aircraftType,
      status: this.newAircraft.status,
      location,
      notes: this.newAircraft.notes.trim()
    };
  }

  finishSave(): void {

    this.saving = false;

    this.showAddAircraftForm = false;

    this.editingAircraftId = null;

    this.formErrorMessage = '';

    this.resetAircraftForm();

    this.changeDetectorRef.markForCheck();

    this.loadAircraft();
  }

  resetAircraftForm(): void {

    this.newAircraft = {
      tailNumber: '',
      aircraftType: '',
      status: 'MISSION_CAPABLE',
      location: '',
      notes: ''
    };
  }

  deleteAircraft(
    aircraft: Aircraft
  ): void {

    const confirmed =
      window.confirm(
        `Delete aircraft ${aircraft.tailNumber}? This action cannot be undone.`
      );

    if (!confirmed) {
      return;
    }

    this.aircraftService
      .deleteAircraft(aircraft.id)
      .subscribe({

        next: () => {

          this.aircraft =
            this.aircraft.filter(
              record =>
                record.id !== aircraft.id
            );

          this.changeDetectorRef
            .markForCheck();
        },

        error: (error) => {

          console.error(
            'Error deleting aircraft:',
            error
          );

          this.errorMessage =
            'Unable to delete aircraft. Please try again.';

          this.changeDetectorRef
            .markForCheck();
        }
      });
  }

  logout(): void {

    this.authService.logout();

    this.router.navigate([
      '/login'
    ]);
  }

  get filteredAircraft(): Aircraft[] {

    const search =
      this.searchTerm
        .trim()
        .toLowerCase();

    return this.aircraft.filter(
      record => {

        const matchesSearch =
          search.length === 0 ||
          record.tailNumber
            .toLowerCase()
            .includes(search) ||
          record.aircraftType
            .toLowerCase()
            .includes(search) ||
          record.location
            .toLowerCase()
            .includes(search);

        const matchesStatus =
          this.statusFilter.length === 0 ||
          record.status ===
          this.statusFilter;

        return (
          matchesSearch &&
          matchesStatus
        );
      }
    );
  }

  get totalAircraft(): number {

    return this.aircraft.length;
  }

  get missionCapableCount(): number {

    return this.aircraft.filter(
      aircraft =>
        aircraft.status ===
        'MISSION_CAPABLE'
    ).length;
  }

  get inMaintenanceCount(): number {

    return this.aircraft.filter(
      aircraft =>
        aircraft.status ===
        'IN_MAINTENANCE'
    ).length;
  }

  get nonMissionCapableCount(): number {

    return this.aircraft.filter(
      aircraft =>
        aircraft.status ===
        'NON_MISSION_CAPABLE'
    ).length;
  }
}
