import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const sessionRequest = request.clone({
    withCredentials: true,
  });

  return next(sessionRequest);
};
