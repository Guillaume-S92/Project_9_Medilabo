export interface Patient {
  id: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: 'M' | 'F';
  address: string | null;
  phone: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PatientRequest {
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: 'M' | 'F';
  address: string;
  phone: string;
}
