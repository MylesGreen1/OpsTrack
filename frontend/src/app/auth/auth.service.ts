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
            currentUser.role
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
    role: UserRole
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
