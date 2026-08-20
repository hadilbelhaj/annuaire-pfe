import { Component, OnInit } from '@angular/core';
import { FraudDetectionService } from '../../services/Stats/fraud-detection.service';
import { ExceededAdherent } from '../../services/Stats/fraud-detection.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-exceeded-deductibles',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './exceeded-deductibles.component.html',
  styleUrl: './exceeded-deductibles.component.css'
})
export class ExceededDeductiblesComponent implements OnInit {
  exceededAdherents: ExceededAdherent[] = [];
  loading = false;
  totalInsurancePaidUnderDeductible:number=0 ;

  constructor(private deductibleService: FraudDetectionService) {}

  ngOnInit() {
    this.fetchExceededAdherents();
    console.log("calling");
  }

  fetchExceededAdherents() {
    this.loading = true;
    this.deductibleService.getExceededDeductibles().subscribe({
      next: (data) => {
        this.exceededAdherents = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching exceeded deductibles', err);
        this.loading = false;
      }
    });
  }
  calculateTotalInsurancePaidUnderDeductible(adherent:ExceededAdherent):number {
    this.exceededAdherents.map(adherent => {
      this.totalInsurancePaidUnderDeductible = adherent.claimsCausingExcess.reduce(
        (sum, claim) => sum + claim.insuranceAmount, 
        0
      );
    })
    const amount =adherent.deductible-this.totalInsurancePaidUnderDeductible;
    this.totalInsurancePaidUnderDeductible=0;
    return amount
      ;
  }
}
