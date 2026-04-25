import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient, PatientRequest } from '../models/patient.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly apiUrl = 'http://localhost:8080/api/patients';

  constructor(private http: HttpClient) {}

  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.apiUrl);
  }

  getPatientById(id: string): Observable<Patient> {
    return this.http.get<Patient>(`${this.apiUrl}/${id}`);
  }

  createPatient(request: PatientRequest): Observable<Patient> {
    return this.http.post<Patient>(this.apiUrl, request);
  }

  updatePatient(id: string, request: PatientRequest): Observable<Patient> {
    return this.http.put<Patient>(`${this.apiUrl}/${id}`, request);
  }
}
