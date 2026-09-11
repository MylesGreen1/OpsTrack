import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {

  username = '';
  password = '';

  errorMessage = '';

  loading = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
  }

  login(): void {

    this.errorMessage = '';

    if (
      this.username.trim().length === 0 ||
      this.password.length === 0
    ) {

      this.errorMessage =
        'Enter your username and password.';

      return;
    }

    this.loading = true;

    this.authService
      .login(
        this.username.trim(),
        this.password
      )
      .subscribe({

        next: () => {

          this.loading = false;

          this.router.navigate([
            '/aircraft'
          ]);
        },

        error: (error) => {

          console.error(
            'Login failed:',
            error
          );

          this.loading = false;

          if (error.status === 401) {

            this.errorMessage =
              'Invalid username or password.';

          } else {

            this.errorMessage =
              'Unable to connect to OpsTrack. Please try again.';
          }
        }
      });
  }
}
