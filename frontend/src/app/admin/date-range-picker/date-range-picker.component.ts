import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-date-range-picker',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './date-range-picker.component.html',
  styleUrl: './date-range-picker.component.css'
})
export class DateRangePickerComponent {
  @Output() dateRangeSelected = new EventEmitter<{startDate: Date, endDate: Date}>();
  
  dateRangeForm = new FormGroup({
    startDate: new FormControl<string>('', [Validators.required]),
    endDate: new FormControl<string>('', [Validators.required])
  });
  
  get startDateControl() { return this.dateRangeForm.get('startDate') as FormControl; }
  get endDateControl() { return this.dateRangeForm.get('endDate') as FormControl; }
  
  onSubmit() {
    if (this.dateRangeForm.valid) {
      // Convert string dates to Date objects
      const startDateStr = this.dateRangeForm.value.startDate as string;
      const endDateStr = this.dateRangeForm.value.endDate as string;
      
      const startDate = new Date(startDateStr);
      const endDate = new Date(endDateStr);
      
      if (startDate > endDate) {
        alert('Start date must be before end date');
        return;
      }
      
      this.dateRangeSelected.emit({
        startDate,
        endDate
      });
    }
  }
}
 