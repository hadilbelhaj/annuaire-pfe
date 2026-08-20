import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
interface PasswordResetRequest {
  email: string;
}

interface PasswordResetCompleteRequest {
  token: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

  private apiUrl = 'http://localhost:8090/api/password';

  constructor(private http: HttpClient) { }

  forgotPassword(email: string): Observable<string> {
    const request: PasswordResetRequest = { email };
    return this.http.post(`${this.apiUrl}/forgot`, request, { responseType: 'text' });
  }
  

  
  resetPassword(token: string, newPassword: string): Observable<String> {
    const request: PasswordResetCompleteRequest = { token, newPassword };
    return this.http.post(`${this.apiUrl}/reset`, request,{ responseType: 'text' });
  }

 
  validateToken(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/validate-token/${token}`);
  }
}
