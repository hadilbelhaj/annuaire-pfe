import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { ExceededDeductiblesComponent } from '../exceeded-deductibles/exceeded-deductibles.component';

interface DeductibleStatus {
  adherentName: string;
  currentDeductible: number;
  totalDeductible: number;
  remainingAmount: number;
}

interface ApproachingAdherent {
  name: string;
  currentDeductible: number;
  threshold: number;
}

interface ExceededAdherent {
  name: string;
  claimDetails: {
    year: number;
    totalClaims: number;
  }[];
}


@Component({
  selector: 'app-deductible-check',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule,ExceededDeductiblesComponent,RouterOutlet],
  templateUrl: './deductible-check.component.html',
  styleUrl: './deductible-check.component.css'
})
export class DeductibleCheckComponent {
  deductibleForm: FormGroup;
  deductibleStatus: DeductibleStatus | null = null;
  approachingAdherents: ApproachingAdherent[] = [];
  exceededAdherents: ExceededAdherent[] = [];
  
  loading = {
    status: false,
    approaching: false,
    exceeded: false
  };
  
  error: string | null = null;

  constructor(
    private http: HttpClient,
    private fb: FormBuilder,
    private router :Router
  ) {
    this.deductibleForm = this.fb.group({
      adherentName: ['']
    });
  }

  ngOnInit(): void {}

  fetchDeductibleStatus() {
    const adherentName = this.deductibleForm.get('adherentName')?.value;
    
    if (!adherentName) {
      this.error = 'Please enter an adherent name';
      return;
    }

    this.loading.status = true;
    this.error = null;

    this.http.get<DeductibleStatus>(`/api/deductible/check`, {
      params: { adherentName }
    }).subscribe({
      next: (data) => {
        this.deductibleStatus = data;
        this.loading.status = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to fetch deductible status';
        this.loading.status = false;
      }
    });
  }

  fetchApproachingAdherents() {
    this.loading.approaching = true;
    this.http.get<ApproachingAdherent[]>('/api/deductible/approaching')
      .subscribe({
        next: (data) => {
          this.approachingAdherents = data;
          this.loading.approaching = false;
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to fetch approaching adherents';
          this.loading.approaching = false;
        }
      });
  }
  goToExcededPage(){
    this.router.navigate(['/admin/Exceded']);
  }
}
