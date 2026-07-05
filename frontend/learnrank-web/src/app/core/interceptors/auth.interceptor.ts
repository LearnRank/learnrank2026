import { HttpHandler, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";

import { AuthService } from "../services/auth.service";
import { catchError, throwError } from "rxjs";

export const authInterceptor:HttpInterceptorFn = (req,next) =>
   {
    const authService = inject(AuthService);
    const router = inject(Router);
    const token = authService.getAccessToken();
    const authReq = token ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    }):req;

    return next(authReq).pipe(catchError(err => {
      if (err.status === 401) {
        authService.logout();
        router.navigate(['/login']);
      }return throwError(() => err);
    }));
  };

