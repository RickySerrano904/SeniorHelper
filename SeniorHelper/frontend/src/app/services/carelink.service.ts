import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CareLinkModel } from '../models/carelink.model';
import { apiEndpoint } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class CareLinkService {
  private apiUrl = apiEndpoint('/care-links');

  constructor(private http: HttpClient) {}

  // Get all connections for a specific senior
  getConnectionsBySenior(seniorId: number): Observable<CareLinkModel[]> {
    const params = new HttpParams().set('seniorId', seniorId);
    return this.http.get<CareLinkModel[]>(`${this.apiUrl}/by-senior`, {params})
  }

  // Get all connections for a specific caregiver
  getConnectionsByCaregiver(caregiverId: number): Observable<CareLinkModel[]> {
    const params = new HttpParams().set('caregiverId', caregiverId);
    return this.http.get<CareLinkModel[]>(`${this.apiUrl}/by-caregiver`, { params });
  }

  getPendingRequestsByCaregiver(caregiverId: number): Observable<CareLinkModel[]> {
    const params = new HttpParams().set('caregiverId', caregiverId);
    return this.http.get<CareLinkModel[]>(`${this.apiUrl}/pending/by-caregiver`, { params });
  }

  // Create a new connection between a caregiver and a senior
  createConnection(caregiverId: number, seniorId: number): Observable<CareLinkModel> {
    const params = new HttpParams()
      .set('caregiverId', caregiverId)
      .set('seniorId', seniorId);
      return this.http.post<CareLinkModel>(this.apiUrl, null, { params });
  }

  approveConnection(caregiverId: number, seniorId: number): Observable<CareLinkModel> {
    const params = new HttpParams()
      .set('caregiverId', caregiverId)
      .set('seniorId', seniorId);
    return this.http.post<CareLinkModel>(`${this.apiUrl}/approve`, null, { params });
  }

  // Delete a connection between a caregiver and a senior
  deleteConnection(caregiverId: number, seniorId: number): Observable<void> {
    const params = new HttpParams()
      .set('caregiverId', caregiverId)
      .set('seniorId', seniorId);
    return this.http.delete<void>(this.apiUrl, { params });
  }
}
