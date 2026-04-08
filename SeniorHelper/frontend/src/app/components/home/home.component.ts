import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Appointment } from '../../models/appointment.model';
import { AppointmentService } from '../../services/appointment.service';
import { AuthService } from '../../services/auth.service';

const MAX_UPCOMING_APPOINTMENTS = 4;
const DEFAULT_DISPLAY_NAME = 'friend';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  readonly displayName$: Observable<string>;
  readonly upcomingAppointments$: Observable<Appointment[]>;

  constructor(
    private readonly appointmentService: AppointmentService,
    private readonly authService: AuthService
  ) {
    this.displayName$ = this.loadDisplayName();
    this.upcomingAppointments$ = this.loadUpcomingAppointments();
  }

  private loadDisplayName(): Observable<string> {
    const fallbackName = this.authService.getUsername() || DEFAULT_DISPLAY_NAME;
    return this.authService.getMyFirstName().pipe(
      map((firstName) => firstName || fallbackName),
      catchError(() => of(fallbackName))
    );
  }

  private loadUpcomingAppointments(): Observable<Appointment[]> {
    return this.appointmentService.getMyAppointments().pipe(
      map((appointments) => this.toUpcomingAppointments(appointments)),
      catchError(() => of([]))
    );
  }

  private toUpcomingAppointments(appointments: Appointment[] | null | undefined): Appointment[] {
    const now = Date.now();
    return (appointments ?? [])
      .filter((appointment) => this.getStartTime(appointment) >= now)
      .sort((left, right) => this.getStartTime(left) - this.getStartTime(right))
      .slice(0, MAX_UPCOMING_APPOINTMENTS);
  }

  private getStartTime(appointment: Appointment): number {
    return appointment.start ? new Date(appointment.start).getTime() : 0;
  }
}
