import { Component } from '@angular/core';
import { Professional } from '../../models/professional.modal';
import { Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-professional-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './professional-card.component.html',
  styleUrl: './professional-card.component.css'
})
export class ProfessionalCardComponent {
  @Input() professional!: Professional;

}
