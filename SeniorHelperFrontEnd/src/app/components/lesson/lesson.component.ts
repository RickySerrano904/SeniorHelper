import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { LessonService } from '../../services/lesson.service';

@Component ({
    selector: 'app-lesson',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './lesson.component.html',
    styleUrls: ['./lesson.component.css']
})

export class LessonComponent {
    lesson$;

    constructor(private route: ActivatedRoute, private lessonService: LessonService) {
        const moduleId = this.route.snapshot.paramMap.get('moduleId');
        const lessonId = this.route.snapshot.paramMap.get('lessonId');

        this.lesson$ = this.lessonService.getLessonById(Number(moduleId), Number(lessonId));
    }
}