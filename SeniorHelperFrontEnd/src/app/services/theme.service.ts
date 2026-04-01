import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly storageKey = 'theme_preference';
  private theme: Theme = 'light';

  constructor(@Inject(DOCUMENT) private document: Document) {}

  initTheme(): void {
    const savedTheme = this.readSavedTheme();

    if (savedTheme === 'dark' || savedTheme === 'light') {
      this.theme = savedTheme;
    } else {
      this.theme = 'light';
    }

    this.applyTheme();
  }

  isDarkMode(): boolean {
    return this.theme === 'dark';
  }

  toggleTheme(): void {
    if (this.theme === 'dark') {
      this.theme = 'light';
    } else {
      this.theme = 'dark';
    }

    this.applyTheme();
  }

  private applyTheme(): void {
    this.document.body.classList.remove('theme-light', 'theme-dark');

    if (this.theme === 'dark') {
      this.document.body.classList.add('theme-dark');
    } else {
      this.document.body.classList.add('theme-light');
    }

    localStorage.setItem(this.storageKey, this.theme);
  }

  private readSavedTheme(): Theme | null {
    const value = localStorage.getItem(this.storageKey);

    if (value === 'dark') {
      return 'dark';
    }

    if (value === 'light') {
      return 'light';
    }

    return null;
  }
}
