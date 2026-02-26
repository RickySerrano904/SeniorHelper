import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

export interface CareLinkModel {
  caregiverId: number;
  caregiverName: string;
  seniorId: number;
  seniorName: string;
  role: string;
  connectedSince: string;
}

@Component({
  selector: 'app-carelink',
  standalone: true, // IMPORTANT
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './carelink.component.html',
  styleUrls: ['./carelink.component.css']
})
export class CarelinkComponent implements OnInit {
  newSeniorId: number | null = null;
  newFirstName: string = '';
  newLastName: string = '';

  connections: CareLinkModel[] = [];

  ngOnInit(): void {
    this.connections = [
      {
        caregiverId: 1,
        caregiverName: 'Nolan, Frank',
        seniorId: 101,
        seniorName: 'Caregiver',
        role: 'Caregiver',
        connectedSince: '2026-01-15'
      },
      {
        caregiverId: 2,
        caregiverName: 'Hernandez, Julia',
        seniorId: 102,
        seniorName: 'Family',
        role: 'Family',
        connectedSince: '2026-01-16'
      },
      {
        caregiverId: 3,
        caregiverName: 'Watson, Eric',
        seniorId: 103,
        seniorName: 'Family',
        role: 'Family',
        connectedSince: '2026-01-16'
      }
    ];
  }

  createConnection(): void {
    if (!this.newSeniorId || !this.newFirstName || !this.newLastName) return;

    const newConn: CareLinkModel = {
      caregiverId: Date.now(),
      caregiverName: `${this.newFirstName}, ${this.newLastName}`,
      seniorId: this.newSeniorId,
      seniorName: 'Family',
      role: 'Family',
      connectedSince: new Date().toISOString().split('T')[0]
    };

    this.connections.push(newConn);

    this.newSeniorId = null;
    this.newFirstName = '';
    this.newLastName = '';
  }

  deleteConnection(index: number): void {
    this.connections.splice(index, 1);
  }
}
