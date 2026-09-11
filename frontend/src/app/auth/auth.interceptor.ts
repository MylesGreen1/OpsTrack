import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {

  const authService = inject(AuthService);

  const authorizationHeader =
    authService.getAuthorizationHeader();

  if (!authorizationHeader) {
    return next(request);
  }

  const authenticatedRequest =
    request.clone({
      setHeaders: {
        Authorization: authorizationHeader
      }
    });

  return next(authenticatedRequest);
};
