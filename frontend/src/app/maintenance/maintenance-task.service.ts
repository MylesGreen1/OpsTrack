import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Aircraft } from '../aircraft/aircraft.service';

export type MaintenanceStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'COMPLETED';

export type MaintenancePriority =
  | 'LOW'
  | 'MEDIUM'
  | 'HIGH';

export interface Technician {
  id: number;
  firstName: string;
  lastName: string;
  employeeNumber: string;
  specialty: string;
  active: boolean;
}

export interface MaintenanceTask {
  id: number;
  title: string;
  description: string;
  status: MaintenanceStatus;
  priority: MaintenancePriority;
  aircraft: Aircraft;
  technician: Technician | null;
}

export interface MaintenanceTaskRequest {
  title: string;
  description: string;
  status: MaintenanceStatus;
  priority: MaintenancePriority;
  aircraft: Aircraft;
}

@Injectable({
  providedIn: 'root'
})
export class MaintenanceTaskService {

  private readonly apiUrl =
    'http://localhost:8081/api/maintenance-tasks';

  constructor(
    private readonly http: HttpClient
  ) {}

  getAllTasks(): Observable<MaintenanceTask[]> {
    return this.http.get<MaintenanceTask[]>(
      this.apiUrl
    );
  }

  getTasksByAircraftId(
    aircraftId: number
  ): Observable<MaintenanceTask[]> {

    return this.http.get<MaintenanceTask[]>(
      `${this.apiUrl}/aircraft/${aircraftId}`
    );
  }

  createTask(
    task: MaintenanceTaskRequest
  ): Observable<MaintenanceTask> {

    return this.http.post<MaintenanceTask>(
      this.apiUrl,
      task
    );
  }

  updateTask(
    taskId: number,
    task: MaintenanceTaskRequest
  ): Observable<MaintenanceTask> {

    return this.http.put<MaintenanceTask>(
      `${this.apiUrl}/${taskId}`,
      task
    );
  }

  deleteTask(
    taskId: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${taskId}`
    );
  }

  updateTaskStatus(
    taskId: number,
    status: MaintenanceStatus
  ): Observable<MaintenanceTask> {

    return this.http.patch<MaintenanceTask>(
      `${this.apiUrl}/${taskId}/status`,
      null,
      {
        params: {
          status
        }
      }
    );
  }

  assignTechnician(
    taskId: number,
    technicianId: number
  ): Observable<MaintenanceTask> {

    return this.http.patch<MaintenanceTask>(
      `${this.apiUrl}/${taskId}/technician/${technicianId}`,
      null
    );
  }
}
