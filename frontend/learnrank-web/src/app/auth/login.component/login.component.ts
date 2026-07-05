import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
   styleUrl: './login.component.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });
  errorMessage = '';

  constructor() {}

  submit(): void {
    if (this.form.invalid) return;
    this.authService.login(this.form.getRawValue() as any).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.errorMessage = 'Invalid email or password.',
    });
  }
}
