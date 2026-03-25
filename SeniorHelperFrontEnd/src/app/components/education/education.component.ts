import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Observable, map } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-education',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './education.component.html',
  styleUrls: ['./education.component.css']
})

export class EducationComponent {
  modules$: Observable<any[]>;

  constructor(private http: HttpClient) {
    this.modules$ = this.http.get<any>('http://localhost:8080/api/progress').pipe(
      map(res => res.modules.map((m: any) => ({
        id: m.id, 
        title: m.title,
        description: m.description,
        lessons: m.lessons,
        quiz: m.quiz
      })))
    );
  }

  getButtonProgress(module: any): string {
    const completedCount = module.lessons?.filter((l: any) => l.completed).length || 0;
    const totalCount = module.lessons?.length || 0;

    if (completedCount === 0) return 'Start Learning';
    if (completedCount === totalCount) return 'Review Module';
    return 'Continue Learning';
  }
}