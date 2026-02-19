import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, CanDeactivateFn, Router, Routes } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { EducationComponent } from './components/education/education.component';
import { ModuleComponent } from './components/module/module.component';
import { LessonComponent } from './components/lesson/lesson.component';
import { QuizComponent } from './components/quiz/quiz.component';
import { AuthService } from './services/auth.service';

type DeactivatableComponent = {
  canDeactivate?: () => boolean | Observable<boolean> | Promise<boolean>;
};

// Protect child routes under the private route group.
const requireAuthChild: CanActivateChildFn = (_route, state) => {
  const authService = inject(AuthService);
  if (authService.isAuthenticated()) {
    return true;
  }

  return inject(Router).createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};

// Keep authenticated users from navigating back to the login screen.
const guestOnlyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  return authService.isAuthenticated() ? inject(Router).createUrlTree(['/home']) : true;
};

// If a component exposes canDeactivate(), defer navigation decision to it.
const pendingChangesGuard: CanDeactivateFn<DeactivatableComponent> = (component) => {
  if (!component || typeof component.canDeactivate !== 'function') {
    return true;
  }
  return component.canDeactivate();
};

export const routes: Routes = [
  // Entry route resolves through the protected home path.
  { path: '', pathMatch: 'full', redirectTo: 'home' },
  { path: 'login', component: LoginComponent, canActivate: [guestOnlyGuard] },
  { path: 'register', component: RegisterComponent, canDeactivate: [pendingChangesGuard] },
  {
    path: '',
    // Any child in this group requires authentication.
    canActivateChild: [requireAuthChild],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'education', component: EducationComponent },
      { path: 'education/:moduleId', component: ModuleComponent },
      { path: 'education/:moduleId/lessons/:lessonId', component: LessonComponent },
      { path: 'education/:moduleId/quiz', component: QuizComponent }
    ]
  },
  // Unknown URLs flow through home and then auth guard logic.
  { path: '**', redirectTo: 'home' }
];
