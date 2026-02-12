import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { EducationComponent } from './components/education/education.component';
import { ModuleComponent } from './components/module/module.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'home', component: HomeComponent },
  { path: 'education', component: EducationComponent },
  { path: 'education/:moduleId', component: ModuleComponent },
  { path: '**', redirectTo: 'login' }
];
