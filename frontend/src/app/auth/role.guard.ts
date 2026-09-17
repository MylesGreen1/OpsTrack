import { inject } from '@angular/core';

import {
  CanActivateFn,
  Router
} from '@angular/router';

import {
  AuthService,
  UserRole
} from './auth.service';

export const roleGuard: CanActivateFn = (
  route
) => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);

  if (!authService.hasCredentials()) {

    return router.createUrlTree([
      '/login'
    ]);
  }

  const currentRole =
    authService.getRole();

  const allowedRoles =
    route.data['roles'] as UserRole[];

  if (
    currentRole &&
    allowedRoles.includes(currentRole)
  ) {

    return true;
  }

  return router.createUrlTree([
    '/'
  ]);
};
