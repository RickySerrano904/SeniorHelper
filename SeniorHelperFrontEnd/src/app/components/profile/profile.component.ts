import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { timeout } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  private readonly requestTimeoutMs = 10000;
  userId: number | null = null;
  username = '';
  firstName = '';
  lastName = '';
  email = '';
  loading = true;
  saving = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  onSubmit(form: NgForm): void {
    this.errorMessage = '';

    if (form.invalid || this.userId === null) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }

    this.saving = true;

    this.authService
      .updateMyProfile(this.userId, {
        username: this.username.trim(),
        firstName: this.firstName.trim(),
        lastName: this.lastName.trim(),
        email: this.email.trim()
      })
      .pipe(timeout(this.requestTimeoutMs))
      .subscribe({
        next: () => {
          this.saving = false;
          this.authService.clearSession();
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = this.getErrorMessage(err, 'Could not save your settings. Please try again.');
        }
      });
  }

  private loadProfile(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService
      .getMyProfile()
      .pipe(timeout(this.requestTimeoutMs))
      .subscribe({
        next: (profile) => {
          this.loading = false;
          this.userId = profile.id;
          this.username = profile.username;
          this.firstName = profile.firstName;
          this.lastName = profile.lastName;
          this.email = profile.email;
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = this.getErrorMessage(err, 'Could not load your settings.');

          if (err?.status === 401) {
            this.authService.clearSession();
            this.router.navigate(['/login']);
          }
        }
      });
  }

  private getErrorMessage(err: any, fallback: string): string {
    if (err?.name === 'TimeoutError') {
      return 'Request timed out. Please make sure the backend is running and try again.';
    }

    return err?.error?.message || err?.error?.error || fallback;
  }
}
