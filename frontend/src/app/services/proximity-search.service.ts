import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NearbyProviderDTO {
  id: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  distanceKm: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export enum PrestationType {
    Consultation,
    Pharmacie,
    UU,
    AMC,
    Analyse,
    Ortho,
    Radio,
    SD,
    PD,
    ODF,
    Implant,
    Verre,
    Lentille,
    Hospitalisation,
    Circoncision,
    Maternité,
    Chirurgie,
    FF,
    Transport,
 
}

export interface UserAddress {
  address: string;
  latitude: number;
  longitude: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProximitySearchService {

  private apiUrl = 'http://localhost:8090/api/proximity';

  constructor(private http: HttpClient) { }
  findNearbyProvidersForUser(
    prestation?: PrestationType,
    maxDistanceKm: number = 10,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<NearbyProviderDTO>> {
    let params = new HttpParams()
      .set('maxDistanceKm', maxDistanceKm.toString())
      .set('page', page.toString())
      .set('size', size.toString());
  
    if (prestation) {
      params = params.set('prestation', prestation);
    }
    console.log('Token before search call:', localStorage.getItem('authToken'));
    
    return this.http.get<PageResponse<NearbyProviderDTO>>(`${this.apiUrl}/search/user`, { params });
  }
  findNearbyProvidersByAddress(
    address: string,
    prestation?: PrestationType,
    maxDistanceKm: number = 10,
    authenticated: boolean = false,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<NearbyProviderDTO>> {
    let params = new HttpParams()
      .set('address', address)
      .set('maxDistanceKm', maxDistanceKm.toString())
      .set('authenticated', authenticated.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    if (prestation) {
      params = params.set('prestation', prestation);
    }

    return this.http.get<PageResponse<NearbyProviderDTO>>(`${this.apiUrl}/search/address`, { params });
  }
}
