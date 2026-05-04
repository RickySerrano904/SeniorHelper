import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { EducationComponent } from './components/education/education.component';
import { ModuleComponent } from './components/module/module.component';
import { LessonComponent } from './components/lesson/lesson.component';
import { QuizComponent } from './components/quiz/quiz.component';
import { CarelinkComponent } from './components/carelink/carelink.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import { SettingsComponent } from './components/settings/settings.component';
import { LandingComponent } from './components/landing/landing.component';
import { FaqComponent } from './components/faq/faq.component';
import { guestOnlyGuard, pendingChangesGuard, requireAuthChildGuard } from './components/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent, canActivate: [guestOnlyGuard] },
  { path: 'register', component: RegisterComponent, canDeactivate: [pendingChangesGuard] },
  {
    path: '',
    // Any child in this group requires authentication.
    canActivateChild: [requireAuthChildGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'connections', component: CarelinkComponent },
      { path: 'settings', component: SettingsComponent },
      { path: 'education', component: EducationComponent },
      { path: 'education/:moduleId', component: ModuleComponent },
      { path: 'education/:moduleId/lessons/:lessonId', component: LessonComponent },
      { path: 'education/:moduleId/quiz', component: QuizComponent },
      { path: 'calendar', component: CalendarComponent },
      { path: 'faq', component: FaqComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
