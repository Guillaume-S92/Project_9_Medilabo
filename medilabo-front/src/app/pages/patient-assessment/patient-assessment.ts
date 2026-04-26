import { Component, OnInit, computed, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AssessmentService } from '../../services/assessment.service';
import { Assessment } from '../../models/assessment.model';

@Component({
  selector: 'app-patient-assessment',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './patient-assessment.html',
  styleUrl: './patient-assessment.css'
})
export class PatientAssessment implements OnInit {
  loading = signal(false);
  pageError = signal('');
  assessment = signal<Assessment | null>(null);
  patientId = signal('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private assessmentService: AssessmentService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.router.navigate(['/patients']);
      return;
    }

    this.patientId.set(id);

    if (!this.isPractitioner()) {
      this.pageError.set('Cette page est accessible uniquement au rôle PRACTITIONER.');
      return;
    }

    this.loadAssessment(id);
  }

  get username(): string {
    return this.authService.getUsername() ?? 'Utilisateur';
  }

  get roles(): string {
    const roles = this.authService.getRoles();
    return roles.length ? roles.join(', ') : 'Aucun rôle';
  }

  isPractitioner(): boolean {
    return this.authService.hasRole('PRACTITIONER');
  }

  riskLevel = computed(() => this.assessment()?.riskLevel ?? '—');

  riskToneClass = computed(() => {
    const risk = (this.assessment()?.riskLevel ?? '').toLowerCase();

    if (risk.includes('early')) {
      return 'risk-early';
    }

    if (risk.includes('danger')) {
      return 'risk-danger';
    }

    if (risk.includes('border')) {
      return 'risk-borderline';
    }

    return 'risk-none';
  });

  riskDescription = computed(() => {
    const risk = (this.assessment()?.riskLevel ?? '').toLowerCase();

    if (risk.includes('early')) {
      return 'Risque très élevé, surveillance prioritaire recommandée.';
    }

    if (risk.includes('danger')) {
      return 'Patient à risque, suivi clinique rapproché conseillé.';
    }

    if (risk.includes('border')) {
      return 'Risque modéré, vigilance recommandée.';
    }

    return 'Aucun risque significatif détecté à partir des données actuelles.';
  });

  loadAssessment(patientId: string): void {
    this.loading.set(true);
    this.pageError.set('');

    this.assessmentService.getAssessmentByPatientId(patientId).subscribe({
      next: (assessment) => {
        this.assessment.set(assessment);
        this.loading.set(false);
      },
      error: () => {
        this.pageError.set('Impossible de charger l’assessment du patient.');
        this.loading.set(false);
      }
    });
  }

  formatGender(gender: string): string {
    if (gender === 'M') {
      return 'Homme';
    }

    if (gender === 'F') {
      return 'Femme';
    }

    return gender;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
