import { Component, OnInit, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NoteService } from '../../services/note.service';
import { Note } from '../../models/note.model';

@Component({
  selector: 'app-notes',
  imports: [RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './notes.html',
  styleUrl: './notes.css'
})
export class Notes implements OnInit {
  notes = signal<Note[]>([]);
  loading = signal(false);
  error = signal('');
  searchTerm = signal('');

  constructor(
    private authService: AuthService,
    private router: Router,
    private noteService: NoteService
  ) {}

  ngOnInit(): void {
    if (!this.authService.hasRole('PRACTITIONER')) {
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadNotes();
  }

  get username(): string {
    return this.authService.getUsername() ?? 'Utilisateur';
  }

  get roles(): string {
    const r = this.authService.getRoles();
    return r.length ? r.join(', ') : 'Aucun rôle';
  }

  filteredNotes = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    if (!term) return this.notes();
    return this.notes().filter(n =>
      n.content.toLowerCase().includes(term) ||
      n.patientId.toLowerCase().includes(term) ||
      n.practitionerUsername.toLowerCase().includes(term)
    );
  });

  noteCount = computed(() => this.filteredNotes().length);

  loadNotes(): void {
    this.loading.set(true);
    this.error.set('');
    this.noteService.getAllNotes().subscribe({
      next: (notes) => { this.notes.set(notes); this.loading.set(false); },
      error: () => { this.error.set('Impossible de charger les notes.'); this.loading.set(false); }
    });
  }

  updateSearch(value: string): void {
    this.searchTerm.set(value);
  }

  deleteNote(noteId: string): void {
    if (!confirm('Supprimer cette note définitivement ?')) return;
    this.noteService.deleteNote(noteId).subscribe({
      next: () => this.notes.update(list => list.filter(n => n.id !== noteId)),
      error: () => alert('Impossible de supprimer la note.')
    });
  }

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('fr-FR');
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
