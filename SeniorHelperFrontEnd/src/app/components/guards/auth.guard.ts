import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, CanDeactivateFn, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';

export type DeactivatableComponent = {
  canDeactivate?: () => boolean | Observable<boolean> | Promise<boolean>;
};

export const requireAuthChildGuard: CanActivateChildFn = () => {
  const authService = inject(AuthService);
  if (authService.isAuthenticated()) {
    return true;
  }

  return inject(Router).createUrlTree(['/login']);
};

export const guestOnlyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  return authService.isAuthenticated() ? inject(Router).createUrlTree(['/home']) : true;
};

export const pendingChangesGuard: CanDeactivateFn<DeactivatableComponent> = (component) => {
  if (!component || typeof component.canDeactivate !== 'function') {
    return true;
  }

  return component.canDeactivate();
};
