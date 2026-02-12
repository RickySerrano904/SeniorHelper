import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';

@Component({
    selector: 'app-quiz',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './quiz.component.html',
    styleUrls: ['./quiz.component.css']
})

export class QuizComponent {
    quiz$;

    constructor(private route: ActivatedRoute, private quizService: QuizService) {
        const moduleId = this.route.snapshot.paramMap.get('moduleId');
        this.quiz$ = this.quizService.getQuizById(Number(moduleId));
    }
}