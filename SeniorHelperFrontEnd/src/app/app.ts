import { Component, signal } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, FooterComponent, CommonModule],
  templateUrl: './app.html'
})
export class App {
  protected readonly title = signal('SeniorHelperFrontEnd');

  constructor(private router: Router) {}

  isNotLoginPage(): boolean {
    return !this.router.url.includes('login');
  }
}
