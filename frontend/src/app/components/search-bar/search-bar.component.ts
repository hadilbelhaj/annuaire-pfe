import { Component, OnInit } from '@angular/core';
import { ProfessionalService } from '../../services/professional.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Professional } from '../../models/professional.modal';
import { BehaviorSubject } from 'rxjs';
import { Page } from '../../models/page.modal';
import { SearchComponent } from '../search/search.component';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule,SearchComponent],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent  {
  
  
}