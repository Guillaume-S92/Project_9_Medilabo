import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note, NoteRequest } from '../models/note.model';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private readonly apiUrl = 'http://localhost:8080/api/notes';

  constructor(private http: HttpClient) {}

  getAllNotes(): Observable<Note[]> {
    return this.http.get<Note[]>(this.apiUrl);
  }

  getNotesByPatientId(patientId: string): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  createNote(request: NoteRequest): Observable<Note> {
    return this.http.post<Note>(this.apiUrl, request);
  }

  deleteNote(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
