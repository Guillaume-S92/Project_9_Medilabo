export interface Assessment {
  patientId: string;
  patientFirstName: string;
  patientLastName: string;
  age: number;
  gender: string;
  noteCount: number;
  triggerCount: number;
  matchedTriggers: string[];
  riskLevel: string;
}
