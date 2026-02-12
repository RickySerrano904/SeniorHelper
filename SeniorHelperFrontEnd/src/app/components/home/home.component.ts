import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { Appointment } from '../../models/appointment.model';
import { AppointmentService } from '../../services/appointment.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  readonly displayName: string;
  readonly upcomingAppointments$: Observable<Appointment[]>;

  constructor(
    private authService: AuthService,
    private appointmentService: AppointmentService
  ) {
    this.displayName = this.authService.getUsername() || 'friend';
    this.upcomingAppointments$ = this.appointmentService.getMyAppointments().pipe(
      map((appointments) => this.toUpcomingAppointments(appointments)),
      catchError(() => of([]))
    );
  }

  private toUpcomingAppointments(appointments: Appointment[] | null | undefined): Appointment[] {
    const now = Date.now();
    return (appointments ?? [])
      .filter((appointment) => this.getStartTime(appointment) >= now)
      .sort((left, right) => this.getStartTime(left) - this.getStartTime(right))
      .slice(0, 3);
  }

  private getStartTime(appointment: Appointment): number {
    return appointment.start ? new Date(appointment.start).getTime() : 0;
  }
}
