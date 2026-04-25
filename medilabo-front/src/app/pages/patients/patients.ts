import { Component, OnInit, computed, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';
import { NoteService } from '../../services/note.service';
import { Patient } from '../../models/patient.model';
import { Note } from '../../models/note.model';

@Component({
  selector: 'app-patients',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './patients.html',
  styleUrl: './patients.css'
})
export class Patients implements OnInit {
  patients = signal<Patient[]>([]);
  loadingPatients = signal(false);
  patientsError = signal('');
  searchTerm = signal('');

  // Map patientId -> notes[]
  notesMap = signal<Map<string, Note[]>>(new Map());
  notesLoaded = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router,
    private patientService: PatientService,
    private noteService: NoteService
  ) {}

  ngOnInit(): void {
    this.loadPatients();
    if (this.isPractitioner()) {
      this.loadAllNotes();
    }
  }

  get username(): string {
    return this.authService.getUsername() ?? 'Utilisateur';
  }

  get roles(): string {
    const roles = this.authService.getRoles();
    return roles.length ? roles.join(', ') : 'Aucun rôle';
  }

  isOrganizer = computed(() => this.authService.hasRole('ORGANIZER'));
  isPractitioner = computed(() => this.authService.hasRole('PRACTITIONER'));

  filteredPatients = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) return this.patients();
    return this.patients().filter((patient) => {
      const values = [
        patient.lastName, patient.firstName, patient.birthDate,
        patient.gender, patient.address ?? '', patient.phone ?? '', patient.id
      ];
      return values.some((value) => value.toLowerCase().includes(term));
    });
  });

  patientCount = computed(() => this.filteredPatients().length);

  getNoteCount(patientId: string): number {
    return this.notesMap().get(patientId)?.length ?? 0;
  }

  getLastNote(patientId: string): string {
    const notes = this.notesMap().get(patientId);
    if (!notes || notes.length === 0) return '—';
    const last = notes[0]; // already sorted desc
    return new Date(last.createdAt).toLocaleDateString('fr-FR');
  }

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
      },
      error: () => {
        this.patientsError.set('Impossible de charger les patients.');
        this.loadingPatients.set(false);
      }
    });
  }

  loadAllNotes(): void {
    this.noteService.getAllNotes().subscribe({
      next: (notes) => {
        const map = new Map<string, Note[]>();
        for (const note of notes) {
          if (!map.has(note.patientId)) map.set(note.patientId, []);
          map.get(note.patientId)!.push(note);
        }
        this.notesMap.set(map);
        this.notesLoaded.set(true);
      },
      error: () => {
        this.notesLoaded.set(true);
      }
    });
  }

  updateSearchTerm(value: string): void {
    this.searchTerm.set(value);
  }
}
