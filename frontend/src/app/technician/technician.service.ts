import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Technician {
  id: number;
  firstName: string;
  lastName: string;
  employeeNumber: string;
  specialty: string;
  active: boolean;
}

export interface TechnicianRequest {
  firstName: string;
  lastName: string;
  employeeNumber: string;
  specialty: string;
  active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TechnicianService {

  private readonly apiUrl =
    'http://localhost:8081/api/technicians';

  constructor(
    private readonly http: HttpClient
  ) {}

  getAllTechnicians(): Observable<Technician[]> {
    return this.http.get<Technician[]>(
      this.apiUrl
    );
  }

  getTechnicianById(
    id: number
  ): Observable<Technician> {

    return this.http.get<Technician>(
      `${this.apiUrl}/${id}`
    );
  }

  createTechnician(
    technician: TechnicianRequest
  ): Observable<Technician> {

    return this.http.post<Technician>(
      this.apiUrl,
      technician
    );
  }

  updateTechnician(
    id: number,
    technician: TechnicianRequest
  ): Observable<Technician> {

    return this.http.put<Technician>(
      `${this.apiUrl}/${id}`,
      technician
    );
  }

  updateTechnicianActiveStatus(
    id: number,
    active: boolean
  ): Observable<Technician> {

    return this.http.patch<Technician>(
      `${this.apiUrl}/${id}/active`,
      null,
      {
        params: {
          active
        }
      }
    );
  }

  deleteTechnician(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
