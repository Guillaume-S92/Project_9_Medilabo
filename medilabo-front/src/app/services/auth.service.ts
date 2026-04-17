import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  username: string;
  roles: string[];
}

interface StoredAuth {
  accessToken: string;
  username: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly storageKey = 'medilabo_auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<AuthResponse> {
    const body: LoginRequest = { username, password };

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, body).pipe(
      tap((response) => {
        const auth: StoredAuth = {
          accessToken: response.accessToken,
          username: response.username,
          roles: response.roles
        };

        localStorage.setItem(this.storageKey, JSON.stringify(auth));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
  }

  getToken(): string | null {
    return this.getStoredAuth()?.accessToken ?? null;
  }

  getUsername(): string | null {
    return this.getStoredAuth()?.username ?? null;
  }

  getRoles(): string[] {
    return this.getStoredAuth()?.roles ?? [];
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private getStoredAuth(): StoredAuth | null {
    const raw = localStorage.getItem(this.storageKey);

    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as StoredAuth;
    } catch {
      return null;
    }
  }
}
