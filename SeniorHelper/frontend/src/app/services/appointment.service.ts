import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Appointment } from '../models/appointment.model';
import { apiEndpoint } from '../config/api.config';

export interface AppointmentDto {
  id?: number;
  title: string;
  notes?: string;
  location?: string;
  start?: string;
  end?: string;
}

export interface CreateAppointmentRequest {
  title: string;
  notes?: string;
  location?: string;
  start?: string;
  end?: string;
}

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private readonly apiUrl = apiEndpoint('/appointments');
  private readonly usersApiUrl = apiEndpoint('/users');

  constructor(private http: HttpClient) {}

  // Legacy endpoint shape used by older calendar implementation.
  createAppointment(seniorId: number, appointment: AppointmentDto): Observable<AppointmentDto> {
    const params = new HttpParams().set('seniorId', seniorId);
    return this.http.post<AppointmentDto>(this.apiUrl, appointment, { params });
  }

  getMyAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/me`);
  }

  getAppointmentsBySenior(seniorId: number): Observable<Appointment[]> {
    const params = new HttpParams().set('seniorId', seniorId);
    return this.http.get<Appointment[]>(this.apiUrl, { params });
  }

  createMyAppointment(body: CreateAppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.apiUrl}/me`, body);
  }

  createAppointmentForSenior(seniorId: number, body: CreateAppointmentRequest): Observable<Appointment> {
    const params = new HttpParams().set('seniorId', seniorId);
    return this.http.post<Appointment>(this.apiUrl, body, { params });
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
