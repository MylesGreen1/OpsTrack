import {
  HttpClient,
  HttpHeaders
} from '@angular/common/http';

import { Injectable } from '@angular/core';

import {
  Observable,
  tap
} from 'rxjs';

export type UserRole =
  | 'TECHNICIAN'
  | 'SUPERVISOR'
  | 'QA_INSPECTOR'
  | 'ADMIN';

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

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly backendUrl =
    'http://localhost:8081';

  private readonly usernameKey =
    'opstrack_username';

  private readonly passwordKey =
    'opstrack_password';

  private readonly roleKey =
    'opstrack_role';

  private readonly technicianIdKey =
    'opstrack_technician_id';

  constructor(
    private readonly http: HttpClient
  ) {
  }

  register(
    username: string,
    password: string
  ): Observable<RegisterResponse> {

    return this.http.post<RegisterResponse>(
      `${this.backendUrl}/api/auth/register`,
      null,
      {
        params: {
          username,
          password
        }
      }
    );
  }

  login(
    username: string,
    password: string
  ): Observable<CurrentUser> {

    const credentials =
      `${username}:${password}`;

    const encodedCredentials =
      btoa(credentials);

    const headers =
      new HttpHeaders({
        Authorization:
          `Basic ${encodedCredentials}`
      });

    return this.http
      .get<CurrentUser>(
        `${this.backendUrl}/api/auth/me`,
        { headers }
      )
      .pipe(
        tap(currentUser => {

          this.setCredentials(
            username,
            password,
            currentUser.role,
            currentUser.technicianId
          );

        })
      );
  }

  getCurrentUser(): Observable<CurrentUser> {

    return this.http.get<CurrentUser>(
      `${this.backendUrl}/api/auth/me`
    );
  }

  setCredentials(
    username: string,
    password: string,
    role: UserRole,
    technicianId: number | null
  ): void {

    sessionStorage.setItem(
      this.usernameKey,
      username
    );

    sessionStorage.setItem(
      this.passwordKey,
      password
    );

    sessionStorage.setItem(
      this.roleKey,
      role
    );

    if (technicianId !== null) {

      sessionStorage.setItem(
        this.technicianIdKey,
        technicianId.toString()
      );

    } else {

      sessionStorage.removeItem(
        this.technicianIdKey
      );
    }
  }

  clearCredentials(): void {

    sessionStorage.removeItem(
      this.usernameKey
    );

    sessionStorage.removeItem(
      this.passwordKey
    );

    sessionStorage.removeItem(
      this.roleKey
    );

    sessionStorage.removeItem(
      this.technicianIdKey
    );
  }

  hasCredentials(): boolean {

    const username =
      sessionStorage.getItem(
        this.usernameKey
      );

    const password =
      sessionStorage.getItem(
        this.passwordKey
      );

    const role =
      sessionStorage.getItem(
        this.roleKey
      );

    return (
      !!username &&
      !!password &&
      !!role
    );
  }

  getUsername(): string | null {

    return sessionStorage.getItem(
      this.usernameKey
    );
  }

  getRole(): UserRole | null {

    return sessionStorage.getItem(
      this.roleKey
    ) as UserRole | null;
  }

  getTechnicianId(): number | null {

    const technicianId =
      sessionStorage.getItem(
        this.technicianIdKey
      );

    if (!technicianId) {
      return null;
    }

    return Number(technicianId);
  }

  hasRole(
    ...roles: UserRole[]
  ): boolean {

    const currentRole =
      this.getRole();

    if (!currentRole) {
      return false;
    }

    return roles.includes(
      currentRole
    );
  }

  getAuthorizationHeader(): string | null {

    const username =
      sessionStorage.getItem(
        this.usernameKey
      );

    const password =
      sessionStorage.getItem(
        this.passwordKey
      );

    if (
      !username ||
      !password
    ) {
      return null;
    }

    const credentials =
      `${username}:${password}`;

    const encodedCredentials =
      btoa(credentials);

    return `Basic ${encodedCredentials}`;
  }

  logout(): void {
    this.clearCredentials();
  }
}
