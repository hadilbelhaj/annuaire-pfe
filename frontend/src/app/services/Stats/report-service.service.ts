import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, catchError, Observable, switchMap, tap, throwError } from 'rxjs';

export enum PeriodType {
  MONTH = 'MONTH',
  QUARTER = 'QUARTER',
  YEAR = 'YEAR'
}

export interface TransactionStatistics {
  id?: number;
  period: string; 
  periodType: PeriodType;
  totalMonetaryValue: number;
  averageTransactionAmount: number;
  highestActivityRegion: string;
  lowestActivityRegion: string;
  
  topProfessionalByPatientCount: number;
  topProfessionalByPatientCountName: string;
  topProfessionalByTransactionAmount: number;
  topProfessionalByTransactionAmountName: string;
  topProfessionalByAverageValue: number;
  topProfessionalByAverageValueName: string;
  
  monthOverMonthGrowthPercentage: number;
  
  specialtyTransactionVolumes: string;
  
  generatedAt: string; 
}
@Injectable({
  providedIn: 'root'
})

export class ReportServiceService {
  private apiUrl = 'http://localhost:8090/api/statistics';
  
  private reportGenerationStatusSubject = new BehaviorSubject<boolean>(false);
  reportGenerationStatus$ = this.reportGenerationStatusSubject.asObservable();
  
  // Store the last generated report data
  private currentReportData: TransactionStatistics | null = null;

  constructor(private http: HttpClient) { }

  generateStatistics(category: string, params: any): Observable<TransactionStatistics> {
    this.reportGenerationStatusSubject.next(true);
    
    let endpoint = '';
    
    if (category === 'Monthly Statistics') {
      endpoint = `${this.apiUrl}/generate/month/${params.year}/${params.month}`;
    } else if (category === 'Quarterly Statistics') {
      endpoint = `${this.apiUrl}/generate/quarter/${params.year}/${params.quarter}`;
    } else if (category === 'Yearly Statistics') {
      endpoint = `${this.apiUrl}/generate/year/${params.year}`;
    } else {
      return throwError(() => new Error('Invalid category'));
    }
    
    return this.http.get<TransactionStatistics>(endpoint).pipe(
      tap(data => {
        this.currentReportData = data;
        this.reportGenerationStatusSubject.next(false);
      }),
      catchError(error => {
        this.reportGenerationStatusSubject.next(false);
        return throwError(() => error);
      })
    );
  }

  getReport(category: string, params: any): Observable<Blob> {
    let endpoint = '';
    
    if (category === 'Monthly Statistics') {
      endpoint = `${this.apiUrl}/report/month/${params.year}/${params.month}`;
    } else if (category === 'Quarterly Statistics') {
      endpoint = `${this.apiUrl}/report/quarter/${params.year}/${params.quarter}`;
    } else if (category === 'Yearly Statistics') {
      endpoint = `${this.apiUrl}/report/year/${params.year}`;
    } else {
      return throwError(() => new Error('Invalid category'));
    }
    
    const headers = new HttpHeaders({
      'Accept': 'application/pdf'
    });
    
    return this.http.request('GET', endpoint, {
      headers: headers,
      responseType: 'blob',
    }).pipe(
      catchError(error => throwError(() => error))
    );
  }

  generateAndGetReport(category: string, params: any, reportType: string): Observable<Blob> {
    return this.generateStatistics(category, params).pipe(
      tap(() => console.log('Statistics generated successfully, fetching report...')),
      catchError(error => {
        console.error('Error generating statistics:', error);
        return throwError(() => error);
      }),
      switchMap(() => this.getReport(category, params))
    );
  }
  getCurrentReportData(): TransactionStatistics | null {
    return this.currentReportData;
  }

  checkReportExists(periodType: string, period: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/exists/${periodType}/${period}`);
  }

  public getReportAndOpenInNewTab(category: string, params: any, reportType: string): Promise<void> {
    return new Promise((resolve, reject) => {
      this.getReport(category, params).subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          let filename = '';
          if (category === 'Monthly Statistics') {
            filename = `${reportType}-report-${params.year}-${params.month}.pdf`;
          } else if (category === 'Quarterly Statistics') {
            filename = `${reportType}-report-${params.year}-Q${params.quarter}.pdf`;
          } else if (category === 'Yearly Statistics') {
            filename = `${reportType}-report-${params.year}.pdf`;
          }
          
          // Open the PDF in a new tab
          const newWindow = window.open(url, '_blank');
          if (!newWindow) {
            console.error('Pop-up blocked. Please allow pop-ups for this site to view the report.');
            
            // Alternative: Create a download link
            const link = document.createElement('a');
            link.href = url;
            link.download = filename;
            link.click();
          }
          
          // Clean up the URL object after the window has been opened
          setTimeout(() => {
            window.URL.revokeObjectURL(url);
            resolve(); 
          }, 100);
        },
        error: (error) => {
          console.error('Error getting report:', error);
          alert('Failed to retrieve the report. Please try again later.');
          reject(error); // Reject the promise on error
        }
      });
    });
  }
}
