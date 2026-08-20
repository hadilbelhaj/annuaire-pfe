import { Injectable, NgModule } from '@angular/core';
import { Professional, ProfessionalRequest } from '../models/professional.modal';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Page } from '../models/page.modal';
import { AuthserviceService } from './authservice.service';

@Injectable({
  providedIn: 'root',
})
export class ProfessionalService {
  private apiUrl = 'http://localhost:8090/api/ps';
  
  // Add BehaviorSubject for professionals data
  private professionalsSubject = new BehaviorSubject<Page<Professional>>({
    content: [],
    pageable: {
      pageNumber: 0,
      pageSize: 10,
      offset: 0,
      paged: true,
      unpaged: false,
      sort: {
        empty: true,
        sorted: false,
        unsorted: true
      }
    },
    totalElements: 0,
    totalPages: 0,
    size: 0,
    number: 0,
    first: true,
    last: true,
    empty: true,
    numberOfElements: 0,
    sort: {
      empty: true,
      sorted: false,
      unsorted: true
    }
  });
  private specialtiesSubject = new BehaviorSubject<string[]>([]);
  private regionsSubject = new BehaviorSubject<string[]>([]);
  specialties$ = this.specialtiesSubject.asObservable();
  regions$ = this.regionsSubject.asObservable();

  // Public observable that components can subscribe to
  professionals$ = this.professionalsSubject.asObservable();
  private lastSearchCriteriaSubject = new BehaviorSubject<{ 
    name?: string; 
    specialty?: string; 
    region?: string;
  }>({});
  
  lastSearchCriteria$ = this.lastSearchCriteriaSubject.asObservable();
  private authService :AuthserviceService;

  constructor(private http: HttpClient,authService:AuthserviceService)
   {this.loadSpecialtiesAndRegions();
    this.authService=authService;
   }
  updateLastSearchCriteria(criteria: { name?: string; specialty?: string; region?: string }) {
    this.lastSearchCriteriaSubject.next(criteria);
  }
  getAllProfessionals(): Observable<Professional[]> {
    return this.http.get<Professional[]>(this.apiUrl);
  }
  private loadSpecialtiesAndRegions(): void {
    // Load specialties
    this.http.get<string[]>(this.apiUrl + '/specs')
      .pipe(
        tap(specialties => this.specialtiesSubject.next(specialties))
      )
      .subscribe();

    // Load regions
    this.http.get<string[]>(this.apiUrl + '/regions')
      .pipe(
        tap(regions => this.regionsSubject.next(regions))
      )
      .subscribe();
  }

  getUniqueSpecialties(): Observable<string[]> {

    return this.specialties$;
  }

  getUniqueRegions(): Observable<string[]> {
    
    return this.regions$;
  }

 

  getProfessionalsByPageAndRegion(page: number, size: number, region: string): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/paginator/region/${page}/${size}?region=${region}&authenticated=${isAuthenticated}`
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getProfessionalsByPageAndSpecialty(page: number, size: number, specialty: string): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/paginator/speciality/${page}/${size}?speciality=${specialty}&authenticated=${isAuthenticated}`
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getProfessionalsBypageAndName(page: number, size: number, name: string): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/paginator/search/${page}/${size}?name=${name}&authenticated=${isAuthenticated}`
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getPaginatedProfessionals(
    page: number, 
    size: number, 
    specialty: string, 
    region: string, 
    name: string
  ): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const params = new HttpParams()
      .set('speciality', specialty)
      .set('region', region)
      .set('name', name)
      .set('authenticated', isAuthenticated.toString());

    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/paginator/specialty/region/${page}/${size}`,
      { params }
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getProfessionalsBySpecialtyRegion(
    page: number,
    size: number,
    specialty: string,
    region: string
  ): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const params = new HttpParams()
      .set('speciality', specialty)
      .set('region', region)
      .set('authenticated', isAuthenticated.toString());

    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/page/specialty/region/${page}/${size}`,
      { params }
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getProfessionalsByNameRegion(
    page: number,
    size: number,
    name: string,
    region: string
  ): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const params = new HttpParams()
      .set('name', name)
      .set('region', region)
      .set('authenticated', isAuthenticated.toString());

    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/page/name/region/${page}/${size}`,
      { params }
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }

  getProfessionalsByNameSpecialty(
    page: number,
    size: number,
    name: string,
    specialty: string
  ): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    const params = new HttpParams()
      .set('name', name)
      .set('specialty', specialty)
      .set('authenticated', isAuthenticated.toString());

    const request = this.http.get<Page<Professional>>(
      this.apiUrl + `/page/name/specialty/${page}/${size}`,
      { params }
    );
    
    request.subscribe(response => this.professionalsSubject.next(response));
    return request;
  }


  getData(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  getPaginated(page: number, size: number, searchTerm?: string, deletedFilter: string = 'all'): Observable<Page<Professional>> {
    const isAuthenticated = this.authService.isAuthenticated();
    let url = this.apiUrl + `/paginator/${page}/${size}?authenticated=${isAuthenticated}`;
    
   
    if (searchTerm && searchTerm.trim() !== '') {
      url += `&search=${encodeURIComponent(searchTerm)}`;
    }
  
    url += `&deletedFilter=${deletedFilter}`;
    
    console.log(url);
    return this.http.get<Page<Professional>>(url).pipe(
      tap(pageResponse => this.professionalsSubject.next(pageResponse))
    );
  }

  createProfessional(professional: Professional, prestationLabels: string[] = []): Observable<Professional> {
    const request: ProfessionalRequest = {
      healthcareProfessional: professional,
      prestationLabels: prestationLabels
    };

    return this.http.post<Professional>(this.apiUrl, request).pipe(
      tap(() => {
        const currentPage = this.professionalsSubject.value;
        this.getPaginated(currentPage.number, currentPage.size).subscribe();
      })
    );
  }

  updateProfessional(id: number, professional: Professional, prestationLabels: string[] = []): Observable<Professional> {
    const request: ProfessionalRequest = {
      healthcareProfessional: professional,
      prestationLabels: prestationLabels
    };

    return this.http.put<Professional>(`${this.apiUrl}/${id}`, request).pipe(
      tap(() => {
        const currentPage = this.professionalsSubject.value;
        this.getPaginated(currentPage.number, currentPage.size).subscribe();
      })
    );
  }

  deleteProfessional(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        
        const currentPage = this.professionalsSubject.value;
        this.getPaginated(currentPage.number, currentPage.size).subscribe();
      })
    );
  }

  
  restoreProfessional(id: number): Observable<Professional> {
    return this.http.put<Professional>(`${this.apiUrl}/admin/restore/${id}`, {}).pipe(
      tap(() => {
       
        const currentPage = this.professionalsSubject.value;
        this.getPaginated(currentPage.number, currentPage.size).subscribe();
      })
    );
  }
}