import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { Appointment } from '../../models/appointment.model';
import { AppointmentService } from '../../services/appointment.service';
import { AuthService, UpdateProfileRequest } from '../../services/auth.service';
import { NotificationReminderService } from '../../services/notification-reminder.service';
import { InAppNotificationService } from '../../services/in-app-notification.service';

type SettingsSection = 'account' | 'security' | 'notifications';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  private readonly requestTimeoutMs = 10000;
  private readonly notificationsWindowMs = 7 * 24 * 60 * 60 * 1000;
  private readonly defaultReminderLeadMinutes = 30;
  private readonly maxReminderPartValue = 999;
  userId: number | null = null;
  username = '';
  firstName = '';
  lastName = '';
  email = '';
  role = '';
  newPassword = '';
  confirmPassword = '';
  activeSection: SettingsSection = 'account';
  loading = true;
  saving = false;
  notifying = false;
  errorMessage = '';
  successMessage = '';
  notificationStatus = '';
  notificationsEnabled = false;
  notificationLeadDays = 0;
  notificationLeadHours = 0;
  notificationLeadMinutes = this.defaultReminderLeadMinutes;

  constructor(
    private authService: AuthService,
    private appointmentService: AppointmentService,
    private notificationReminderService: NotificationReminderService,
    private inAppNotifications: InAppNotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSettings();
  }

  selectSection(section: SettingsSection): void {
    this.activeSection = section;
    this.errorMessage = '';
    this.successMessage = '';
    this.notificationStatus = '';
  }

  saveReminderPreferences(): void {
    this.notificationStatus = '';

    if (!this.isValidReminderPart(this.notificationLeadDays) || !this.isValidReminderPart(this.notificationLeadHours) || !this.isValidReminderPart(this.notificationLeadMinutes)) {
      this.notificationStatus = 'Please choose reminder values between 0 and 999.';
      return;
    }

    const totalLeadMinutes = this.toTotalLeadMinutes();

    this.notificationReminderService.savePreferences(this.userId, {
      enabled: this.notificationsEnabled,
      leadMinutes: totalLeadMinutes
    });
    this.notificationReminderService.refreshForUser(this.userId);

    this.notificationStatus = this.notificationsEnabled
      ? `You will be notified ${this.formatReminderLead()} before each appointment.`
      : 'Appointment reminders are turned off.';
  }

  toggleNotificationsEnabled(enabled: boolean): void {
    this.notificationsEnabled = enabled;
    this.saveReminderPreferences();
  }

  async pushUpcomingAppointmentsNotification(): Promise<void> {
    await this.sendUpcomingAppointmentsNotification();
  }

  private async sendUpcomingAppointmentsNotification(): Promise<void> {
    this.notificationStatus = '';

    this.notifying = true;

    try {
      const appointments = await firstValueFrom(
        this.appointmentService.getMyAppointments().pipe(timeout(this.requestTimeoutMs))
      );
      const upcoming = this.getUpcomingWeekAppointments(appointments ?? []);
      const body = this.buildNotificationBody(upcoming);

      this.inAppNotifications.show({
        title: 'Upcoming appointments this week',
        message: body,
        tone: upcoming.length === 0 ? 'info' : 'success',
        timeoutMs: 10000
      });
      this.notificationStatus =
        upcoming.length === 0
          ? 'Preview shown. No appointments are scheduled in the next 7 days.'
          : `Preview shown for ${upcoming.length} upcoming appointment${upcoming.length === 1 ? '' : 's'}.`;
    } catch (err) {
      this.notificationStatus = this.getErrorMessage(
        err,
        'Could not load appointments for notification. Please try again.'
      );
    } finally {
      this.notifying = false;
    }
  }

  saveAccount(form: NgForm): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || this.userId === null) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }

    const request: UpdateProfileRequest = this.buildSettingsRequest();
    this.submitProfileUpdate(request, false);
  }

  saveSecurity(form: NgForm): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (form.invalid || this.userId === null) {
      this.errorMessage = 'Please complete all fields.';
      return;
    }

    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Please enter and confirm your new password.';
      return;
    }

    if (this.newPassword.length < 8) {
      this.errorMessage = 'New password must be at least 8 characters.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'New password and confirmation do not match.';
      return;
    }

    const request: UpdateProfileRequest = this.buildSettingsRequest();
    request.password = this.newPassword;
    this.submitProfileUpdate(request, true);
  }

  private submitProfileUpdate(request: UpdateProfileRequest, forceRelogin: boolean): void {
    if (this.userId === null) {
      this.errorMessage = 'Could not save your settings. Missing user context.';
      return;
    }

    this.saving = true;
    const userId = this.userId;

    this.authService
      .updateMyProfile(userId, request)
      .pipe(timeout(this.requestTimeoutMs))
      .subscribe({
        next: () => {
          this.saving = false;
          this.newPassword = '';
          this.confirmPassword = '';

          if (forceRelogin) {
            this.authService.clearSession();
            this.router.navigate(['/login']);
            return;
          }

          this.successMessage = 'Account settings updated successfully.';
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = this.getErrorMessage(err, 'Could not save your settings. Please try again.');
        }
      });
  }

  private buildSettingsRequest(): UpdateProfileRequest {
    return {
      username: this.username.trim(),
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim()
    };
  }

  private loadSettings(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService
      .getMyProfile()
      .pipe(timeout(this.requestTimeoutMs))
      .subscribe({
        next: (profile) => {
          this.loading = false;
          this.userId = profile.id;
          this.username = profile.username;
          this.firstName = profile.firstName;
          this.lastName = profile.lastName;
          this.email = profile.email;
          this.role = profile.role;
          this.loadNotificationPreferences();
          this.notificationReminderService.refreshForUser(profile.id);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = this.getErrorMessage(err, 'Could not load your settings.');

          if (err?.status === 401) {
            this.authService.clearSession();
            this.router.navigate(['/login']);
          }
        }
      });
  }

  private getErrorMessage(err: any, fallback: string): string {
    if (err?.name === 'TimeoutError') {
      return 'Request timed out. Please make sure the backend is running and try again.';
    }

    return err?.error?.message || err?.error?.error || fallback;
  }

  private loadNotificationPreferences(): void {
    const preferences = this.notificationReminderService.loadPreferences(this.userId);
    this.notificationsEnabled = preferences.enabled;
    this.applyLeadMinutes(preferences.leadMinutes);
  }

  private isValidReminderPart(value: number): boolean {
    return Number.isFinite(value) && Number.isInteger(value) && value >= 0 && value <= this.maxReminderPartValue;
  }

  private toTotalLeadMinutes(): number {
    return (this.notificationLeadDays * 24 * 60) + (this.notificationLeadHours * 60) + this.notificationLeadMinutes;
  }

  private applyLeadMinutes(totalLeadMinutes: number): void {
    const safeMinutes = Number.isFinite(totalLeadMinutes) && totalLeadMinutes >= 0
      ? Math.floor(totalLeadMinutes)
      : this.defaultReminderLeadMinutes;

    let remainingMinutes = safeMinutes;
    this.notificationLeadDays = Math.min(this.maxReminderPartValue, Math.floor(remainingMinutes / (24 * 60)));
    remainingMinutes -= this.notificationLeadDays * 24 * 60;
    this.notificationLeadHours = Math.min(this.maxReminderPartValue, Math.floor(remainingMinutes / 60));
    remainingMinutes -= this.notificationLeadHours * 60;
    this.notificationLeadMinutes = Math.min(this.maxReminderPartValue, remainingMinutes);
  }

  private formatReminderLead(): string {
    const parts: string[] = [];

    if (this.notificationLeadDays > 0) {
      parts.push(`${this.notificationLeadDays} day${this.notificationLeadDays === 1 ? '' : 's'}`);
    }

    if (this.notificationLeadHours > 0) {
      parts.push(`${this.notificationLeadHours} hour${this.notificationLeadHours === 1 ? '' : 's'}`);
    }

    if (this.notificationLeadMinutes > 0 || parts.length === 0) {
      parts.push(`${this.notificationLeadMinutes} minute${this.notificationLeadMinutes === 1 ? '' : 's'}`);
    }

    return parts.join(', ');
  }

  private getUpcomingWeekAppointments(appointments: Appointment[]): Appointment[] {
    const now = Date.now();
    const nextWeek = now + this.notificationsWindowMs;

    return appointments
      .filter((appointment) => {
        if (!appointment.start) {
          return false;
        }

        const startMs = new Date(appointment.start).getTime();
        if (Number.isNaN(startMs)) {
          return false;
        }

        return startMs >= now && startMs <= nextWeek;
      })
      .sort((a, b) => {
        const startA = a.start ? new Date(a.start).getTime() : 0;
        const startB = b.start ? new Date(b.start).getTime() : 0;
        return startA - startB;
      });
  }

  private buildNotificationBody(appointments: Appointment[]): string {
    if (appointments.length === 0) {
      return 'No upcoming appointments in the next 7 days.';
    }

    return appointments
      .map((appointment) => {
        const title = (appointment.title || 'Appointment').trim();
        const startTime = appointment.start
          ? new Date(appointment.start).toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' })
          : 'Unknown time';
        return `${title} - ${startTime}`;
      })
      .join('\n');
  }
}
