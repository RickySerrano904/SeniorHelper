import { Component, signal } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NotificationReminderService } from './services/notification-reminder.service';
import { ThemeService } from './services/theme.service';
import {
  InAppNotificationService,
  InAppNotificationTone
} from './services/in-app-notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent, FooterComponent, CommonModule, FormsModule],
  templateUrl: './app.html',
  host: {
    '[class.calendar-route]': 'isCalendarPage()'
  }
})
export class App {
  protected readonly title = signal('SeniorHelperFrontEnd');

  constructor(
    private router: Router,
    private themeService: ThemeService,
    private notificationReminderService: NotificationReminderService,
    public inAppNotifications: InAppNotificationService
  ) {
    this.themeService.initTheme();
    this.notificationReminderService.init();
  }

  isNotLoginPage(): boolean {
    const url = this.router.url;
    return !url.includes('login') && !url.includes('register');
  }

  isCalendarPage(): boolean {
    const url = this.router.url;
    return url === '/calendar' || url.startsWith('/calendar?') || url.startsWith('/calendar/');
  }

  notificationIcon(tone: InAppNotificationTone): string {
    switch (tone) {
      case 'success':
        return 'check_circle';
      case 'warning':
        return 'warning';
      case 'error':
        return 'error';
      default:
        return 'notifications';
    }
  }
}
