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

        // 1. Mark current lesson as complete, update progress bar
        await firstValueFrom(this.lessonService.markAsComplete(this.moduleId, lessonId));

        // 2. Get the list of lessons to see what is next, if any.
        const lessons = await firstValueFrom(this.lessonService.getLessonsByModule(this.moduleId));
        const currentIndex = lessons.findIndex(lesson => lesson.id === lessonId);
        const nextLesson = lessons[currentIndex + 1];

        if (nextLesson) {
            console.log(`Moving from ${lessonId} to ${nextLesson.id}`);
            this.router.navigate(['/education', this.moduleId, 'lessons', nextLesson.id]);
        } else {
            this.router.navigate(['/education', this.moduleId]);
        }
    }
}