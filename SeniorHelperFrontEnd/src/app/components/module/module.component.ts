import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ModuleService } from '../../services/module.service';
import { Module } from '../../models/module.model';
import { Observable, switchMap, forkJoin, map } from 'rxjs';

@Component({
    selector: 'app-module', 
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './module.component.html',
    styleUrls: ['./module.component.css']
})

export class ModuleComponent {
    module$: Observable<Module>;

    constructor(
        private route: ActivatedRoute, 
        private moduleService: ModuleService,
        private http: HttpClient,
        private router: Router
    ) {
        // Force a fresh fetch every time you navigate back to this page
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;

        this.module$ = this.route.paramMap.pipe(
            switchMap(params => {
                const id = Number(params.get('moduleId'));
                
                // Fetch both the Module details AND the Progress data
                return forkJoin({
                    module: this.moduleService.getModuleById(id),
                    progress: this.http.get<any>('http://localhost:8080/api/progress')
                }).pipe(
                    map(({ module, progress }) => {
                        
                        // Find the specific module inside the progress report
                        const moduleProgress = progress.modules.find((m: any) => m.moduleId === id);

                        // Merge the 'completed' status into our module object
                        if (moduleProgress) {
                            module.lessons = module.lessons.map(lesson => {
                                const progLesson = moduleProgress.lessons.find((pl: any) => pl.id === lesson.id);
                                return {
                                    ...lesson,
                                    completed: progLesson ? progLesson.completed : false
                                };
                            });

                            if (module.quiz && moduleProgress.quiz) {
                                module.quiz.completed = moduleProgress.quiz.completed;
                            }
                        }

                        return module;
                    })
                );
            })
        );
    }

    calculateProgress(lessons: { completed?: boolean }[] = []): number {
        if (!lessons || !lessons.length) return 0;
        const completed = lessons.filter(l => l.completed).length;
        return (completed / lessons.length) * 100;
    }
}