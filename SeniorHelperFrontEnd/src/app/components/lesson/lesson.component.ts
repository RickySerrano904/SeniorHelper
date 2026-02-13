import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { LessonService } from '../../services/lesson.service';
import { Lesson } from '../../models/module.model';
import { firstValueFrom, Observable } from 'rxjs';

@Component ({
    selector: 'app-lesson',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './lesson.component.html',
    styleUrls: ['./lesson.component.css']
})

export class LessonComponent {
    lesson$: Observable<Lesson>;
    moduleId: number;

    constructor(private route: ActivatedRoute, private lessonService: LessonService, private router: Router) {
        this.moduleId = Number(this.route.snapshot.paramMap.get('moduleId'));
        const lessonId = Number(this.route.snapshot.paramMap.get('lessonId'));
        this.lesson$ = this.lessonService.getLessonById(this.moduleId, lessonId);
    }

    async markAsComplete() {
        const lessonId = Number(this.route.snapshot.paramMap.get('lessonId'));
        await firstValueFrom(this.lessonService.markAsComplete(this.moduleId, lessonId));
        this.router.navigate(['/education', this.moduleId]);
    }
}