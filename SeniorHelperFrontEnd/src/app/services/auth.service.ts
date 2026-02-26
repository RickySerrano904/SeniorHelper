import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { RegisterRequest } from '../models/register-request.model';
import { RegisterResponse } from '../models/register-response.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.tokenKey);
   ;
  }
  private readonly tokenKey = 'auth_token';
  private readonly usernameKey = 'auth_username';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('http://localhost:8080/api/auth/login', request);
  }

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>('http://localhost:8080/api/auth/register', request);
  }

  logout(): Observable<void> {
    return this.http.post<void>('http://localhost:8080/api/auth/logout', {});
  }

  // Convenience helper used by login/register to persist both auth fields together.
  persistSession(token: string, username: string, remember: boolean): void {
    this.saveToken(token, remember);
    this.saveUsername(username, remember);
  }

  saveToken(token: string, remember: boolean) {
    if (!token) return;
    if (remember) {
      localStorage.setItem(this.tokenKey, token);
      sessionStorage.removeItem(this.tokenKey);
    } else {
      sessionStorage.setItem(this.tokenKey, token);
      localStorage.removeItem(this.tokenKey);
    }
  }

  saveUsername(username: string, remember: boolean) {
    if (!username) return;
    if (remember) {
      localStorage.setItem(this.usernameKey, username);
      sessionStorage.removeItem(this.usernameKey);
    } else {
      sessionStorage.setItem(this.usernameKey, username);
      localStorage.removeItem(this.usernameKey);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey) ?? sessionStorage.getItem(this.tokenKey);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.usernameKey) ?? sessionStorage.getItem(this.usernameKey);
  }

  clearSession(): void {
    localStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usernameKey);
    sessionStorage.removeItem(this.usernameKey);
  }

  // Route guards use this to decide access quickly.
  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }
}
