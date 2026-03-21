import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Appointment } from '../models/appointment.model';

export interface CreateAppointmentRequest {
  title: string;
  notes?: string;
  location?: string;
  start?: string;
  end?: string;
}

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private readonly apiUrl = 'http://localhost:8080/api/appointments';
  private readonly usersApiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getMyAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/me`);
  }

  createMyAppointment(body: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.apiUrl}/me`, body);
  }

  updateAppointment(appointmentId: number, seniorId: number, body: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.apiUrl}/${appointmentId}?seniorId=${seniorId}`, body);
  }

  deleteAppointment(appointmentId: number, seniorId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${appointmentId}?seniorId=${seniorId}`);
  }

  getMyUserId(): Observable<number> {
    return this.http.get<{ id: number }>(`${this.usersApiUrl}/me`).pipe(map((user) => user.id));
  }
}
