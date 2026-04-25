import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';
import { NoteService } from '../../services/note.service';
import { Patient, PatientRequest } from '../../models/patient.model';
import { Note } from '../../models/note.model';

@Component({
  selector: 'app-patient-detail',
  imports: [FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.css'
})
export class PatientDetail implements OnInit {
  loading = signal(false);
  saving = signal(false);
  pageError = signal('');
  successMessage = signal('');
  isNew = signal(false);
  patientId = signal('');

  notes = signal<Note[]>([]);
  loadingNotes = signal(false);
  notesError = signal('');
  noteContent = signal('');
  savingNote = signal(false);
  noteSuccessMessage = signal('');

  form = signal<PatientRequest>({
    firstName: '',
    lastName: '',
    birthDate: '',
    gender: 'M',
    address: '',
    phone: ''
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private patientService: PatientService,
    private noteService: NoteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const urlSegments = this.route.snapshot.url.map(segment => segment.path);
    const isNewRoute = urlSegments.includes('new') || id === 'new';

    if (isNewRoute) {
      this.isNew.set(true);

      if (!this.isOrganizer()) {
        this.router.navigate(['/patients']);
        return;
      }

      return;
    }

    if (!id) {
      this.router.navigate(['/patients']);
      return;
    }

    this.patientId.set(id);
    this.loadPatient(id);

    if (this.isPractitioner()) {
      this.loadNotes(id);
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

  pageTitle = computed(() => this.isNew() ? 'Nouveau patient' : 'Fiche patient');

  pageSubtitle = computed(() =>
    this.isNew()
      ? 'Création d’un nouveau dossier patient'
      : 'Consultation et mise à jour des informations patient'
  );

  canSaveNote = computed(() =>
    this.noteContent().trim().length > 0 && !this.savingNote()
  );

  updateField<K extends keyof PatientRequest>(field: K, value: PatientRequest[K]): void {
    this.form.update(current => ({
      ...current,
      [field]: value
    }));
  }

  loadPatient(id: string): void {
    this.loading.set(true);
    this.pageError.set('');
    this.successMessage.set('');

    this.patientService.getPatientById(id).subscribe({
      next: (patient: Patient) => {
        this.form.set({
          firstName: patient.firstName,
          lastName: patient.lastName,
          birthDate: patient.birthDate,
          gender: patient.gender,
          address: patient.address ?? '',
          phone: patient.phone ?? ''
        });
        this.loading.set(false);
      },
      error: () => {
        this.pageError.set('Impossible de charger la fiche patient.');
        this.loading.set(false);
      }
    });
  }

  loadNotes(patientId: string): void {
    this.loadingNotes.set(true);
    this.notesError.set('');
    this.noteSuccessMessage.set('');

    this.noteService.getNotesByPatientId(patientId).subscribe({
      next: (notes) => {
        this.notes.set(notes);
        this.loadingNotes.set(false);
      },
      error: () => {
        this.notesError.set('Impossible de charger les notes du patient.');
        this.loadingNotes.set(false);
      }
    });
  }

  savePatient(): void {
    if (!this.isOrganizer()) {
      return;
    }

    this.saving.set(true);
    this.pageError.set('');
    this.successMessage.set('');

    const request: PatientRequest = {
      firstName: this.form().firstName.trim(),
      lastName: this.form().lastName.trim(),
      birthDate: this.form().birthDate,
      gender: this.form().gender,
      address: this.form().address.trim(),
      phone: this.form().phone.trim()
    };

    if (this.isNew()) {
      this.patientService.createPatient(request).subscribe({
        next: (patient) => {
          this.saving.set(false);
          this.successMessage.set('Patient créé avec succès.');
          this.router.navigate(['/patients', patient.id]);
        },
        error: () => {
          this.saving.set(false);
          this.pageError.set('Impossible de créer le patient.');
        }
      });
      return;
    }

    this.patientService.updatePatient(this.patientId(), request).subscribe({
      next: (patient) => {
        this.form.set({
          firstName: patient.firstName,
          lastName: patient.lastName,
          birthDate: patient.birthDate,
          gender: patient.gender,
          address: patient.address ?? '',
          phone: patient.phone ?? ''
        });
        this.saving.set(false);
        this.successMessage.set('Patient mis à jour avec succès.');
      },
      error: () => {
        this.saving.set(false);
        this.pageError.set('Impossible de mettre à jour le patient.');
      }
    });
  }

  addNote(): void {
    const content = this.noteContent().trim();

    if (!this.isPractitioner() || !content || !this.patientId()) {
      return;
    }

    this.savingNote.set(true);
    this.notesError.set('');
    this.noteSuccessMessage.set('');

    this.noteService.createNote({
      patientId: this.patientId(),
      content
    }).subscribe({
      next: (createdNote) => {
        this.notes.update(current => [createdNote, ...current]);
        this.noteContent.set('');
        this.savingNote.set(false);
        this.noteSuccessMessage.set('Note ajoutée avec succès.');
      },
      error: () => {
        this.savingNote.set(false);
        this.notesError.set('Impossible d’ajouter la note.');
      }
    });
  }

  formatDateTime(value: string): string {
    if (!value) {
      return '—';
    }

    return new Date(value).toLocaleString('fr-FR');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
