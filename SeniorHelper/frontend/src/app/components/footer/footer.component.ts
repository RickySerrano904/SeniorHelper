import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [NgIf, RouterLink, RouterLinkActive],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  showFaq = true;

  constructor(private router: Router) {
    this.updateShowFaq(this.router.url);

    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe(event => this.updateShowFaq(event.urlAfterRedirects));
  }

  private updateShowFaq(url: string): void {
    const path = url.split('?')[0].split('#')[0];
    this.showFaq = !(path === '/' || path === '/login' || path === '/register');
  }
}
