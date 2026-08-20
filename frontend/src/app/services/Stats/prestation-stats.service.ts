import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';


export interface Prestation {
  averageAmount: number;
  prestationName: string;
  prestationId: number;
  totalRevenue: number;
  frequency: number;
}

@Injectable({
  providedIn: 'root'
})
export class PrestationStatsService {

  private apiUrl = 'http://localhost:8090/api';

  constructor(private http: HttpClient) { }

 
  getMostFrequentPrestations(limit: number = 5): Observable<Prestation[]> {
    return this.http.get<Prestation[]>(`${this.apiUrl}/statistics/prestations/most-frequent?limit=${limit}`)
      .pipe(
        catchError(error => {
          console.error('Error fetching prestation data:', error);
          throw error;
        })
      );
  }
}
