import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);

  profileForm = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    learningGoals: ['', [Validators.maxLength(2000)]],
    experienceLevel: [''],
  });

  passwordForm = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8),
                        Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
  });

  profileMessage = signal('');
  passwordMessage = signal('');
  showDeleteConfirm = signal(false);

  ngOnInit(): void {
    this.userService.getOwnProfile().subscribe(profile => {
      this.profileForm.patchValue({
        fullName: profile.fullName,
        learningGoals: profile.learningGoals ?? '',
        experienceLevel: profile.experienceLevel ?? '',
      });
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;
    this.userService.updateProfile(this.profileForm.getRawValue() as any).subscribe({
      next: () => this.profileMessage.set('Profile updated.'),
      error: () => this.profileMessage.set('Could not update profile.'),
    });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) return;
    this.userService.changePassword(this.passwordForm.getRawValue() as any).subscribe({
      next: () => {
        this.passwordMessage.set('Password changed. Please log in again.');
        this.passwordForm.reset();
        setTimeout(() => { this.authService.logout(); this.router.navigate(['/login']); }, 1500);
      },
      error: (err) => this.passwordMessage.set(err.error?.message ?? 'Current password is incorrect.'),
    });
  }

  confirmDelete(): void {
    this.userService.deleteAccount().subscribe(() => {
      this.authService.logout();
      this.router.navigate(['/']);
    });
  }
}
