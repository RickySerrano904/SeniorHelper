import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

// 1. Define the connection interface to ensure type safety
interface CareLinkModel {
  caregiverName: string;
  role: string;
  connectedSince: Date;
}

@Component({
  selector: 'app-carelink',
  standalone: true,
  // 2. Import CommonModule for *ngFor and FormsModule for [(ngModel)]
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './carelink.component.html',
  styleUrls: ['./carelink.component.css']
})
export class CarelinkComponent implements OnInit {

  // 3. Form variables to bind to the inputs
  newSeniorId: number | null = null;
  newFirstName: string = '';
  newLastName: string = '';

  // 4. Initial mock data to populate the 'View Connections' grid
  connections: CareLinkModel[] = [
    { caregiverName: 'Nolan, Frank', role: 'Caregiver', connectedSince: new Date('2026-01-15') },
    { caregiverName: 'Hernandez, Julia', role: 'Family', connectedSince: new Date('2026-02-05') },
    { caregiverName: 'Watson, Eric', role: 'Family', connectedSince: new Date('2026-03-20') }
  ];

  constructor() { }

  ngOnInit(): void {
  const savedConnections = localStorage.getItem('my_connections');
  if (savedConnections) {
    // Convert the saved string back into a JavaScript array
    this.connections = JSON.parse(savedConnections);
  }
}

  // 5. Logic to add a new connection to the right-hand grid
  createConnection(): void {
    if (this.newFirstName && this.newLastName && this.newSeniorId) {
      const newConn: CareLinkModel = {
        caregiverName: `${this.newFirstName} ${this.newLastName}`,
        role: 'Caregiver', // Default role for new additions
        connectedSince: new Date()
      };
      
      this.connections.push(newConn);
      this.saveToLocalStorage();
    }
  }

  // 6. Logic to delete a connection using the index from *ngFor
  deleteConnection(index: number): void {
    if (index > -1) {
      this.connections.splice(index, 1);
      this.saveToLocalStorage();
    }
  }

 private saveToLocalStorage(): void {
  // Save the array as a string so the browser can store it
  localStorage.setItem('my_connections', JSON.stringify(this.connections));
} 

  // Helper to clear form after submission
  private resetForm(): void {
    this.newSeniorId = null;
    this.newFirstName = '';
    this.newLastName = '';
  }
}