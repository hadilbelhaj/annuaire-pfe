import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HealthcareProfessionalStats,SpecialtyDistribution, 
  RegionDistribution, 
  TopProfessional  } from '../../models/Stats/healthcare-professional-stats.model';
@Injectable({
  providedIn: 'root'
})
export class HealthcareprofessionalstatsService {

  private apiUrl = 'http://localhost:8090/api/stats/professionals';

  constructor(private http: HttpClient) { }

  getProfessionalStats(): Observable<HealthcareProfessionalStats> {
    return this.http.get<HealthcareProfessionalStats>(this.apiUrl);
  }
  

  getSpecialtyDistribution(): Observable<SpecialtyDistribution[]> {
    return this.http.get<SpecialtyDistribution[]>(`${this.apiUrl}/specialty`);
  }

  getRegionDistribution(): Observable<RegionDistribution[]> {
    return this.http.get<RegionDistribution[]>(`${this.apiUrl}/region`);
  }

  getTopProfessionals(): Observable<{
    topByVisits: TopProfessional[],
    topByTransactions: TopProfessional[],
    topByAverage: TopProfessional[]
  }> {
    return this.http.get<{
      topByVisits: TopProfessional[],
      topByTransactions: TopProfessional[],
      topByAverage: TopProfessional[]
    }>(`${this.apiUrl}/top`);
  }

  // New method for manually triggering stats calculation
  triggerStatsCalculation(): Observable<string> {
    return this.http.post<string>('http://localhost:8090/api/stats/calculation/trigger', {});
  }

  // New method to get calculation status
  getCalculationStatus(): Observable<{
    lastCalculatedAt?: Date;
    status: string;
  }> {
    return this.http.get<{
      lastCalculatedAt?: Date;
      status: string;
    }>('http://localhost:8090/api/stats/calculation/status');
  }
  
}
