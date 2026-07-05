import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  accessToken: string; refreshToken: string; expiresIn: number;
  userId: number; fullName: string; role: string;
}

export interface UserResponse {
  id: string;
  fullName: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private readonly baseUrl = '/api/v1/auth';
  private accessTokenSig = signal<string | null>(null);
  private currentUserSig = signal<{ id: number; fullName: string; role: string } | null>(null);
  readonly currentUser = computed(() => this.currentUserSig());
  readonly isLoggedIn = computed(() => !!this.accessTokenSig() != null);

  constructor(private http: HttpClient) { 
  }

  register(request:RegisterRequest):Observable<UserResponse>{
    return this.http.post<UserResponse>(`${this.baseUrl}/register`, request);
  }

 login(request: LoginRequest): Observable<AuthResponse> {
  return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
    tap(res => {
      this.accessTokenSig.set(res.accessToken);
      this.currentUserSig.set({ id: res.userId, fullName: res.fullName, role: res.role });
    })
  );
}

  logout():void{
    this.accessTokenSig.set(null);
    this.currentUserSig.set(null);
  }

  getAccessToken():string | null{
    return this.accessTokenSig();
  }
}
