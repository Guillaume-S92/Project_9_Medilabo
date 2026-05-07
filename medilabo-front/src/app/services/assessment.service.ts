import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Assessment } from '../models/assessment.model';

@Injectable({
  providedIn: 'root'
})
export class AssessmentService {
  private readonly apiUrl = '/api/assessments';

  constructor(private http: HttpClient) {}

  getAssessmentByPatientId(patientId: string): Observable<Assessment> {
    return this.http.get<Assessment>(`${this.apiUrl}/patient/${patientId}`);
  }
}
