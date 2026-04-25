import { Component, OnInit, signal, computed } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';
import { Patient } from '../../models/patient.model';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  patients = signal<Patient[]>([]);
  loadingPatients = signal(false);
  patientsError = signal('');

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

  patientCount = computed(() => this.patients().length);

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
}
