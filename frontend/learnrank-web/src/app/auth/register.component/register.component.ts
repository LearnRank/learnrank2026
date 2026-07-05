import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';


@Component({
  selector: 'app-register.component',
 imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8),
                     Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
  });

  constructor() {}

  errorMessage = '';
  submit(): void {
    if (this.form.invalid) return;
    this.authService.register(this.form.getRawValue() as any).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => this.errorMessage = err.error?.message ?? 'Registration failed.',
    });
  }
}

