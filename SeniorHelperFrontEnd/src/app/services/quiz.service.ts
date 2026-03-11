import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Quiz } from '../models/module.model';


@Injectable({ providedIn: 'root' })
export class QuizService {
    private readonly apiUrl = 'http://localhost:8080/api/modules';

    constructor(private http: HttpClient) {}

    getQuizById(moduleId: number): Observable<Quiz> {
        return this.http.get<Quiz>(`${this.apiUrl}/${moduleId}/quiz`);
    }

    markAsComplete(moduleId: number, quizId: number): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${moduleId}/quiz/${quizId}/complete`, {});
    }
}