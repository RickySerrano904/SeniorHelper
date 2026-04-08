import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { timeout } from 'rxjs/operators';
import { AuthService, UpdateProfileRequest } from '../../services/auth.service';

type SettingsSection = 'account' | 'security';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  private readonly requestTimeoutMs = 10000;
  userId: number | null = null;
  username = '';
  firstName = '';
  lastName = '';
  email = '';
  role = '';
  newPassword = '';
  confirmPassword = '';
  activeSection: SettingsSection = 'account';
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSettings();
  }

  selectSection(section: SettingsSection): void {
    this.activeSection = section;
    this.errorMessage = '';
    this.successMessage = '';
  }

  saveAccount(form: NgForm): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || this.userId === null) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }

    const request: UpdateProfileRequest = this.buildSettingsRequest();
    this.submitProfileUpdate(request, false);
  }

  saveSecurity(form: NgForm): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || this.userId === null) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }

    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Please enter and confirm your new password.';
      return;
    }

    if (this.newPassword.length < 8) {
      this.errorMessage = 'New password must be at least 8 characters.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New password and confirmation do not match.';
      return;
    }

    const request: UpdateProfileRequest = this.buildSettingsRequest();
    request.password = this.newPassword;
    this.submitProfileUpdate(request, true);
  }

  private submitProfileUpdate(request: UpdateProfileRequest, forceRelogin: boolean): void {
    if (this.userId === null) {
      this.errorMessage = 'Could not save your settings. Missing user context.';
      return;
    }

    this.saving = true;
    const userId = this.userId;

    this.authService
      .updateMyProfile(userId, request)
      .pipe(timeout(this.requestTimeoutMs))
      .subscribe({
        next: () => {
          this.saving = false;
          this.newPassword = '';
          this.confirmPassword = '';

          if (forceRelogin) {
            this.authService.clearSession();
            this.router.navigate(['/login']);
            return;
          }

          this.successMessage = 'Account settings updated successfully.';
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = this.getErrorMessage(err, 'Could not save your settings. Please try again.');
        }
      });
  }

  private buildSettingsRequest(): UpdateProfileRequest {
    return {
      username: this.username.trim(),
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim()
    };
  }

  private loadSettings(): void {
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
          this.role = profile.role;
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
