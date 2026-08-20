import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface FraudAlert {
  id: number | null;
  alertType: string;
  description: string;
  reimbursementId: number;
  detectedAt: string;
  status: string;
  resolvedBy: string | null;
  resolvedAt: string | null;
  resolutionNotes: string | null;
}

export interface EnhancedFraudCheckResult {
  remboursementId: number;
  adherantName: string;
  doctorName: string;
  movementDate: string;
  movementDescription: string;
  amount: number;
  alerts: FraudAlert[];
}

export interface FraudStats {
  totalReimbursements: number;
  totalFraudulentReimbursements: number;
  fraudPercentage: number;
  fraudByDoctor: Record<string, number>;
  fraudByAdherant: Record<string, number>;
}

@Injectable({
  providedIn: 'root'
})
export class ReimbursementFraudService {
  private apiUrl = 'http://localhost:8090/api/fraud';

  constructor(private http: HttpClient) { }

  checkAll(): Observable<EnhancedFraudCheckResult[]> {
    let params = new HttpParams();
    params = params.set('onlyFraudulent', 'true');
    
    return this.http.get<EnhancedFraudCheckResult[]>(`${this.apiUrl}/check-all`, { params });
  }

  checkDateRange(startDate: Date, endDate: Date): Observable<EnhancedFraudCheckResult[]> {
    // Format dates to ISO format (YYYY-MM-DD)
    const start = startDate.toISOString().split('T')[0];
    const end = endDate.toISOString().split('T')[0];
    
    let params = new HttpParams()
      .set('startDate', start)
      .set('endDate', end);
      
    return this.http.get<EnhancedFraudCheckResult[]>(`${this.apiUrl}/check-date-range`, { params });
  }

  checkSpecificReimbursement(id: number): Observable<EnhancedFraudCheckResult> {
    return this.http.get<EnhancedFraudCheckResult>(`${this.apiUrl}/check/${id}`);
  }

  checkBatch(ids: number[]): Observable<EnhancedFraudCheckResult[]> {
    return this.http.post<EnhancedFraudCheckResult[]>(`${this.apiUrl}/check-batch`, ids);
  }

  getFraudStats(): Observable<FraudStats> {
    return this.http.get<FraudStats>(`${this.apiUrl}/stats`);
  }
}

