import { Component, OnInit } from '@angular/core';
import { MovementService } from '../../services/movement.service';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Movement } from '../../models/movement.modal';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';
import { FormsModule } from '@angular/forms';
interface MovementInerface {
  id: number;
  date: string;
  description: string;
  total: number;
  amount: number;
  adherant: {
    firstName: string;
    lastName: string;
    email: string;
    deductible: number;
    region: string;
    contract: {
      contractant: {
        libelle: string;
      };
      formule: {
        libelle_formule: string;
      };
    };
    name: string;
  };
  actePS: {
    libelle_actePs: string;
    prestation: {
      prestation_libelle: string;
    };
    healthcareProfessional: {
      name: string;
      medicalSpecialty: string;
      // ... any other fields you may need
    };
  };
  // ... any additional fields or nested objects
}

interface GroupedData {
  healthcareProfessionalName?: string;
  medicalSpecialty?: string;
  ref?: string;
  adherantName?: string;
  adherantDeductible?: string;
  adherantEmail?: string;
  datePeriod?: string;
  count?: number;
  totalAmount: number;
  totalVisits: number;
}

@Component({
  selector: 'app-movements',
  templateUrl: './movement-table.component.html',
  styleUrls: ['./movement-table.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [DatePipe],
})
export class MovementTableComponent implements OnInit {
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  pageSizeOptions: number[] = [5, 10, 20, 50];
  searchTerm: string = '';
  groupedData: GroupedData[] = [];
  movements: MovementInerface[] = [];
  groupBy: 'date' | 'adherant' | 'healthcareProfessional' | 'none' = 'none';
  groupByDateOptions: string[] = ['quarter', 'month', 'year', 'day'];
  dateGrouping: string = 'quarter';
  sortField: string = 'date';
  sortDirection: 'asc' | 'desc' = 'asc';
  private searchSubject = new Subject<string>();

  constructor(
    private movementService: MovementService,
    private datePipe: DatePipe
  ) {
    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => {
        this.currentPage = 0;
        this.loadData();
      });
  }

  ngOnInit(): void {
    this.loadData();
  }

  changePageSize(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0;
    this.loadData();
  }

  nextPage(): void {
    if ((this.currentPage + 1) * this.pageSize < this.totalElements) {
      this.currentPage++;
      this.loadData();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadData();
    }
  }

  sortData(field: string): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.currentPage = 0;
    this.loadData();
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchTerm = value.trim();
    this.searchSubject.next(this.searchTerm);
  }

  public loadData(): void {
    if (this.groupBy === 'none') {
      this.loadMovements();
    } else {
      this.loadGroupedData();
    }
  }

  private loadGroupedData(): void {
    const sort = this.sortField
      ? `${this.sortField},${this.sortDirection}`
      : '';

    switch (this.groupBy) {
      case 'date':
        this.movementService
          .getGroupedByDate(
            this.currentPage,
            this.pageSize,
            this.dateGrouping,
            this.searchTerm,
            sort
          )
          .subscribe((data) => this.handleGroupedResponse(data));
        break;
      case 'adherant':
        this.movementService
          .getGroupedByAdherant(
            this.currentPage,
            this.pageSize,
            this.searchTerm,
            sort
          )
          .subscribe((data) => this.handleGroupedResponse(data));
        break;
      case 'healthcareProfessional':
        this.movementService
          .getGroupedByHealthcareProfessional(
            this.currentPage,
            this.pageSize,
            this.searchTerm,
            sort
          )
          .subscribe((data) => this.handleGroupedResponse(data));
        break;
    }
  }

  private loadMovements(): void {
    const sort = this.sortField
      ? `${this.sortField},${this.sortDirection}`
      : '';
    this.movementService
      .getMovements(this.currentPage, this.pageSize, this.searchTerm, sort)
      .subscribe((data) => this.handleMovementsResponse(data));
  }

  private handleGroupedResponse(response: any): void {
    this.groupedData = response.content;
    this.totalElements = response.totalElements;
  }

  private handleMovementsResponse(response: any): void {
    this.movements = response.content;
    console.log(response.content);
    this.totalElements = response.totalElements;
  }

  get displayRange(): string {
    const start = this.currentPage * this.pageSize + 1;
    const end = Math.min(
      (this.currentPage + 1) * this.pageSize,
      this.totalElements
    );
    return `${start} - ${end}`;
  }
}
