import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CareLinkService } from '../../services/carelink.service';
import { AuthService } from '../../services/auth.service';
import { CareLinkModel } from '../../models/carelink.model';

@Component({
  selector: 'app-carelink',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './carelink.component.html',
  styleUrls: ['./carelink.component.css']
})
export class CarelinkComponent implements OnInit {
  connections: CareLinkModel[] = [];
  currentUserId: number | null = null;
  currentUserRole: string | null = null;
  newCaregiverId: number | null = null;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private carelinkService: CareLinkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.getMyProfile().subscribe({
      next: (profile) => {
        console.log('role', profile.role);
        this.currentUserId = profile.id;
        this.currentUserRole = profile.role;
        this.loadConnections();
      },
      error: (err) => console.error('Could not load user profile', err)
    });
  }

    loadConnections(): void {
    if (!this.currentUserId) return;

    if (this.currentUserRole === 'CAREGIVER') {
      this.carelinkService.getConnectionsByCaregiver(this.currentUserId)
        .subscribe({
          next: (data: CareLinkModel[]) => this.connections = data,
          error: (err) => console.error(err)
        });
    } else {
      this.carelinkService.getConnectionsBySenior(this.currentUserId)
        .subscribe({
          next: (data: CareLinkModel[]) => this.connections = data,
          error: (err) => console.error(err)
        });
    }
  }

createConnection(): void {
  if (this.newCaregiverId && this.currentUserId) {
    this.carelinkService.createConnection(this.newCaregiverId, this.currentUserId)
      .subscribe({
        next: (created: CareLinkModel) => {
            this.connections.push(created);
            this.successMessage = 'Connection created successfully!';
            this.errorMessage = '';
            this.resetForm();
            setTimeout(() => this.successMessage = '', 3000);
          },
          error: (err) => {
            if (err.status === 404) {
              this.errorMessage = 'No user found with that ID. Please check and try again.';
            } else if (err.status === 400) {
              this.errorMessage = 'That user cannot be added as a caregiver.';
            } else if (err.status === 409) {
              this.errorMessage = 'That caregiver is already connected to your account.';
            } else {
              this.errorMessage = 'Something went wrong. Please try again.';
            }
            this.successMessage = '';
            setTimeout(() => this.errorMessage = '', 3000);
          }
      });
  } else {
    this.errorMessage = 'Please enter a valid User ID to create a connection.';
    setTimeout(() => this.errorMessage = '', 3000);
  }
}

deleteConnection(conn: CareLinkModel): void {
  if (this.currentUserId) {
    const caregiverId = this.currentUserRole === 'CAREGIVER' ? this.currentUserId : conn.caregiverId;
    const seniorId = this.currentUserRole === 'CAREGIVER' ? conn.seniorId : this.currentUserId;

    this.carelinkService.deleteConnection(caregiverId, seniorId)
      .subscribe({
        next: () => {
          this.connections = this.connections.filter(c =>
            this.currentUserRole === 'CAREGIVER'
              ? c.seniorId !== conn.seniorId
              : c.caregiverId !== conn.caregiverId
          );
        },
        error: (err) => console.error('Error deleting connection', err)
      });
  }
}

  // deleteConnection(conn: CareLinkModel): void {
  //   if (this.currentUserId) {
  //     this.carelinkService.deleteConnection(conn.caregiverId, this.currentUserId)
  //       .subscribe({
  //         next: () => {
  //           this.connections = this.connections.filter(c => c.caregiverId !== conn.caregiverId);
  //         },
  //         error: (err) => console.error('Error deleting connection', err)
  //       });
  //   }
  // }

  private resetForm(): void {
    this.newCaregiverId = null;
  }
}