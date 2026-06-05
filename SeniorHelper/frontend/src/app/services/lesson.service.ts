import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lesson } from '../models/module.model';
import { apiEndpoint } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class LessonService {
    private readonly apiUrl = apiEndpoint('/modules');

    constructor(private http: HttpClient) {}

    getLessonById(moduleId: number, lessonId: number): Observable<Lesson> {
        return this.http.get<Lesson>(`${this.apiUrl}/${moduleId}/lessons/${lessonId}`);
    }

    getLessonsByModule(moduleId: number): Observable<Lesson[]> {
        return this.http.get<Lesson[]>(`${this.apiUrl}/${moduleId}/lessons`);
    }

    markAsComplete(moduleId: number, lessonId: number): Observable<any> {
        return this.http.post(`${this.apiUrl}/${moduleId}/lessons/${lessonId}/complete`, {});
    }
}
