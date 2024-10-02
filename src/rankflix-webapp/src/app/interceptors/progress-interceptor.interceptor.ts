import { HttpInterceptorFn } from '@angular/common/http';
import NProgress from 'nprogress';
import { finalize } from 'rxjs';

export const progressInterceptorInterceptor: HttpInterceptorFn = (
  req,
  next
) => {
  NProgress.start();
  return next(req).pipe(finalize(() => NProgress.done()));
};
