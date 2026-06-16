import { TestBed } from '@angular/core/testing';
import { InAppNotification, InAppNotificationService } from './in-app-notification.service';

describe('InAppNotificationService', () => {
  let service: InAppNotificationService;
  let notifications: InAppNotification[];

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InAppNotificationService);
    notifications = [];
    service.notifications$.subscribe((nextNotifications) => {
      notifications = nextNotifications;
    });
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('adds notifications with default values', () => {
    const id = service.show({ title: 'Saved' });

    expect(id).toBe(1);
    expect(notifications).toEqual([
      {
        id: 1,
        title: 'Saved',
        message: '',
        tone: 'info'
      }
    ]);
  });

  it('dismisses a notification by id', () => {
    const firstId = service.show({ title: 'First', timeoutMs: 0 });
    service.show({ title: 'Second', timeoutMs: 0 });

    service.dismiss(firstId);

    expect(notifications).toEqual([
      {
        id: 2,
        title: 'Second',
        message: '',
        tone: 'info'
      }
    ]);
  });

  it('auto-dismisses notifications when a timeout is set', () => {
    vi.useFakeTimers();

    service.show({ title: 'Reminder', timeoutMs: 1000 });

    expect(notifications.length).toBe(1);

    vi.advanceTimersByTime(1000);

    expect(notifications).toEqual([]);
  });
});
