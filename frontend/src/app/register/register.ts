import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class RegisterComponent {

  username = '';
  password = '';
  confirmPassword = '';

  errorMessage = '';
  successMessage = '';

  loading = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
  }

  register(): void {

    this.errorMessage = '';
    this.successMessage = '';

    const trimmedUsername =
      this.username.trim();

    if (trimmedUsername.length === 0) {

      this.errorMessage =
        'Enter a username.';

      return;
    }

    if (this.password.length === 0) {

      this.errorMessage =
        'Enter a password.';

      return;
    }

    if (
      this.password !==
      this.confirmPassword
    ) {

      this.errorMessage =
        'Passwords do not match.';

      return;
    }

    this.loading = true;

    this.authService
      .register(
        trimmedUsername,
        this.password
      )
      .subscribe({

        next: () => {

          this.loading = false;

          this.successMessage =
            'Account created successfully. Redirecting to sign in...';

          setTimeout(() => {

            this.router.navigate([
              '/login'
            ]);

          }, 1200);
        },

        error: (error) => {

          console.error(
            'Registration failed:',
            error
          );

          this.loading = false;

          if (
            error.status === 400 ||
            error.status === 409
          ) {

            this.errorMessage =
              'That username may already be in use.';

          } else {

            this.errorMessage =
              'Unable to create the account. Please try again.';
          }
        }
      });
  }
}
