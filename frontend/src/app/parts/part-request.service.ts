import { API_BASE_URL } from '../api.config';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MaintenanceTask } from '../maintenance/maintenance-task.service';

export type PartRequestStatus = 'REQUESTED' | 'ORDERED' | 'RECEIVED' | 'CANCELLED';

export interface PartRequest {
  id: number;
  partNumber: string;
  partName: string;
  quantity: number;
  status: PartRequestStatus;
  maintenanceTask: MaintenanceTask;
  requestedBy: { id: number; firstName: string; lastName: string; employeeNumber: string };
  requestedAt: string;
  notes: string;
}

export interface PartRequestCreate {
  partNumber: string;
  partName: string;
  quantity: number;
  notes: string;
}

@Injectable({ providedIn: 'root' })
export class PartRequestService {
  private readonly apiUrl = `${API_BASE_URL}/api/part-requests`;
  constructor(private readonly http: HttpClient) {}
  getAll(): Observable<PartRequest[]> { return this.http.get<PartRequest[]>(this.apiUrl); }
  getMyRequests(): Observable<PartRequest[]> { return this.http.get<PartRequest[]>(`${this.apiUrl}/my-requests`); }
  create(taskId: number, request: PartRequestCreate): Observable<PartRequest> {
    return this.http.post<PartRequest>(this.apiUrl, request, { params: { maintenanceTaskId: taskId } });
  }
  updateStatus(id: number, status: PartRequestStatus): Observable<PartRequest> {
    return this.http.patch<PartRequest>(`${this.apiUrl}/${id}/status`, null, { params: { status } });
  }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
}
