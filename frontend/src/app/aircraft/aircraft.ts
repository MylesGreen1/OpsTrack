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
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './aircraft.html',
  styleUrl: './aircraft.css',
})
export class AircraftComponent implements OnInit {

  aircraft: Aircraft[] = [];

  loading = true;
  saving = false;

  errorMessage = '';
  formErrorMessage = '';

  showAddAircraftForm = false;

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

    this.resetAircraftForm();

    this.showAddAircraftForm = true;
  }

  closeAddAircraftForm(): void {

    if (this.saving) {
      return;
    }

    this.showAddAircraftForm = false;

    this.formErrorMessage = '';
  }

  createAircraft(): void {

    this.formErrorMessage = '';

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

      return;
    }

    const aircraftToCreate: AircraftRequest = {
      tailNumber,
      aircraftType,
      status: this.newAircraft.status,
      location,
      notes: this.newAircraft.notes.trim()
    };

    this.saving = true;

    this.aircraftService
      .createAircraft(aircraftToCreate)
      .subscribe({

        next: () => {

          this.saving = false;

          this.showAddAircraftForm = false;

          this.resetAircraftForm();

          this.changeDetectorRef.markForCheck();

          this.loadAircraft();
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

  resetAircraftForm(): void {

    this.newAircraft = {
      tailNumber: '',
      aircraftType: '',
      status: 'MISSION_CAPABLE',
      location: '',
      notes: ''
    };
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
