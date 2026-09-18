import { API_BASE_URL } from '../api.config';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type AircraftStatus =
  | 'MISSION_CAPABLE'
  | 'PARTIALLY_MISSION_CAPABLE'
  | 'NON_MISSION_CAPABLE'
  | 'IN_MAINTENANCE';

export interface Aircraft {
  id: number;
  tailNumber: string;
  aircraftType: string;
  status: AircraftStatus;
  location: string;
  notes: string;
}

export interface AircraftRequest {
  tailNumber: string;
  aircraftType: string;
  status: AircraftStatus;
  location: string;
  notes: string;
}

@Injectable({
  providedIn: 'root'
})
export class AircraftService {

  private readonly apiUrl =
    `${API_BASE_URL}/api/aircraft`;

  constructor(
    private readonly http: HttpClient
  ) {
  }

  getAllAircraft(): Observable<Aircraft[]> {

    return this.http.get<Aircraft[]>(
      this.apiUrl
    );
  }

  getAircraftById(
    id: number
  ): Observable<Aircraft> {

    return this.http.get<Aircraft>(
      `${this.apiUrl}/${id}`
    );
  }

  createAircraft(
    aircraft: AircraftRequest
  ): Observable<Aircraft> {

    return this.http.post<Aircraft>(
      this.apiUrl,
      aircraft
    );
  }

  updateAircraft(
    id: number,
    aircraft: AircraftRequest
  ): Observable<Aircraft> {

    return this.http.put<Aircraft>(
      `${this.apiUrl}/${id}`,
      aircraft
    );
  }

  updateAircraftStatus(
    id: number,
    status: AircraftStatus
  ): Observable<Aircraft> {

    return this.http.patch<Aircraft>(
      `${this.apiUrl}/${id}/status`,
      null,
      {
        params: {
          status
        }
      }
    );
  }

  deleteAircraft(
    id: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
