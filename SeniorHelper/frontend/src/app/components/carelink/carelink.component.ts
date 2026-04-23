import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CareLinkService } from '../../services/carelink.service';
import { AuthService, UserProfileResponse } from '../../services/auth.service';
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
  pendingRequests: CareLinkModel[] = [];
  seniors: UserProfileResponse[] = [];
  selectedSeniorId: number | null = null;
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

        if (this.currentUserRole === 'ADMIN') {
          this.loadSeniors();
        } else {
          this.selectedSeniorId = this.currentUserId;
          this.loadConnections();
          this.loadPendingRequests();
        }
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
      const seniorId = this.currentUserRole === 'ADMIN' ? this.selectedSeniorId : this.currentUserId;
      if (!seniorId) {
        this.connections = [];
        return;
      }

      this.carelinkService.getConnectionsBySenior(seniorId)
        .subscribe({
          next: (data: CareLinkModel[]) => this.connections = data,
          error: (err) => console.error(err)
        });
    }
  }

  loadSeniors(): void {
    this.authService.getSeniors().subscribe({
      next: (data: UserProfileResponse[]) => {
        this.seniors = data;
        this.selectedSeniorId = data.length > 0 ? data[0].id : null;
        this.loadConnections();
      },
      error: (err) => {
        console.error(err);
        this.seniors = [];
        this.selectedSeniorId = null;
        this.connections = [];
      }
    });
  }

  onSeniorSelectionChange(seniorId: number | null): void {
    this.selectedSeniorId = seniorId;
    this.errorMessage = '';
    this.successMessage = '';
    this.loadConnections();
  }

  loadPendingRequests(): void {
    if (!this.currentUserId || this.currentUserRole !== 'CAREGIVER') {
      this.pendingRequests = [];
      return;
    }

    this.carelinkService.getPendingRequestsByCaregiver(this.currentUserId)
      .subscribe({
        next: (data: CareLinkModel[]) => this.pendingRequests = data,
        error: (err) => console.error(err)
      });
  }

createConnection(): void {
  const seniorId = this.currentUserRole === 'ADMIN' ? this.selectedSeniorId : this.currentUserId;

  if (this.newCaregiverId && seniorId) {
    this.carelinkService.createConnection(this.newCaregiverId, seniorId)
      .subscribe({
        next: () => {
            if (this.currentUserRole === 'ADMIN') {
              this.carelinkService.approveConnection(this.newCaregiverId!, seniorId).subscribe({
                next: () => {
                  this.successMessage = 'Connection created successfully.';
                  this.errorMessage = '';
                  this.loadConnections();
                  this.resetForm();
                  setTimeout(() => this.successMessage = '', 3000);
                },
                error: () => {
                  this.errorMessage = 'Connection request was created, but could not be auto-approved.';
                  this.successMessage = '';
                  setTimeout(() => this.errorMessage = '', 3000);
                }
              });
              return;
            }

            this.successMessage = 'Connection request sent. It will appear after the caregiver approves it.';
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
              this.errorMessage = 'That caregiver already has a request or connection for your account.';
            } else {
              this.errorMessage = 'Something went wrong. Please try again.';
            }
            this.successMessage = '';
            setTimeout(() => this.errorMessage = '', 3000);
          }
      });
  } else {
    this.errorMessage = this.currentUserRole === 'ADMIN'
      ? 'Please select a senior and enter a valid caregiver User ID.'
      : 'Please enter a valid User ID to create a connection.';
    setTimeout(() => this.errorMessage = '', 3000);
  }
}

approveRequest(conn: CareLinkModel): void {
  this.carelinkService.approveConnection(conn.caregiverId, conn.seniorId)
    .subscribe({
      next: () => {
        this.successMessage = 'Connection request approved.';
        this.errorMessage = '';
        this.loadConnections();
        this.loadPendingRequests();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (err) => {
        console.error('Error approving connection request', err);
        this.errorMessage = 'Could not approve the request. Please try again.';
        this.successMessage = '';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
}

declineRequest(conn: CareLinkModel): void {
  this.carelinkService.deleteConnection(conn.caregiverId, conn.seniorId)
    .subscribe({
      next: () => {
        this.pendingRequests = this.pendingRequests.filter(request => request.id !== conn.id);
      },
      error: (err) => {
        console.error('Error declining connection request', err);
        this.errorMessage = 'Could not decline the request. Please try again.';
        this.successMessage = '';
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
}

deleteConnection(conn: CareLinkModel): void {
  if (this.currentUserId) {
    const caregiverId = this.currentUserRole === 'CAREGIVER' ? this.currentUserId : conn.caregiverId;
    const seniorId = this.currentUserRole === 'CAREGIVER'
      ? conn.seniorId
      : (this.currentUserRole === 'ADMIN' ? this.selectedSeniorId : this.currentUserId);

    if (!seniorId) {
      this.errorMessage = 'Please select a senior first.';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

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

  private resetForm(): void {
    this.newCaregiverId = null;
  }
}