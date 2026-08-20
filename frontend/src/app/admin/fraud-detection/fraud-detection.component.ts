import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FraudDetectionService } from '../../services/Stats/fraud-detection.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface MedicalClaim {
  amount: number;
  specialtyAverageAmount: number;
  medicalSpecialty: string;
  designation: string;
  reimbursementPercentage: number;
}

interface FraudTestType {
  id: number;
  name: string;
  description: string;
  detects: string;
  icon: string;
}

interface AnalysisResult {
  actualPercentage: number;
  isFraudulent: boolean;
  predictedPercentage: number;
  threshold: number;
}

@Component({
  selector: 'app-fraud-detection',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './fraud-detection.component.html',
  styleUrl: './fraud-detection.component.css'
})
export class FraudDetectionComponent implements OnInit {
  fraudTestTypes: FraudTestType[] = [];
  loading = true;
  selectedTestType: FraudTestType | null = null;
  showTestModal = false;
  testResult: AnalysisResult | null = null;
  testForm: FormGroup;

  constructor(
    private fraudService: FraudDetectionService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.testForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0)]],
      specialtyAverageAmount: ['', [Validators.required, Validators.min(0)]],
      medicalSpecialty: ['', Validators.required],
      designation: ['', Validators.required],
      reimbursementPercentage: ['', [Validators.required, Validators.min(0), Validators.max(100)]]
    });
  }

  ngOnInit(): void {
    this.fetchFraudTestTypes();
  }

  fetchFraudTestTypes(): void {
    this.loading = true;
    setTimeout(() => {
      this.fraudTestTypes = [
        {
          id: 1,
          name: "Test Reimbursement Fraud Possibilities",
          description: "Analyze claims for abnormal reimbursement percentages compared to provider and specialty norms",
          detects: "Excessive billing percentages, reimbursement rate anomalies",
          icon: "M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"
        },
        {
          id:3,
            name: 'Prestation Fraud Detection',
            description: 'Checks if a doctor committed a prestation that they are not authorized to perform',
            detects: 'Inconsistent claim reimbursements and thresholds',
            icon: 'M9 17v-2h6v2H9zm0-4v-2h6v2H9zm0-4V7h6v2H9z'
          },
          {
            id: 4,
            name: "Doctor-Adherent Collusion Detection",
            description: "Detect potential collusion between healthcare professionals and adherents based on abnormal claiming patterns, such as repeated same-day visits or high-frequency claims.",
            detects: "Suspicious claiming behavior, excessive repeat visits, high-value collusion, misuse of ActePS codes",
            icon: "M12 6a2 2 0 012 2v4h4a2 2 0 012 2v6a2 2 0 01-2 2H8a2 2 0 01-2-2v-6a2 2 0 012-2h4V8a2 2 0 012-2z"
          },
      ];
      this.loading = false;
    }, 1000);
  }

  selectTestType(testType: FraudTestType): void {
    this.selectedTestType = testType;
    if (testType.id === 1) {
      this.router.navigate(['/admin/reimbursement']); 
      return;
    }
    if (testType.id === 2) {
      this.router.navigate(['/admin/deductible']); 
      return;
    }
    if (testType.id === 3) {
      this.router.navigate(['/admin/prestation-fraud-detection']); // Navigate to the new prestation fraud detection page
      return;
    }
    if (testType.id === 4) {
      this.router.navigate(['/admin/Collusion']); // Navigate to the new prestation fraud detection page
      return;
    }
  
    this.showTestModal = true;
    this.testResult = null;
  }
  getProgressBarWidth(actualPercentage: number, predictedPercentage: number): number {
    const difference = Math.abs(actualPercentage - predictedPercentage);
    const ratio = (difference / actualPercentage) * 100;
    return Math.min(100, ratio);
  }

  closeModal(): void {
    this.showTestModal = false;
    this.selectedTestType = null;
    this.testResult = null;
  }

  submitTest(): void {
    if (this.testForm.invalid) {
      this.markFormGroupTouched(this.testForm);
      return;
    }

    this.testResult = null;
    
    const claimData: MedicalClaim = {
      ...this.testForm.value
    };
    console.log(claimData);

    this.fraudService.analyzeClaimDetails(claimData).subscribe({
      next: (response) => {
        this.testResult = response;
        console.log(response);
      },
      error: (error) => {
        console.error('Error analyzing claim:', error);}
        
    });
  }

  
  markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if ((control as any).controls) {
        this.markFormGroupTouched(control as FormGroup);
      }
    });
  }

  getRiskColor(score: number): string {
    if (score < 0.7) return '#ffc107'; // yellow
    if (score < 0.85) return '#fd7e14'; // orange
    return '#dc3545'; // red
  }

  getTextColorClass(score: number): string {
    return score > 0.5 ? 'text-white' : 'text-gray-900';
  }

  getAlertClass(isFraudulent: boolean): string {
    return isFraudulent ? 'bg-red-100 border-red-500 text-red-900' : 'bg-green-100 border-green-500 text-green-900';
  }

  getPercentageDifferenceClass(actual: number, predicted: number): string {
    const diff = actual - predicted;
    if (diff > 30) return 'text-red-600 font-bold';
    if (diff > 20) return 'text-orange-600 font-medium';
    if (diff > 10) return 'text-yellow-600';
    return 'text-green-600';
  }
}