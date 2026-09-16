import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
  MaintenanceTask
} from '../maintenance/maintenance-task.service';

import {
  Technician
} from '../technician/technician.service';

export type InspectionStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED';

export interface Inspection {
  id: number;
  status: InspectionStatus;
  comments: string;
  maintenanceTask: MaintenanceTask;
  inspector: Technician;
}

@Injectable({
  providedIn: 'root'
})
export class InspectionService {

  private readonly apiUrl =
    'http://localhost:8081/api/inspections';

  constructor(
    private readonly http: HttpClient
  ) {}

  getAllInspections(): Observable<Inspection[]> {

    return this.http.get<Inspection[]>(
      this.apiUrl
    );
  }

  getInspectionsByMaintenanceTaskId(
    maintenanceTaskId: number
  ): Observable<Inspection[]> {

    return this.http.get<Inspection[]>(
      `${this.apiUrl}/maintenance-task/${maintenanceTaskId}`
    );
  }

  createInspection(
    maintenanceTaskId: number,
    inspectorId: number,
    status: InspectionStatus,
    comments: string
  ): Observable<Inspection> {

    return this.http.post<Inspection>(
      this.apiUrl,
      null,
      {
        params: {
          maintenanceTaskId,
          inspectorId,
          status,
          comments
        }
      }
    );
  }
}
