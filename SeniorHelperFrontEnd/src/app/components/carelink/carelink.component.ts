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
  currentSeniorId: number | null = null;

  newCaregiverId: number | null = null;
  newFirstName: string = '';
  newLastName: string = '';

  constructor(
    private carelinkService: CareLinkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.getMyProfile().subscribe({
      next: (profile) => {
        this.currentSeniorId = profile.id;
        this.loadConnections();
      },
      error: (err) => console.error('Could not load user profile', err)
    });
  }

  loadConnections(): void {
  if (this.currentSeniorId) {
    this.carelinkService.getConnectionsBySenior(this.currentSeniorId)
      .subscribe({
        next: (data: CareLinkModel[]) => {
          this.connections = data; 
          },
        error: (err) => console.error(err)
      });
  }
}

createConnection(): void {
  if (this.newCaregiverId && this.currentSeniorId) {
    this.carelinkService.createConnection(this.newCaregiverId, this.currentSeniorId)
      .subscribe({
        next: (created: CareLinkModel) => {
            this.connections.push(created);
            this.resetForm();
          },
        error: (err) => console.error('Error creating connection', err)
      });
  }
}

  deleteConnection(conn: CareLinkModel): void {
    if (this.currentSeniorId) {
      this.carelinkService.deleteConnection(conn.caregiverId, this.currentSeniorId)
        .subscribe({
          next: () => {
            this.connections = this.connections.filter(c => c.caregiverId !== conn.caregiverId);
          },
          error: (err) => console.error('Error deleting connection', err)
        });
    }
  }

  private resetForm(): void {
    this.newCaregiverId = null;
    this.newFirstName = '';
    this.newLastName = '';
  }
}