import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./home/home.component/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./auth/login.component/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./auth/register.component/register.component').then(m => m.RegisterComponent) },
  { path: 'profile', canActivate: [authGuard],
  loadComponent: () => import('./profile/profile.component/profile.component').then(m => m.ProfileComponent) },

];
