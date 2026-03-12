import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  remember = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid) {
      this.errorMessage = 'Please enter your username and password.';
      return;
    }

    this.loading = true;

    this.authService
      .login({ username: this.username.trim(), password: this.password })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (resp) => {
          // Persist token + username in one place so route guards can read auth state.
          this.authService.persistSession(resp.token, this.username.trim(), this.remember);
          this.successMessage = resp.message || 'Signed in successfully.';
          this.router.navigate(['/home']);
          this.cdr.detectChanges();
        },
        error: (err) => {
          const msg =
            err?.error?.message ||
            err?.error?.error ||
            'Login failed. Please check your credentials.';
          this.errorMessage = msg;
          this.cdr.detectChanges();
        }
      });
  }
}
