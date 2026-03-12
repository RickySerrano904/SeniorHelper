import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  firstName = '';
  lastName = '';
  username = '';
  email = '';
  password = '';
  role = '';
  loading = false;
  errorMessage = '';
  successMessage = '';
  private registrationCompleted = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || !this.role) {
      this.errorMessage = 'Please complete all fields to register.';
      return;
    }

    this.loading = true;

    this.authService
      .register({
        firstName: this.firstName.trim(),
        lastName: this.lastName.trim(),
        username: this.username.trim(),
        email: this.email.trim(),
        password: this.password,
        role: this.role
      })
      .subscribe({
        next: () => {
          this.authService
            .login({ username: this.username.trim(), password: this.password })
            .pipe(finalize(() => (this.loading = false)))
            .subscribe({
              next: (resp) => {
                // Keep auth persistence consistent with login flow.
                this.authService.persistSession(resp.token, this.username.trim(), true);
                this.registrationCompleted = true;
                this.successMessage = resp.message || 'Signed in successfully.';
                this.router.navigate(['/home']);
                this.cdr.detectChanges();
              },
              error: () => {
                this.registrationCompleted = true;
                this.successMessage = 'Account created. Please sign in.';
                this.cdr.detectChanges();
              }
            });
        },
        error: (err) => {
          this.loading = false;
          const msg =
            err?.error?.message ||
            err?.error?.error ||
            'Registration failed. Please try again.';
          this.errorMessage = msg;
          this.cdr.detectChanges();
        }
      });
  }

  canDeactivate(): boolean {
    if (this.loading) {
      return false;
    }

    if (this.registrationCompleted) {
      return true;
    }

    const hasDraft =
      this.firstName.trim().length > 0 ||
      this.lastName.trim().length > 0 ||
      this.username.trim().length > 0 ||
      this.email.trim().length > 0 ||
      this.password.length > 0 ||
      this.role.length > 0;

    if (!hasDraft) {
      return true;
    }

    return window.confirm('Discard your registration changes?');
  }
}
