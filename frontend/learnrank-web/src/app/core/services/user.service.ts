import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface UserProfileResponse {
  id: number; fullName: string; email: string; role: string;
  experienceLevel: string | null; learningGoals: string | null; createdAt: string;
}
export interface UpdateProfileRequest { fullName: string; learningGoals: string; experienceLevel: string; }
export interface ChangePasswordRequest { currentPassword: string; newPassword: string; }

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private baseUrl = '/api/v1/users';

  profile = signal<UserProfileResponse | null>(null);

  getOwnProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.baseUrl}/me`).pipe(
      tap(res => this.profile.set(res))
    );
  }

  updateProfile(request: UpdateProfileRequest): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.baseUrl}/me`, request).pipe(
      tap(res => this.profile.set(res))
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/me/password`, request);
  }

  deleteAccount(): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/me`);
  }
}
