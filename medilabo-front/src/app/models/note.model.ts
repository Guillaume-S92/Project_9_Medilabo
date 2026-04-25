export interface Note {
  id: string;
  patientId: string;
  content: string;
  practitionerUsername: string;
  createdAt: string;
  updatedAt: string;
}

export interface NoteRequest {
  patientId: string;
  content: string;
}
