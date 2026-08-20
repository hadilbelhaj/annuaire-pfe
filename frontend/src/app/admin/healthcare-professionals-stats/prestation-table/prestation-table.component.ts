import { Component, Input } from '@angular/core';
import { Prestation } from '../../../services/Stats/prestation-stats.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-prestation-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './prestation-table.component.html',
  styleUrl: './prestation-table.component.css'
})
export class PrestationTableComponent {
  @Input() data: Prestation[] = [];
  
  constructor() {}

}
