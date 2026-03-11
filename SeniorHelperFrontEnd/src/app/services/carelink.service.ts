import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CareLinkModel } from '../models/carelink.model';

@Injectable({
  providedIn: 'root',
})
export class CareLinkService {
  private apiUrl = 'http://localhost:8080/api/carelink';

  constructor(private http: HttpClient) {}

  // View all connections
  viewConnections(): Observable<CareLinkModel[]> {
    return this.http.get<CareLinkModel[]>(`${this.apiUrl}/view`);
  }

  // Create a new connection
  createConnection(caregiverId: number, seniorId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, { caregiverId, seniorId });
  }

  // Delete a connection
  deleteConnection(caregiverId: number, seniorId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/delete`, { caregiverId, seniorId });
  }
}
