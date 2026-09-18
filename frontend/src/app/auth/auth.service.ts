import { API_BASE_URL } from '../api.config';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable, tap } from 'rxjs';

export type UserRole = 'TECHNICIAN' | 'SUPERVISOR' | 'QA_INSPECTOR' | 'ADMIN';

export interface RegisterResponse {
  username: string;
  role: UserRole;
  enabled: boolean;
}

export interface CurrentUser {
  username: string;
  role: UserRole;
  enabled: boolean;
  technicianId: number | null;
}

export interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly backendUrl = API_BASE_URL;

  private readonly usernameKey = 'opstrack_username';

  private readonly roleKey = 'opstrack_role';

  private readonly technicianIdKey = 'opstrack_technician_id';

  constructor(private readonly http: HttpClient) {}

  register(username: string, password: string): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.backendUrl}/api/auth/register`, null, {
      params: {
        username,
        password,
      },
    });
  }

  login(username: string, password: string): Observable<CurrentUser> {
    const loginRequest: LoginRequest = {
      username,
      password,
    };

    return this.http
      .post<CurrentUser>(`${this.backendUrl}/api/auth/login`, loginRequest, {
        withCredentials: true,
      })
      .pipe(
        tap((currentUser) => {
          this.setUserSession(currentUser);
        }),
      );
  }

  getCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>(`${this.backendUrl}/api/auth/me`, {
      withCredentials: true,
    });
  }

  private setUserSession(currentUser: CurrentUser): void {
    sessionStorage.setItem(this.usernameKey, currentUser.username);

    sessionStorage.setItem(this.roleKey, currentUser.role);

    if (currentUser.technicianId !== null) {
      sessionStorage.setItem(this.technicianIdKey, currentUser.technicianId.toString());
    } else {
      sessionStorage.removeItem(this.technicianIdKey);
    }
  }

  clearCredentials(): void {
    sessionStorage.removeItem(this.usernameKey);

    sessionStorage.removeItem(this.roleKey);

    sessionStorage.removeItem(this.technicianIdKey);

    // Clean up credentials left by the old
    // Basic Authentication implementation.
    sessionStorage.removeItem('opstrack_password');
  }

  hasCredentials(): boolean {
    const username = sessionStorage.getItem(this.usernameKey);

    const role = sessionStorage.getItem(this.roleKey);

    return !!username && !!role;
  }

  getUsername(): string | null {
    return sessionStorage.getItem(this.usernameKey);
  }

  getRole(): UserRole | null {
    return sessionStorage.getItem(this.roleKey) as UserRole | null;
  }

  getTechnicianId(): number | null {
    const technicianId = sessionStorage.getItem(this.technicianIdKey);

    if (!technicianId) {
      return null;
    }

    return Number(technicianId);
  }

  hasRole(...roles: UserRole[]): boolean {
    const currentRole = this.getRole();

    if (!currentRole) {
      return false;
    }

    return roles.includes(currentRole);
  }

  logout(): void {
    this.clearCredentials();
  }
}
