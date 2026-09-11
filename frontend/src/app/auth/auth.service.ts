import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface RegisterResponse {
  username: string;
  role: string;
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
  ): Observable<unknown> {

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
      .get(
        `${this.backendUrl}/api/aircraft`,
        { headers }
      )
      .pipe(
        tap(() => {
          this.setCredentials(
            username,
            password
          );
        })
      );
  }

  setCredentials(
    username: string,
    password: string
  ): void {

    sessionStorage.setItem(
      this.usernameKey,
      username
    );

    sessionStorage.setItem(
      this.passwordKey,
      password
    );
  }

  clearCredentials(): void {

    sessionStorage.removeItem(
      this.usernameKey
    );

    sessionStorage.removeItem(
      this.passwordKey
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

    return (
      !!username &&
      !!password
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
