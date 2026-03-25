import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { Quiz } from '../../models/module.model';
import { firstValueFrom, Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-quiz',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './quiz.component.html',
    styleUrls: ['./quiz.component.css']
})

export class QuizComponent {
    quiz$: Observable<Quiz>;
    moduleId: number;

    selectedAnswers: { [key: number]: number } = {};

    constructor(
        private route: ActivatedRoute, 
        private quizService: QuizService, 
        private router: Router) {

        this.moduleId = Number(this.route.snapshot.paramMap.get('moduleId'));
        this.quiz$ = this.quizService.getQuizById(this.moduleId);
    }

    async submitQuiz() {
        console.log('Submitting quiz for moduleId:', this.moduleId);

        try {
            const quiz = await firstValueFrom(this.quiz$);

            if (quiz && quiz.id) {
                await firstValueFrom(this.quizService.markAsComplete(this.moduleId, quiz.id, this.selectedAnswers));
                console.log('Quiz marked as complete for quizId:', quiz.id);
            }

            this.router.navigate(['/education', this.moduleId]);
            
        } catch (error) {
            console.error('Error submitting quiz:', error);
        }
    }

}