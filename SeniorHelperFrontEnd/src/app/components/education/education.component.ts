import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Module } from '../../models/module.model';
import { ModuleService } from '../../services/module.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-education',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './education.component.html',
  styleUrls: ['./education.component.css']
})
export class EducationComponent {
  modules$;
  
  constructor(private moduleService: ModuleService) {
    this.modules$ = this.moduleService.getAllModules();
  }
}