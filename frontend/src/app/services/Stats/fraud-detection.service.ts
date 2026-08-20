import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
export interface MedicalClaim {
  amount: number;
  specialtyAverageAmount: number;
  medicalSpecialty: string;
  designation: string;
  reimbursementPercentage: number;
}

export interface CheckFraudResponse {
  isFraudulent: boolean;
  claim: MedicalClaim;
}

export interface AnalysisResponse {
  actualPercentage: number;
  isFraudulent: boolean;
  predictedPercentage: number;
  threshold: number;
}
export interface ClaimDetail {
  date: string;
  insuranceAmount: number;
  amount: number;
  formattedDate: string;
  claimId: number;
}

export interface ExceededAdherent {
  deductible: number;
  name: string;
  totalInsurancePaid: number;
  id: number;
  region: string;
  claimsCausingExcess: ClaimDetail[];
  excessAmount: number;
}
export interface MovementDTO {
  id: number;
  date: string;
  amount: number;
  description: string;
  adherentId?: number;
  adherentName?: string;
  healthcareProfessionalName?: string;
  actePSName?: string;
  healthcareProfessional: {
    id: number;
    name: string;
    medicalSpecialty: string;
  };
  actePS: {
    prestation: {
      prestation_libelle: string;
    }
  };
}

export interface FraudCheckResponse {
  movementId: number;
  fraudulent: boolean;
  reason: string;
}


@Injectable({
  providedIn: 'root'
})
export class FraudDetectionService {
  private apiUrl = 'http://localhost:8090/api/claims';

  constructor(private http: HttpClient) { }

  getFraudList(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/fraud-list`).pipe(
      catchError(this.handleError)
    );
  }
  getDoctorMovements(doctorName: string): Observable<MovementDTO[]> {
    return this.http.get<MovementDTO[]>(`http://localhost:8090/api/ps/movements/name/${encodeURIComponent(doctorName)}`);
  }
  checkPrestationFraud(movement: MovementDTO): Observable<FraudCheckResponse> {
    return this.http.post<FraudCheckResponse>(`http://localhost:8090/api/fraud-prestation/check`, movement);
  }
  checkAllMovements(): Observable<MovementDTO[]> {
    return this.http.get<MovementDTO[]>(`http://localhost:8090/api/fraud-prestation/check-all`);
  }
  getFraudulentMovementsByDoctor(doctorName: string): Observable<MovementDTO[]> {
    return this.http.get<MovementDTO[]>(`http://localhost:8090/api/fraud-prestation/check-by-doctor/${encodeURIComponent(doctorName)}`).pipe(
      catchError(this.handleError)
    );
  }


  checkFraud(claim: MedicalClaim): Observable<CheckFraudResponse> {
    return this.http.post<CheckFraudResponse>(`${this.apiUrl}/check-fraud`, claim).pipe(
      catchError(this.handleError)
    );
  }

  analyzeClaimDetails(claim: MedicalClaim): Observable<any> {
    return this.http.post<AnalysisResponse>(`${this.apiUrl}/analyze`, claim).pipe(
      catchError(this.handleError)
    );
  }
  getExceededDeductibles(): Observable<ExceededAdherent[]> {
    return this.http.get<ExceededAdherent[]>('http://localhost:8090/api/deductible/exceeded');
  }

  // Error handling
  private handleError(error: any) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}