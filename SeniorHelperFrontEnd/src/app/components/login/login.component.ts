import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FooterComponent } from "../footer/footer.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, FooterComponent],
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
    this.cdr.detectChanges();

    this.authService
      .login({ username: this.username.trim(), password: this.password })
      .subscribe({
        next: (resp) => {
          this.authService.saveToken(resp.token, this.remember);
          this.successMessage = resp.message || 'Signed in successfully.';
          this.loading = false;
          this.cdr.detectChanges();
          this.router.navigate(['/home']);
        },
        error: (err) => {
          const status = err?.status;
          const msg =
            status === 401
              ? 'Invalid username or password.'
              : status === 0
                ? 'Unable to reach the server. Is it running?'
                : err?.error?.message ||
                  err?.error?.error ||
                  'Login failed. Please check your credentials.';
          this.errorMessage = msg;
          this.password = '';
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
  }
}
