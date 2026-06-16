import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AppointmentService, CreateAppointmentRequest } from './appointment.service';

describe('AppointmentService', () => {
  let service: AppointmentService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AppointmentService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('loads appointments for the current user', () => {
    const appointments = [
      {
        id: 1,
        title: 'Doctor visit',
        start: '2026-06-16T09:00:00Z'
      }
    ];

    service.getMyAppointments().subscribe((result) => {
      expect(result).toEqual(appointments);
    });

    const request = httpTesting.expectOne('/api/appointments/me');
    expect(request.request.method).toBe('GET');

    request.flush(appointments);
  });

  it('creates an appointment for a senior with the senior id query parameter', () => {
    const body: CreateAppointmentRequest = {
      title: 'Check-in',
      notes: 'Bring paperwork'
    };

    service.createAppointmentForSenior(42, body).subscribe((result) => {
      expect(result.title).toBe('Check-in');
    });

    const request = httpTesting.expectOne((candidate) => candidate.url === '/api/appointments');
    expect(request.request.method).toBe('POST');
    expect(request.request.params.get('seniorId')).toBe('42');
    expect(request.request.body).toEqual(body);

    request.flush({ id: 9, title: 'Check-in' });
  });

  it('updates an appointment using the appointment and senior ids', () => {
    const body: CreateAppointmentRequest = {
      title: 'Updated visit'
    };

    service.updateAppointment(7, 42, body).subscribe((result) => {
      expect(result.id).toBe(7);
    });

    const request = httpTesting.expectOne('/api/appointments/7?seniorId=42');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(body);

    request.flush({ id: 7, title: 'Updated visit' });
  });

  it('maps the current user profile response to a user id', () => {
    service.getMyUserId().subscribe((result) => {
      expect(result).toBe(12);
    });

    const request = httpTesting.expectOne('/api/users/me');
    expect(request.request.method).toBe('GET');

    request.flush({ id: 12 });
  });
});
