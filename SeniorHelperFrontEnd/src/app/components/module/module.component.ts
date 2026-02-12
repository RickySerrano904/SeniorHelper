import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ModuleService } from '../../services/module.service';
import { Module } from '../../models/module.model';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-module', 
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './module.component.html',
    styleUrls: ['./module.component.css']
})
export class ModuleComponent {
    module$: Observable<Module>;

    constructor(private route: ActivatedRoute, private moduleService: ModuleService) {

        const id = this.route.snapshot.paramMap.get('moduleId');
        this.module$ = this.moduleService.getModuleById(Number(id));
    }

    calculateProgress(lessons: { completed?: boolean }[] = []): number {
        if (!lessons.length) return 0;
        const completed = lessons.filter(l => l.completed).length;
        return (completed / lessons.length) * 100;
    }
}