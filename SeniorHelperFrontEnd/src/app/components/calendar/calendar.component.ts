import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AppointmentService } from '../../services/appointment.service';
import { Appointment } from '../../models/appointment.model';

interface DayCell {
  date: Date;
  inMonth: boolean;
  iso: string;
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css'
})
export class CalendarComponent implements OnInit, OnDestroy {
  today = new Date();
  displayMonth = this.today.getMonth();
  displayYear = this.today.getFullYear();
  monthNames = [
    'January','February','March','April','May','June','July','August','September','October','November','December'
  ];

  weekDays = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];

  weeks: DayCell[][] = [];

  // events keyed by ISO date (yyyy-mm-dd)
  events: Record<string, Appointment[]> = {};
  // holidays keyed by ISO date (yyyy-mm-dd)
  holidays: Record<string, string> = {};

  appointmentForm: FormGroup;
  submitError = '';
  submitSuccess = '';
  isSubmitting = false;
  isAddAppointmentOpen = false;
  selectedDateLabel = '';
  currentUserId: number | null = null;
  editingAppointmentId: number | null = null;
  contextMenuVisible = false;
  contextMenuX = 0;
  contextMenuY = 0;
  contextMenuDayIso = '';
  contextMenuAppointment: Appointment | null = null;
  hasAppointmentsThisMonth = false;
  appointmentsLoaded = false;
  isMonthYearPickerOpen = false;
  pickerMonth = this.displayMonth;
  pickerYear = this.displayYear;
  yearOptions = this.buildYearOptions();
  private successMessageTimeoutId: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService
  ) {
    this.appointmentForm = this.fb.group({
      title: ['', Validators.required], notes: [''], location: [''], start: ['', Validators.required], end: ['']
    });
  }

  ngOnInit(): void {
    this.appointmentService.getMyUserId().subscribe({
      next: (userId) => {
        this.currentUserId = userId;
      },
      error: () => {
        this.currentUserId = null;
      }
    });
    this.loadAppointments();
    this.holidays = this.generateHolidaysForYear(this.displayYear);
    this.syncPickerToDisplayDate();
    this.buildCalendar();
  }

  ngOnDestroy(): void {
    if (this.successMessageTimeoutId) {
      clearTimeout(this.successMessageTimeoutId);
      this.successMessageTimeoutId = null;
    }
    this.closeContextMenu();
  }

  prevMonth() {
    if (this.displayMonth === 0) {
      this.displayMonth = 11;
      this.displayYear -= 1;
    } else {
      this.displayMonth -= 1;
    }
    this.holidays = this.generateHolidaysForYear(this.displayYear);
    this.syncPickerToDisplayDate();
    this.buildCalendar();
    this.refreshHasAppointmentsState();
  }

  nextMonth() {
    if (this.displayMonth === 11) {
      this.displayMonth = 0;
      this.displayYear += 1;
    } else {
      this.displayMonth += 1;
    }
    this.holidays = this.generateHolidaysForYear(this.displayYear);
    this.syncPickerToDisplayDate();
    this.buildCalendar();
    this.refreshHasAppointmentsState();
  }

  toggleMonthYearPicker(event: MouseEvent) {
    event.stopPropagation();
    if (!this.isMonthYearPickerOpen) {
      this.pickerMonth = this.today.getMonth();
      this.pickerYear = this.today.getFullYear();
    }
    this.isMonthYearPickerOpen = !this.isMonthYearPickerOpen;
  }

  closeMonthYearPicker() {
    this.isMonthYearPickerOpen = false;
  }

  onMonthYearSelectionClick(event: MouseEvent) {
    event.stopPropagation();
  }

  setPickerMonth(value: string) {
    const parsedMonth = Number(value);
    this.pickerMonth = Number.isNaN(parsedMonth) ? this.today.getMonth() : parsedMonth;
  }

  setPickerYear(value: string) {
    const parsedYear = Number(value);
    this.pickerYear = Number.isNaN(parsedYear) ? this.today.getFullYear() : parsedYear;
  }

  applyMonthYearSelection() {
    this.displayMonth = this.pickerMonth;
    this.displayYear = this.pickerYear;
    this.holidays = this.generateHolidaysForYear(this.displayYear);
    this.buildCalendar();
    this.refreshHasAppointmentsState();
    this.closeMonthYearPicker();
  }

  buildCalendar() {
    const firstOfMonth = new Date(this.displayYear, this.displayMonth, 1);
    const startDay = firstOfMonth.getDay(); // 0 = Sunday
    const daysInMonth = new Date(this.displayYear, this.displayMonth + 1, 0).getDate();

    // previous month filler
    const prevMonthLastDay = new Date(this.displayYear, this.displayMonth, 0).getDate();

    const cells: DayCell[] = [];

    // leading days from previous month
    for (let i = startDay - 1; i >= 0; i--) {
      const date = new Date(this.displayYear, this.displayMonth - 1, prevMonthLastDay - i);
      cells.push({ date, inMonth: false, iso: this.isoDate(date) });
    }

    // current month
    for (let d = 1; d <= daysInMonth; d++) {
      const date = new Date(this.displayYear, this.displayMonth, d);
      cells.push({ date, inMonth: true, iso: this.isoDate(date) });
    }

    // trailing next month days to fill last week
    while (cells.length % 7 !== 0) {
      const nextIndex = cells.length - (startDay + daysInMonth);
      const date = new Date(this.displayYear, this.displayMonth + 1, nextIndex + 1);
      cells.push({ date, inMonth: false, iso: this.isoDate(date) });
    }

    // split into weeks
    this.weeks = [];
    for (let i = 0; i < cells.length; i += 7) {
      this.weeks.push(cells.slice(i, i + 7));
    }
  }

  isoDate(d: Date) {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  isToday(cell: DayCell) {
    return this.isoDate(cell.date) === this.isoDate(this.today);
  }

  hasEvents(cell: DayCell) {
    return (this.events[cell.iso] || []).length > 0;
  }

  getHolidayName(cell: DayCell): string | null {
    return this.holidays[cell.iso] || null;
  }

  openAddAppointment(event: MouseEvent, day: DayCell) {
    event.preventDefault();
    this.openAddAppointmentForDate(day.date);
  }

  openAddAppointmentFromButton() {
    const isDisplayedMonthToday =
      this.displayMonth === this.today.getMonth() && this.displayYear === this.today.getFullYear();
    const defaultDate = isDisplayedMonthToday
      ? this.today
      : new Date(this.displayYear, this.displayMonth, 1);

    this.openAddAppointmentForDate(defaultDate);
  }

  private openAddAppointmentForDate(date: Date) {
    this.closeContextMenu();
    this.editingAppointmentId = null;
    const startLocal = this.toDateTimeLocal(date, 9, 0);
    const endLocal = this.toDateTimeLocal(date, 10, 0);

    this.selectedDateLabel = date.toLocaleDateString(undefined, {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });
    this.submitError = '';
    this.submitSuccess = '';
    this.appointmentForm.reset({
      title: '',
      notes: '',
      location: '',
      start: startLocal,
      end: endLocal
    });
    this.isAddAppointmentOpen = true;
  }

  openAppointmentMenu(event: MouseEvent, day: DayCell, appointment: Appointment) {
    event.preventDefault();
    event.stopPropagation();
    this.contextMenuVisible = true;
    this.contextMenuX = event.clientX;
    this.contextMenuY = event.clientY;
    this.contextMenuDayIso = day.iso;
    this.contextMenuAppointment = appointment;
  }

  openDayMenu(event: MouseEvent, day: DayCell) {
    event.preventDefault();
    event.stopPropagation();
    this.contextMenuVisible = true;
    this.contextMenuX = event.clientX;
    this.contextMenuY = event.clientY;
    this.contextMenuDayIso = day.iso;
    this.contextMenuAppointment = null;
  }

  closeContextMenu() {
    this.contextMenuVisible = false;
    this.contextMenuDayIso = '';
    this.contextMenuAppointment = null;
  }

  openAddFromMenu() {
    const iso = this.contextMenuDayIso;
    this.closeContextMenu();
    if (!iso) {
      return;
    }
    const day = new Date(`${iso}T00:00:00`);
    this.openAddAppointment(new MouseEvent('contextmenu'), { date: day, inMonth: true, iso });
  }

  openEditFromMenu() {
    const appointment = this.contextMenuAppointment;
    if (!appointment || appointment.id == null || !appointment.start) {
      this.closeContextMenu();
      return;
    }

    this.openEditForAppointment(appointment);
    this.closeContextMenu();
  }

  openEditFromTitle(event: MouseEvent, appointment: Appointment) {
    event.preventDefault();
    event.stopPropagation();
    this.openEditForAppointment(appointment);
  }

  private openEditForAppointment(appointment: Appointment) {
    if (appointment.id == null || !appointment.start) {
      return;
    }

    const day = new Date(`${appointment.start.split('T')[0]}T00:00:00`);
    this.selectedDateLabel = day.toLocaleDateString(undefined, {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric'
    });

    this.editingAppointmentId = appointment.id;
    this.submitError = '';
    this.submitSuccess = '';
    this.appointmentForm.reset({
      title: appointment.title,
      notes: appointment.notes ?? '',
      location: appointment.location ?? '',
      start: appointment.start.slice(0, 16),
      end: appointment.end ? appointment.end.slice(0, 16) : ''
    });
    this.isAddAppointmentOpen = true;
  }

  deleteFromMenu() {
    if (this.currentUserId == null || this.contextMenuAppointment?.id == null) {
      this.submitError = 'Could not determine current user for delete.';
      this.closeContextMenu();
      return;
    }

    this.isSubmitting = true;
    this.appointmentService.deleteAppointment(this.contextMenuAppointment.id, this.currentUserId).subscribe({
      next: () => {
        const deletedId = this.contextMenuAppointment?.id;
        if (deletedId != null) {
          for (const day of Object.keys(this.events)) {
            this.events[day] = (this.events[day] || []).filter((appointment) => appointment.id !== deletedId);
          }
          this.refreshHasAppointmentsState();
        }
        this.loadAppointments();
        this.submitSuccess = 'Appointment deleted successfully.';
        this.startSuccessMessageTimer();
        this.isSubmitting = false;
        this.closeContextMenu();
      },
      error: (error) => {
        this.submitError = this.getSubmitErrorMessage(error);
        this.isSubmitting = false;
        this.closeContextMenu();
      }
    });
  }

  private hasAppointmentsInDisplayedMonth() {
    const month = String(this.displayMonth + 1).padStart(2, '0');
    const prefix = `${this.displayYear}-${month}-`;

    return Object.keys(this.events).some((date) =>
      date.startsWith(prefix) && (this.events[date] || []).length > 0
    );
  }

  private refreshHasAppointmentsState() {
    this.hasAppointmentsThisMonth = this.hasAppointmentsInDisplayedMonth();
  }

  private loadAppointments() {
    this.appointmentService.getMyAppointments().subscribe({
      next: (appointments) => {
        this.events = this.mapAppointmentsToEvents(appointments);
        this.refreshHasAppointmentsState();
        this.appointmentsLoaded = true;
      },
      error: (error) => {
        console.error('Failed to load appointments', error);
        this.refreshHasAppointmentsState();
        this.appointmentsLoaded = true;
      }
    });
  }

  private mapAppointmentsToEvents(appointments: Appointment[]): Record<string, Appointment[]> {
    const mapped: Record<string, Appointment[]> = {};

    for (const appointment of appointments) {
      if (!appointment.start || !appointment.title) {
        continue;
      }

      const isoDate = this.extractDateKey(appointment.start);
      if (!isoDate) {
        continue;
      }

      if (!mapped[isoDate]) {
        mapped[isoDate] = [];
      }
      mapped[isoDate].push(appointment);
    }

    return mapped;
  }

  private extractDateKey(value?: string | null): string | null {
    if (!value) {
      return null;
    }

    const trimmed = value.trim();
    const match = trimmed.match(/^(\d{4}-\d{2}-\d{2})/);
    return match ? match[1] : null;
  }

  addAppointment() {
    this.submitError = '';
    this.submitSuccess = '';

    if (this.appointmentForm.invalid) {
      this.appointmentForm.markAllAsTouched();
      this.submitError = 'Please enter a title and start date/time before submitting.';
      return;
    }

    const formValue = this.appointmentForm.value;
    const title = (formValue.title as string | null)?.trim();
    const start = formValue.start as string | null;
    const end = formValue.end as string | null;

    if (!title || !start) {
      this.submitError = 'Please enter a title and start date/time before submitting.';
      return;
    }

    const payload = {
      title,
      notes: (formValue.notes as string | null)?.trim() || undefined,
      location: (formValue.location as string | null)?.trim() || undefined,
      start: start.length === 16 ? `${start}:00` : start,
      end: end ? (end.length === 16 ? `${end}:00` : end) : undefined
    };

    if (this.currentUserId == null) {
      this.submitError = 'Could not determine current user.';
      return;
    }

    this.isSubmitting = true;
    const request$ = this.editingAppointmentId == null
      ? this.appointmentService.createMyAppointment(payload)
      : this.appointmentService.updateAppointment(this.editingAppointmentId, this.currentUserId, payload);

    request$.subscribe({
      next: (created) => {
        const createdDate = this.extractDateKey(created.start) ?? this.extractDateKey(start);
        if (!createdDate) {
          this.loadAppointments();
          this.submitSuccess = this.editingAppointmentId == null
            ? 'Appointment saved successfully.'
            : 'Appointment updated successfully.';
          this.startSuccessMessageTimer();
          this.editingAppointmentId = null;
          this.isAddAppointmentOpen = false;
          this.appointmentForm.reset();
          this.isSubmitting = false;
          return;
        }

        const createdDateObj = new Date(`${createdDate}T00:00:00`);

        const existingForDay = this.events[createdDate] || [];
        const withoutCreated = existingForDay.filter((appointment) => appointment.id !== created.id);
        this.events = {
          ...this.events,
          [createdDate]: [...withoutCreated, created]
        };
        this.refreshHasAppointmentsState();

        this.loadAppointments();

        this.displayMonth = createdDateObj.getMonth();
        this.displayYear = createdDateObj.getFullYear();
        this.syncPickerToDisplayDate();
        this.buildCalendar();
        this.refreshHasAppointmentsState();

        this.submitSuccess = this.editingAppointmentId == null
          ? 'Appointment saved successfully.'
          : 'Appointment updated successfully.';
        this.startSuccessMessageTimer();
        this.editingAppointmentId = null;
        this.isAddAppointmentOpen = false;
        this.appointmentForm.reset();
        this.isSubmitting = false;
      },
      error: (error) => {
        console.error('Failed to create appointment', error);
        this.submitError = this.getSubmitErrorMessage(error);
        this.submitSuccess = '';
        this.isSubmitting = false;
      }
    });
  }

  cancel() {
    this.appointmentForm.reset();
    this.submitError = '';
    this.submitSuccess = '';
    this.editingAppointmentId = null;
    this.selectedDateLabel = '';
    this.isAddAppointmentOpen = false;
    this.closeContextMenu();
    if (this.successMessageTimeoutId) {
      clearTimeout(this.successMessageTimeoutId);
      this.successMessageTimeoutId = null;
    }
  }

  private toDateTimeLocal(date: Date, hour: number, minute: number) {
    const local = new Date(date);
    local.setHours(hour, minute, 0, 0);
    const year = local.getFullYear();
    const month = String(local.getMonth() + 1).padStart(2, '0');
    const day = String(local.getDate()).padStart(2, '0');
    const h = String(local.getHours()).padStart(2, '0');
    const m = String(local.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${h}:${m}`;
  }

  private startSuccessMessageTimer() {
    if (this.successMessageTimeoutId) {
      clearTimeout(this.successMessageTimeoutId);
    }

    this.successMessageTimeoutId = setTimeout(() => {
      this.submitSuccess = '';
      this.successMessageTimeoutId = null;
    }, 15000);
  }

  private getSubmitErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Could not save appointment. Please try again.';
    }

    if (error.status === 0) {
      return 'Cannot reach the server. Make sure the backend is running.';
    }

    const apiMessage = typeof error.error === 'object' && error.error?.message
      ? String(error.error.message)
      : '';

    if (error.status === 401) {
      return apiMessage || 'Your session expired. Please log in again.';
    }

    if (error.status === 403) {
      return apiMessage || 'You do not have permission to add appointments.';
    }

    if (error.status === 400) {
      return apiMessage || 'The appointment data is invalid. Check your inputs and try again.';
    }

    return apiMessage || `Request failed with status ${error.status}.`;
  }

  private syncPickerToDisplayDate() {
    this.pickerMonth = this.displayMonth;
    this.pickerYear = this.displayYear;
  }

  private buildYearOptions(): number[] {
    const startYear = this.today.getFullYear() - 15;
    return Array.from({ length: 31 }, (_, index) => startYear + index);
  }

  private generateHolidaysForYear(year: number): Record<string, string> {
    const holidays: Record<string, string> = {};

    // Fixed date holidays
    holidays[this.isoDate(new Date(year, 0, 1))] = "New Year's Day";
    holidays[this.isoDate(new Date(year, 6, 4))] = "Independence Day";
    holidays[this.isoDate(new Date(year, 10, 11))] = "Veterans Day";
    holidays[this.isoDate(new Date(year, 11, 25))] = "Christmas";

    // Holidays based on day of week
    // Martin Luther King Jr. Day (3rd Monday in January)
    holidays[this.isoDate(this.getNthWeekdayOfMonth(year, 0, 1, 3))] = "MLK Jr. Day";

    // Presidents Day (3rd Monday in February)
    holidays[this.isoDate(this.getNthWeekdayOfMonth(year, 1, 1, 3))] = "Presidents Day";

    // Memorial Day (last Monday in May)
    holidays[this.isoDate(this.getLastWeekdayOfMonth(year, 4, 1))] = "Memorial Day";

    // Labor Day (1st Monday in September)
    holidays[this.isoDate(this.getNthWeekdayOfMonth(year, 8, 1, 1))] = "Labor Day";

    // Columbus Day (2nd Monday in October)
    holidays[this.isoDate(this.getNthWeekdayOfMonth(year, 9, 1, 2))] = "Columbus Day";

    // Thanksgiving (4th Thursday in November)
    holidays[this.isoDate(this.getNthWeekdayOfMonth(year, 10, 4, 4))] = "Thanksgiving";

    return holidays;
  }

  private getNthWeekdayOfMonth(year: number, month: number, weekday: number, n: number): Date {
    let count = 0;
    for (let day = 1; day <= 31; day++) {
      const date = new Date(year, month, day);
      if (date.getMonth() !== month) break;
      if (date.getDay() === weekday) {
        count++;
        if (count === n) return date;
      }
    }
    return new Date(year, month, 1);
  }

  private getLastWeekdayOfMonth(year: number, month: number, weekday: number): Date {
    const lastDay = new Date(year, month + 1, 0).getDate();
    for (let day = lastDay; day >= 1; day--) {
      const date = new Date(year, month, day);
      if (date.getDay() === weekday) return date;
    }
    return new Date(year, month, 1);
  }
}
