import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  // Property to track menu visibility on small screens
  isMenuOpen: boolean = false;

  constructor(
    private authService: AuthService, 
    private router: Router
  ) {}

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

    clearToken(): void {
      console.log('Logout clicked');
      this.authService.clearToken();
      console.log('Token cleared');
      this.router.navigate(['/login']);
    }
 
}
