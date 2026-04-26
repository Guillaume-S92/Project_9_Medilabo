import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Assessment } from '../models/assessment.model';

@Injectable({
  providedIn: 'root'
})
export class AssessmentService {
  private readonly apiUrl = 'http://localhost:8080/api/assessments';

  constructor(private http: HttpClient) {}

  getAssessmentByPatientId(patientId: string): Observable<Assessment> {
    return this.http.get<Assessment>(`${this.apiUrl}/patient/${patientId}`);
  }
}
