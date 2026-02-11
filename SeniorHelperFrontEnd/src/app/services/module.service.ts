import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Module } from '../models/module.model';

@Injectable({ providedIn: 'root' })
export class ModuleService {
    private readonly apiUrl = 'http://localhost:8080/api/modules';

    constructor(private http: HttpClient) {}

    getAllModules(): Observable<Module[]> {
        return this.http.get<Module[]>(this.apiUrl);
    }

    getModuleById(id: number): Observable<Module> {
        return this.http.get<Module>(`${this.apiUrl}/${id}`);
    }
}