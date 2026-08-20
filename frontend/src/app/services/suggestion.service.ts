import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
export enum SuggestionStatus {
  PENDING = 'PENDING',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  IMPLEMENTED = 'IMPLEMENTED',
  REJECTED = 'REJECTED'
}

export interface Suggestion {
  id?: number;
  title: string;
  description: string;
  category: string;
  status?: SuggestionStatus;
  userEmail?: string;
  userName?: string;
  createdAt?: string;
  upvotes?: number;
  adminFeedback?: string;
}

export const SUGGESTION_CATEGORIES = [
  'Healthcare Provider Search',
  'Nearest Provider Locator',
  'Appointment Booking',
  'User Interface',
  'User Experience',
  'Mobile App',
  'Security',
  'Accessibility',
  'Other'
];

@Injectable({
  providedIn: 'root'
})
export class SuggestionService {

  private apiUrl = `http://localhost:8090/api/suggestions`;

  constructor(private http: HttpClient) {}

  createSuggestion(suggestion: Suggestion): Observable<Suggestion> {
    return this.http.post<Suggestion>(this.apiUrl, suggestion);
  }

  getAllSuggestions(): Observable<Suggestion[]> {
    return this.http.get<Suggestion[]>(this.apiUrl);
  }

  getSuggestionsByCategory(category: string): Observable<Suggestion[]> {
    return this.http.get<Suggestion[]>(`${this.apiUrl}/category/${encodeURIComponent(category)}`);
  }

  getMySuggestions(): Observable<Suggestion[]> {
    return this.http.get<Suggestion[]>(`${this.apiUrl}/my-suggestions`);
  }

  getSuggestionById(id: number): Observable<Suggestion> {
    return this.http.get<Suggestion>(`${this.apiUrl}/${id}`);
  }

  updateSuggestion(id: number, suggestion: Suggestion): Observable<Suggestion> {
    return this.http.put<Suggestion>(`${this.apiUrl}/${id}`, suggestion);
  }

  upvoteSuggestion(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/upvote`, {});
  }

  deleteSuggestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateSuggestionStatus(id: number, suggestion: Suggestion): Observable<Suggestion> {
    return this.http.put<Suggestion>(`${this.apiUrl}/${id}/status`, suggestion);
  }
}
