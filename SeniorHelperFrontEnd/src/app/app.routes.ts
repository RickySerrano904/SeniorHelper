import { Routes } from '@angular/router';
import { authGuard } from './components/guards/auth.guard';   // <– note the single dot
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { EducationComponent } from './components/education/education.component';
// …other components…

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // public pages
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // everything else is guarded
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
  /*{
    path: 'calendar',
    compontent: CalendarComponent,
    canActivate: [authGuard]
  },
  {
    path: 'connections,'
    component: ConnectionsComponent,
    canActivate: [authGuard]
  },
*/
  // add `canActivate: [AuthGuard]` to any additional routes…

  { path: '**', redirectTo: 'login' }          // optional, will hit guard
];