import { Component, OnInit, computed, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../models/patient.model';

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

  constructor(
    private authService: AuthService,
    private router: Router,
    private patientService: PatientService
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  get username(): string {
    return this.authService.getUsername() ?? 'Utilisateur';
  }

  get roles(): string {
    const roles = this.authService.getRoles();
    return roles.length ? roles.join(', ') : 'Aucun rôle';
  }

  isOrganizer = computed(() => this.authService.hasRole('ORGANIZER'));

  filteredPatients = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();

    if (!term) {
      return this.patients();
    }

    return this.patients().filter((patient) => {
      const values = [
        patient.lastName,
        patient.firstName,
        patient.birthDate,
        patient.gender,
        patient.address ?? '',
        patient.phone ?? '',
        patient.id
      ];

      return values.some((value) => value.toLowerCase().includes(term));
    });
  });

  patientCount = computed(() => this.filteredPatients().length);

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

  updateSearchTerm(value: string): void {
    this.searchTerm.set(value);
  }
}
