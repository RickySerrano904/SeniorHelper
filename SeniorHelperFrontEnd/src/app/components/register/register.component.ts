import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

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

  onSubmit(form: NgForm) {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || !this.role) {
      this.errorMessage = 'Please complete all fields to register.';
      return;
    }

    this.loading = true;
    this.loading = false;
    this.successMessage = 'Registration details captured (= Connect this to the API next.';
  }
}
