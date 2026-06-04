import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LoginRequest } from '../models/login-request.model';
import { LoginResponse } from '../models/login-response.model';
import { RegisterRequest } from '../models/register-request.model';
import { RegisterResponse } from '../models/register-response.model';

export interface UserProfileResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface UpdateProfileRequest {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'auth_token';
  private readonly usernameKey = 'auth_username';
  private readonly usersApiUrl = 'http://localhost:8080/api/users';

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

  getMyProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.usersApiUrl}/me`);
  }

  getSeniors(): Observable<UserProfileResponse[]> {
    return this.http.get<UserProfileResponse[]>(`${this.usersApiUrl}/seniors`);
  }

  updateMyProfile(id: number, request: UpdateProfileRequest): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.usersApiUrl}/${id}`, request);
  }

  getMyFirstName(): Observable<string | null> {
    return this.getMyProfile().pipe(
      map((profile) => profile.firstName?.trim() || null)
    );
  }

  // Convenience helper used by login/register to persist both auth fields together.
  persistSession(token: string, username: string, remember: boolean): void {
    this.saveToken(token, remember);
    this.saveUsername(username, remember);
  }

  saveToken(token: string, remember: boolean) {
    if (!token) return;
    const local = this.getLocalStorage();
    const session = this.getSessionStorage();

    if (remember) {
      local?.setItem(this.tokenKey, token);
      session?.removeItem(this.tokenKey);
    } else {
      session?.setItem(this.tokenKey, token);
      local?.removeItem(this.tokenKey);
    }
  }

  saveUsername(username: string, remember: boolean) {
    if (!username) return;
    const local = this.getLocalStorage();
    const session = this.getSessionStorage();

    if (remember) {
      local?.setItem(this.usernameKey, username);
      session?.removeItem(this.usernameKey);
    } else {
      session?.setItem(this.usernameKey, username);
      local?.removeItem(this.usernameKey);
    }
  }

  getToken(): string | null {
    return this.getLocalStorage()?.getItem(this.tokenKey) ?? this.getSessionStorage()?.getItem(this.tokenKey) ?? null;
  }

  getValidToken(): string | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    if (this.isTokenExpired(token)) {
      this.clearSession();
      return null;
    }

    return token;
  }

  getUsername(): string | null {
    return this.getLocalStorage()?.getItem(this.usernameKey) ?? this.getSessionStorage()?.getItem(this.usernameKey) ?? null;
  }

  clearSession(): void {
    const local = this.getLocalStorage();
    const session = this.getSessionStorage();

    local?.removeItem(this.tokenKey);
    session?.removeItem(this.tokenKey);
    local?.removeItem(this.usernameKey);
    session?.removeItem(this.usernameKey);
  }

  clearToken(): void {
    this.clearSession();
  }

  // Route guards use this to decide access quickly.
  isAuthenticated(): boolean {
    return Boolean(this.getValidToken());
  }

  private isTokenExpired(token: string): boolean {
    const expiresAtMs = this.getTokenExpiryMs(token);
    if (!expiresAtMs) {
      return true;
    }

    return Date.now() >= expiresAtMs;
  }

  private getTokenExpiryMs(token: string): number | null {
    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    try {
      const payloadJson = this.decodeBase64Url(parts[1]);
      const payload = JSON.parse(payloadJson) as { exp?: unknown };

      if (typeof payload.exp !== 'number') {
        return null;
      }

      return payload.exp * 1000;
    } catch {
      return null;
    }
  }

  private decodeBase64Url(value: string): string {
    const base64 = value.replace(/-/g, '+').replace(/_/g, '/');
    const remainder = base64.length % 4;
    const padding = remainder === 0 ? '' : '='.repeat(4 - remainder);
    return atob(base64 + padding);
  }

  private getLocalStorage(): Storage | null {
    return this.getStorage('localStorage');
  }

  private getSessionStorage(): Storage | null {
    return this.getStorage('sessionStorage');
  }

  private getStorage(type: 'localStorage' | 'sessionStorage'): Storage | null {
    try {
      const storage = globalThis[type];

      if (
        storage &&
        typeof storage.getItem === 'function' &&
        typeof storage.setItem === 'function' &&
        typeof storage.removeItem === 'function'
      ) {
        return storage;
      }
    } catch {
      return null;
    }

    return null;
  }
}
