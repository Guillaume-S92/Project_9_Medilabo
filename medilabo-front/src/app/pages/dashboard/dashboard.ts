import { Component, OnInit, signal, computed } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';
import { NoteService } from '../../services/note.service';
import { AssessmentService } from '../../services/assessment.service';
import { Patient } from '../../models/patient.model';
import { Note } from '../../models/note.model';
import { Assessment } from '../../models/assessment.model';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  patients = signal<Patient[]>([]);
  notes = signal<Note[]>([]);
  assessments = signal<Assessment[]>([]);
  loadingPatients = signal(false);
  loadingNotes = signal(false);
  loadingAssessments = signal(false);
  patientsError = signal('');

  constructor(
    private authService: AuthService,
    private router: Router,
    private patientService: PatientService,
    private noteService: NoteService,
    private assessmentService: AssessmentService
  ) {}

  ngOnInit(): void {
    this.loadPatients();
    if (this.isPractitioner()) {
      this.loadNotes();
    }
  }

  get username(): string {
    return this.authService.getUsername() ?? 'Utilisateur';
  }

  get roles(): string {
    const r = this.authService.getRoles();
    return r.length ? r.join(', ') : 'Aucun rôle';
  }

  isOrganizer(): boolean { return this.authService.hasRole('ORGANIZER'); }
  isPractitioner(): boolean { return this.authService.hasRole('PRACTITIONER'); }

  patientCount = computed(() => this.patients().length);
  noteCount = computed(() => this.notes().length);
  recentNotes = computed(() => this.notes().slice(0, 3));

  atRiskCount = computed(() =>
    this.assessments().filter(a => a.riskLevel !== 'None').length
  );

  criticalCount = computed(() =>
    this.assessments().filter(a => a.riskLevel === 'In Danger' || a.riskLevel === 'Early onset').length
  );

  assessmentsLoading = computed(() => this.loadingAssessments());

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  loadPatients(): void {
    this.loadingPatients.set(true);
    this.patientsError.set('');
    this.patientService.getPatients().subscribe({
      next: (patients) => {
        this.patients.set(patients);
        this.loadingPatients.set(false);
        if (this.isPractitioner()) {
          this.loadAssessments(patients);
        }
      },
      error: () => {
        this.patientsError.set('Impossible de charger les patients.');
        this.loadingPatients.set(false);
      }
    });
  }

  loadAssessments(patients: Patient[]): void {
    this.loadingAssessments.set(true);
    const results: Assessment[] = [];
    let completed = 0;
    if (patients.length === 0) { this.loadingAssessments.set(false); return; }
    for (const patient of patients) {
      this.assessmentService.getAssessmentByPatientId(patient.id).subscribe({
        next: (a) => {
          results.push(a);
          completed++;
          if (completed === patients.length) {
            this.assessments.set(results);
            this.loadingAssessments.set(false);
          }
        },
        error: () => {
          completed++;
          if (completed === patients.length) {
            this.assessments.set(results);
            this.loadingAssessments.set(false);
          }
        }
      });
    }
  }

  loadNotes(): void {
    this.loadingNotes.set(true);
    this.noteService.getAllNotes().subscribe({
      next: (notes) => { this.notes.set(notes); this.loadingNotes.set(false); },
      error: () => { this.loadingNotes.set(false); }
    });
  }

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('fr-FR');
  }
}
