import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { Quiz } from '../../models/module.model';
import { firstValueFrom, Observable } from 'rxjs';

@Component({
    selector: 'app-quiz',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './quiz.component.html',
    styleUrls: ['./quiz.component.css']
})

export class QuizComponent {
    quiz$: Observable<Quiz>;
    moduleId: number;

    constructor(private route: ActivatedRoute, private quizService: QuizService, private router: Router) {
        this.moduleId = Number(this.route.snapshot.paramMap.get('moduleId'));
        this.quiz$ = this.quizService.getQuizById(this.moduleId);
    }

    async submitQuiz() {
        const quiz = await firstValueFrom(this.quiz$);

        if (quiz.id) {
            await firstValueFrom(this.quizService.markAsComplete(this.moduleId, quiz.id));
            this.router.navigate(['/education', this.moduleId]);
        }
    }

}