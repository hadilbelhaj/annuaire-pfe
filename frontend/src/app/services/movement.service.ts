import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Movement } from '../models/movement.modal';
import { Page } from '../models/page.modal';

@Injectable({
  providedIn: 'root',
})
export class MovementService {
  private apiUrl = 'http://localhost:8090/api/movements';
  constructor(private http: HttpClient) {}
  getAll() {
    return this.http.get<Movement[]>(this.apiUrl);
  }
  getPage(page: number, size: number): Observable<Page<Movement>> {
    return this.http.get<Page<Movement>>(this.apiUrl + '/' + page + '/' + size);
  }

  getGroupedByHealthcareProfessional(
    page: number,
    size: number,
    search: string,
    sort: string
  ): Observable<any> {
    let params = new HttpParams().set('search', search).set('sort', sort);

    return this.http.get(`${this.apiUrl}/groupbyps/${page}/${size}`, {
      params,
    });
  }

  getGroupedByAdherant(
    page: number,
    size: number,
    search: string,
    sort: string
  ): Observable<any> {
    let params = new HttpParams().set('search', search).set('sort', sort);

    return this.http.get(`${this.apiUrl}/groupbyadherant/${page}/${size}`, {
      params,
    });
  }

  getGroupedByDate(
    page: number,
    size: number,
    period: string,
    search: string,
    sort: string
  ): Observable<any> {
    let params = new HttpParams()
      .set('period', period)
      .set('search', search)
      .set('sort', sort);

    return this.http.get(`${this.apiUrl}/groupbydate/${page}/${size}`, {
      params,
    });
  }
  getAmountDistribution(): Observable<{ category: string; value: number }[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/distribution`);
  }

  getMonthlyTrend(): Observable<{ month: string; count: number }[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/trend`);
  }

  getTopProfessionals(
    limit: number
  ): Observable<{ name: string; count: number }[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/stats/top-professionals?limit=${limit}`
    );
  }
  getMovements(page: number,size: number,search: string,sort: string): Observable<any> {
    let params = new HttpParams().set('search', search).set('sort', sort);
    return this.http.get(`${this.apiUrl}/${page}/${size}`, { params });
    }
}
