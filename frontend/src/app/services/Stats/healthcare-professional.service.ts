// healthcare-professional.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HealthcareProfessionalService {
  private apiUrl = 'http://localhost:8090/api/healthcare-professionals';

  constructor(private http: HttpClient) {}

  getProfessionals(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
  downloadMovementsReport(professionalId: number, year: number) {
    const url = `http://localhost:8090/api/reports/healthcare-professionals/${professionalId}/movements-report?year=${year}`;
    return this.http.get(url, { responseType: 'blob' });
  }
  getProfessionalMovements(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/movements`);
  }

  getProfessionalMovementStats(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/movements/stats`);
  }

  getProfessionalMonthlyStats(id: number, year: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/movements/monthly?year=${year}`);
  }

  getProfessionalAdherentStats(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/movements/adherents`);
  }
}