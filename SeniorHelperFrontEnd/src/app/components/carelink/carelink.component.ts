import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CareLinkModel } from '../../models/carelink.model';
import { CareLinkService } from '../../services/carelink.service';

@Component({
  selector: 'app-carelink',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './carelink.component.html',
  styleUrls: ['./carelink.component.css'],
})
export class CarelinkComponent implements OnInit {
  connections: CareLinkModel[] = [];

  // Fields for creating a connection
  newSeniorId: number | null = null;
  newFirstName: string = '';
  newLastName: string = '';

  constructor(private careLinkService: CareLinkService) {}

  ngOnInit(): void {
    this.loadConnections();
  }

  loadConnections(): void {
    this.careLinkService.viewConnections().subscribe({
      next: (data) => (this.connections = data),
      error: (err) => console.error(err),
    });
  }

  createConnection(): void {
    if (this.newSeniorId !== null) {
      const caregiverId = 1; // Replace with actual logged-in caregiver ID
      this.careLinkService.createConnection(caregiverId, this.newSeniorId).subscribe({
        next: () => {
          this.newSeniorId = null;
          this.newFirstName = '';
          this.newLastName = '';
          this.loadConnections(); // refresh list after creation
        },
        error: (err) => console.error(err),
      });
    }
  }

  deleteConnection(seniorId: number): void {
    const caregiverId = 1;
    this.careLinkService.deleteConnection(caregiverId, seniorId).subscribe({
      next: () => this.loadConnections(),
      error: (err) => console.error(err),
    });
  }
}
