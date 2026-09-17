import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WorkNote {
  id: number;
  note: string;
  createdAt: string;

  maintenanceTask: {
    id: number;
    title: string;
    description: string;
    status: string;
    priority: string;

    aircraft: {
      id: number;
      tailNumber: string;
      aircraftType: string;
    } | null;
  };

  technician: {
    id: number;
    firstName: string;
    lastName: string;
    employeeNumber: string;
    specialty: string;
    active: boolean;
  };
}

@Injectable({
  providedIn: 'root'
})
export class WorkNoteService {

  private readonly apiUrl =
    'http://localhost:8081/api/work-notes';

  constructor(private http: HttpClient) {}

  getAllWorkNotes(): Observable<WorkNote[]> {
    return this.http.get<WorkNote[]>(
      this.apiUrl
    );
  }

  getWorkNotesByMaintenanceTaskId(
    maintenanceTaskId: number
  ): Observable<WorkNote[]> {

    return this.http.get<WorkNote[]>(
      `${this.apiUrl}/maintenance-task/${maintenanceTaskId}`
    );
  }

  createWorkNote(
    maintenanceTaskId: number,
    technicianId: number,
    note: string
  ): Observable<WorkNote> {

    const params = new HttpParams()
      .set(
        'maintenanceTaskId',
        maintenanceTaskId.toString()
      )
      .set(
        'technicianId',
        technicianId.toString()
      )
      .set(
        'note',
        note
      );

    return this.http.post<WorkNote>(
      this.apiUrl,
      null,
      { params }
    );
  }

  deleteWorkNote(
    id: number
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

}
