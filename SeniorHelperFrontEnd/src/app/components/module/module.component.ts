import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ModuleService } from '../../services/module.service';

@Component({
    selector: 'app-module', 
    standalone: true,
    imports: [CommonModule],
    templateUrl: './module.component.html',
    styleUrls: ['./module.component.css']
})
export class ModuleComponent {
    module$;

    constructor(private route: ActivatedRoute, private moduleService: ModuleService) {

        const id = this.route.snapshot.paramMap.get('moduleId');
        this.module$ = this.moduleService.getModuleById(Number(id));
    }
}