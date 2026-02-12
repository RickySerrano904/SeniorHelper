import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { EducationComponent } from './components/education/education.component';
import { ModuleComponent } from './components/module/module.component';
import { LessonComponent } from './components/lesson/lesson.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent },
  { path: 'education', component: EducationComponent },
  { path: 'education/:moduleId', component: ModuleComponent },
  { path: 'education/:moduleId/lessons/:lessonId', component: LessonComponent },
  { path: '**', redirectTo: 'login' }
];
