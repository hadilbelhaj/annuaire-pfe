import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EnhancedFraudCheckResult } from '../../services/Stats/reimbursement-fraud.service';
import { CommonModule } from '@angular/common';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Content, TDocumentDefinitions, TableCell } from 'pdfmake/interfaces';

@Component({
  selector: 'app-all-reimbursements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-reimbursements.component.html',
  styleUrl: './all-reimbursements.component.css'
})
export class AllReimbursementsComponent {
  @Input() results: EnhancedFraudCheckResult[] = [];
  @Output() viewDetails = new EventEmitter<EnhancedFraudCheckResult>();
}