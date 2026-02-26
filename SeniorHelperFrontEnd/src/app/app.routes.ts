import { Routes } from '@angular/router';
import { authGuard } from './components/guards/auth.guard';   // <– note the single dot
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { EducationComponent } from './components/education/education.component';
import { ModuleComponent } from './components/module/module.component';
import { LessonComponent } from './components/lesson/lesson.component';
import { QuizComponent } from './components/quiz/quiz.component';
import { CarelinkComponent } from './components/carelink/carelink.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // public pages
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard]
  },
  {
    path: 'education',
    component: EducationComponent,
    canActivate: [authGuard]
  },

  { path: '**', redirectTo: 'login' }          // optional, will hit guard
];
  { path: 'home', component: HomeComponent },
  { path: 'education', component: EducationComponent },
  { path: 'education/:moduleId', component: ModuleComponent },
  { path: 'education/:moduleId/lessons/:lessonId', component: LessonComponent },
  { path: 'education/:moduleId/quiz', component: QuizComponent},
  { path: 'connections', component: CarelinkComponent },
  { path: '**', redirectTo: 'login' }
];
