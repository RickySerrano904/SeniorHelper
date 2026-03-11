import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lesson } from '../models/module.model';

@Injectable({ providedIn: 'root' })
export class LessonService {
    private readonly apiUrl = 'http://localhost:8080/api/modules';

    constructor(private http: HttpClient) {}

    getLessonById(moduleId: number, lessonId: number): Observable<Lesson> {
        return this.http.get<Lesson>(`${this.apiUrl}/${moduleId}/lessons/${lessonId}`);
    }

    markAsComplete(moduleId: number, lessonId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/${moduleId}/lessons/${lessonId}/complete`, {});
    }
}