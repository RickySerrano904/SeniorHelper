import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type InAppNotificationTone = 'info' | 'success' | 'warning' | 'error';

export interface InAppNotification {
  id: number;
  title: string;
  message: string;
  tone: InAppNotificationTone;
}

interface InAppNotificationOptions {
  title: string;
  message?: string;
  tone?: InAppNotificationTone;
  timeoutMs?: number;
}

@Injectable({ providedIn: 'root' })
export class InAppNotificationService {
  private readonly defaultTimeoutMs = 8000;
  private readonly notificationsSubject = new BehaviorSubject<InAppNotification[]>([]);
  private nextId = 1;
  private dismissTimers = new Map<number, ReturnType<typeof setTimeout>>();

  readonly notifications$ = this.notificationsSubject.asObservable();

  show(options: InAppNotificationOptions): number {
    const id = this.nextId++;
    const notification: InAppNotification = {
      id,
      title: options.title,
      message: options.message ?? '',
      tone: options.tone ?? 'info'
    };

    this.notificationsSubject.next([...this.notificationsSubject.value, notification]);

    const timeoutMs = options.timeoutMs ?? this.defaultTimeoutMs;
    if (timeoutMs > 0) {
      const timer = setTimeout(() => this.dismiss(id), timeoutMs);
      this.dismissTimers.set(id, timer);
    }

    return id;
  }

  dismiss(id: number): void {
    const timer = this.dismissTimers.get(id);
    if (timer) {
      clearTimeout(timer);
      this.dismissTimers.delete(id);
    }

    this.notificationsSubject.next(
      this.notificationsSubject.value.filter((notification) => notification.id !== id)
    );
  }
}
