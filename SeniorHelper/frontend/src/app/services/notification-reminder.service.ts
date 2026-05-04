import { Injectable } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { filter } from 'rxjs/operators';
import { timeout } from 'rxjs/operators';
import { Appointment } from '../models/appointment.model';
import { AppointmentService } from './appointment.service';
import { AuthService } from './auth.service';
import { InAppNotificationService } from './in-app-notification.service';

export interface NotificationReminderPreferences {
  enabled: boolean;
  leadMinutes: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationReminderService {
  private readonly requestTimeoutMs = 10000;
  private readonly reminderPollIntervalMs = 60 * 1000;
  private readonly defaultReminderLeadMinutes = 30;
  private readonly maxReminderLeadMinutes = (999 * 24 * 60) + (999 * 60) + 999;
  private initialized = false;
  private currentUserId: number | null = null;
  private reminderIntervalId: ReturnType<typeof setInterval> | null = null;
  private reminderCheckInProgress = false;
  private profileLookupInProgress = false;

  constructor(
    private readonly authService: AuthService,
    private readonly appointmentService: AppointmentService,
    private readonly router: Router,
    private readonly inAppNotifications: InAppNotificationService
  ) {}

  init(): void {
    if (this.initialized) {
      return;
    }

    this.initialized = true;
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => this.syncWithSession());

    this.syncWithSession();
  }

  loadPreferences(userId: number | null): NotificationReminderPreferences {
    const storedEnabled = this.readPreference(userId, 'enabled');
    const storedLeadMinutes = Number(this.readPreference(userId, 'leadMinutes'));

    return {
      enabled: storedEnabled === 'true',
      leadMinutes: this.isValidLeadMinutes(storedLeadMinutes)
        ? storedLeadMinutes
        : this.defaultReminderLeadMinutes
    };
  }

  savePreferences(userId: number | null, preferences: NotificationReminderPreferences): void {
    if (userId === null) {
      return;
    }

    this.persistPreference(userId, 'enabled', String(preferences.enabled));
    this.persistPreference(userId, 'leadMinutes', String(preferences.leadMinutes));
  }

  refreshForUser(userId: number | null): void {
    this.currentUserId = userId;

    if (userId === null || !this.authService.isAuthenticated()) {
      this.stop();
      return;
    }

    this.startReminderPolling();
  }

  private syncWithSession(): void {
    if (!this.authService.isAuthenticated()) {
      this.stop();
      return;
    }

    if (this.currentUserId !== null) {
      this.startReminderPolling();
      return;
    }

    if (this.profileLookupInProgress) {
      return;
    }

    this.profileLookupInProgress = true;

    this.authService.getMyProfile().pipe(timeout(this.requestTimeoutMs)).subscribe({
      next: (profile) => {
        this.profileLookupInProgress = false;
        this.currentUserId = profile.id;
        this.startReminderPolling();
      },
      error: () => {
        this.profileLookupInProgress = false;
      }
    });
  }

  private startReminderPolling(): void {
    this.clearReminderInterval();

    if (this.currentUserId === null) {
      return;
    }

    const preferences = this.loadPreferences(this.currentUserId);
    if (!preferences.enabled || !this.isValidLeadMinutes(preferences.leadMinutes)) {
      return;
    }

    void this.checkAppointmentReminders();
    this.reminderIntervalId = setInterval(() => {
      void this.checkAppointmentReminders();
    }, this.reminderPollIntervalMs);
  }

  private async checkAppointmentReminders(): Promise<void> {
    if (this.reminderCheckInProgress || this.currentUserId === null) {
      return;
    }

    const preferences = this.loadPreferences(this.currentUserId);
    if (!preferences.enabled || !this.isValidLeadMinutes(preferences.leadMinutes)) {
      return;
    }

    this.reminderCheckInProgress = true;

    try {
      const appointments = await firstValueFrom(
        this.appointmentService.getMyAppointments().pipe(timeout(this.requestTimeoutMs))
      );
      const nowMs = Date.now();
      const notifiedKeys = this.readNotifiedReminderKeys(this.currentUserId);
      const nextNotifiedKeys = new Set<string>();

      for (const appointment of appointments ?? []) {
        if (!appointment.start) {
          continue;
        }

        const appointmentStartMs = new Date(appointment.start).getTime();
        if (Number.isNaN(appointmentStartMs) || appointmentStartMs <= nowMs) {
          continue;
        }

        const reminderAtMs = appointmentStartMs - preferences.leadMinutes * 60 * 1000;
        const reminderKey = this.buildReminderKey(appointment, preferences.leadMinutes);

        if (reminderAtMs > appointmentStartMs) {
          continue;
        }

        if (notifiedKeys.has(reminderKey)) {
          nextNotifiedKeys.add(reminderKey);
          continue;
        }

        if (reminderAtMs <= nowMs) {
          const title = (appointment.title || 'Appointment').trim();
          const startTime = new Date(appointmentStartMs).toLocaleTimeString([], {
            hour: 'numeric',
            minute: '2-digit'
          });

          this.inAppNotifications.show({
            title: 'Upcoming appointment',
            message: `${title} starts at ${startTime}`,
            tone: 'info',
            timeoutMs: 10000
          });
          nextNotifiedKeys.add(reminderKey);
        }
      }

      this.persistNotifiedReminderKeys(this.currentUserId, nextNotifiedKeys);
    } catch {
      // Ignore background reminder fetch errors.
    } finally {
      this.reminderCheckInProgress = false;
    }
  }

  private stop(): void {
    this.currentUserId = null;
    this.clearReminderInterval();
  }

  private clearReminderInterval(): void {
    if (this.reminderIntervalId !== null) {
      clearInterval(this.reminderIntervalId);
      this.reminderIntervalId = null;
    }
  }

  private isValidLeadMinutes(value: number): boolean {
    return Number.isFinite(value) && Number.isInteger(value) && value >= 0 && value <= this.maxReminderLeadMinutes;
  }

  private notificationPrefKey(userId: number | null, suffix: string): string {
    const userPart = userId ?? 'anonymous';
    return `settings.notifications.${userPart}.${suffix}`;
  }

  private persistPreference(userId: number | null, suffix: string, value: string): void {
    if (typeof window === 'undefined') {
      return;
    }

    window.localStorage.setItem(this.notificationPrefKey(userId, suffix), value);
  }

  private readPreference(userId: number | null, suffix: string): string | null {
    if (typeof window === 'undefined') {
      return null;
    }

    return window.localStorage.getItem(this.notificationPrefKey(userId, suffix));
  }

  private buildReminderKey(appointment: Appointment, leadMinutes: number): string {
    const idPart = appointment.id ?? 'na';
    const startPart = appointment.start ?? 'na';
    return `${idPart}|${startPart}|${leadMinutes}`;
  }

  private readNotifiedReminderKeys(userId: number | null): Set<string> {
    const raw = this.readPreference(userId, 'notifiedReminderKeys');
    if (!raw) {
      return new Set<string>();
    }

    try {
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) {
        return new Set<string>();
      }

      return new Set<string>(parsed.filter((value) => typeof value === 'string'));
    } catch {
      return new Set<string>();
    }
  }

  private persistNotifiedReminderKeys(userId: number | null, keys: Set<string>): void {
    this.persistPreference(userId, 'notifiedReminderKeys', JSON.stringify(Array.from(keys)));
  }
}
