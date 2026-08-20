import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface CollusionThresholds {
  minMovementsForSuspicion: number;
  claimsPerMonthThreshold: number;
  percentDaysWithMultipleClaimsThreshold: number;
  mostFrequentActePSPercentThreshold: number;
}
export interface CollusionIndicators {
  claimsPerMonth: number;
  daysWithMultipleClaims: number;
  percentDaysWithMultipleClaims: number;
  mostFrequentActePSCount: number;
  mostFrequentActePSId: number;
  mostFrequentActePSName: string;
  mostFrequentActePSPercent: number;
  totalAmountClaimed: number;
  averageAmountPerClaim: number;
  suspicious: boolean;
}
export interface DoctorAdherentPair {
  doctorId: number;
  adherentId: number;
}
export interface CollusionFlag {
  pair: DoctorAdherentPair;
  doctorName: string;
  adherentName: string;
  indicators: CollusionIndicators;
  riskScore: number;
}

@Injectable({
  providedIn: 'root'
})
export class PsfraudService {

  private apiUrl = 'http://localhost:8090/api/fraud';

  constructor(private http: HttpClient) { }

  // Basic detection with default thresholds
  detectCollusion(): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(`${this.apiUrl}/collusion/detect`);
  }

  // Detection with custom thresholds
  detectCollusionWithCustomThresholds(thresholds: CollusionThresholds): Observable<CollusionFlag[]> {
    return this.http.post<CollusionFlag[]>(`${this.apiUrl}/collusion/detect-custom`, thresholds);
  }

  // Detection for a specific healthcare professional
  detectCollusionByProfessional(professionalId: number): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(
      `${this.apiUrl}/collusion/detect-by-professional`,
      { params: new HttpParams().set('professionalId', professionalId.toString()) }
    );
  }
  detectCollusionByProfessionalName(professionalName: string): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(
      `${this.apiUrl}/collusion/detect-by-professional-name`,
      { params: new HttpParams().set('professionalName', professionalName) }
    );
  }

  // Detection for a specific adherent
  detectCollusionByAdherent(adherentId: number): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(
      `${this.apiUrl}/collusion/detect-by-adherent`,
      { params: new HttpParams().set('adherentId', adherentId.toString()) }
    );
  }

  // Detection for a specific date range
  detectCollusionByDateRange(startDate: string, endDate: string): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(
      `${this.apiUrl}/collusion/detect-by-date`,
      { 
        params: new HttpParams()
          .set('startDate', startDate)
          .set('endDate', endDate)
      }
    );
  }
  detectCollusionByAdherentName(firstName: string, lastName: string): Observable<CollusionFlag[]> {
    return this.http.get<CollusionFlag[]>(
      `${this.apiUrl}/collusion/detect-by-adherent-name`,
      { params: new HttpParams()
          .set('firstName', firstName)
          .set('lastName', lastName) }
    );
  }
}
