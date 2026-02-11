import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  role = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService) {}

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
        username: this.username.trim(),
        email: this.email.trim(),
        password: this.password,
        role: this.role
      })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => {
          this.successMessage = 'Account created. Please sign in.';
        },
        error: (err) => {
          const msg =
            err?.error?.message ||
            err?.error?.error ||
            'Registration failed. Please try again.';
          this.errorMessage = msg;
        }
      });
  }
}
