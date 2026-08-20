import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { ProfessionalService } from '../../services/professional.service';
import { Professional } from '../../models/professional.modal';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-pslist',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,FormsModule],
  templateUrl: './pslist.component.html',
  styleUrls: ['./pslist.component.css']
})
export class PslistComponent implements OnInit, OnDestroy {
  public Math = Math;
  professionals: Professional[] = [];
  searchTerm: string = '';
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalItems = 0;
  statusFilter: string = 'all'; 
  specialties: string[] = [];
  availablePrestations: string[] = [];
  selectedPrestations: string[] = [];
  loadingPrestations = false;

  
  professionalForm!: FormGroup;
  showModal: boolean = false;
  isEditing: boolean = false;
  currentProfessional: Professional = this.getEmptyProfessional();
  
  showConfirmationModal: boolean = false;
  confirmationTitle: string = '';
  confirmationMessage: string = '';
  confirmationType: 'delete' | 'restore' = 'delete';
  professionalToAction: Professional | null = null;
  
  private searchSubscription?: Subscription;
  private searchSubject = new Subject<string>();
  @Output() totalItemsChange = new EventEmitter<number>();

  constructor(
    private professionalService: ProfessionalService, 
    private router: Router,
    private fb: FormBuilder,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProfessionals();
    this.loadUniqueSpecialties();
    this.subscribeToSearchResults();
    this.searchSubject.pipe(
      debounceTime(300), 
      distinctUntilChanged() 
    ).subscribe(() => {
      this.onSearch();
    });
  }

  initForm(): void {
    this.professionalForm = this.fb.group({
      name: ['', Validators.required],
      medicalSpecialty: ['', Validators.required],
      region: ['', Validators.required],
      mail: ['', Validators.email],
      address: [''],
      numFiscal: ['', Validators.required],
      number1: [''],
      number2: [''],
      NumeroOrdre: ['', Validators.required],
      ref: ['', Validators.required],
      conventionne: [0]
    });
  }
  loadUniqueSpecialties(): void {
    this.professionalService.getUniqueSpecialties().subscribe({
      next: (specialties: string[]) => {
        this.specialties = specialties;
        console.log('Unique Specialties:', specialties);
      },
      error: (err) => {
        console.error('Failed to load specialties', err);
      }
    });
  }
  

  ngOnDestroy(): void {
    this.searchSubscription?.unsubscribe();
  }
  
  onSearchInput() {
    this.searchSubject.next(this.searchTerm);
  }

  loadProfessionals(): void {
    this.professionalService.getPaginated(this.currentPage, this.pageSize, this.searchTerm, this.statusFilter)
      .subscribe(result => {
        this.professionals = result.content;
        this.totalPages = result.totalPages;
        this.totalItems = result.totalElements;
        this.totalItemsChange.emit(this.totalItems);
      });
  }
  
  onSearch() {
    this.currentPage = 0; 
    this.loadProfessionals();
  }

  subscribeToSearchResults() {
    this.searchSubscription = this.professionalService.professionals$.subscribe(result => {
      this.professionals = result.content;
      this.totalPages = result.totalPages;
      this.totalItems = result.totalElements;
    });
  }

  onPageSizeChange(event: Event) {
    this.pageSize = +(event.target as HTMLSelectElement).value;
    this.currentPage = 0; // Reset to first page
    this.loadProfessionals();
  }

  onStatusFilterChange() {
    this.currentPage = 0; 
    this.loadProfessionals();
  }

  nextPage() {
    if (this.currentPage + 1 < this.totalPages) {
      this.currentPage++;
      this.loadProfessionals();
    }
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadProfessionals();
    }
  }
  
  onProfessionalClick(professional: Professional): void {
    this.router.navigate(['/admin/healthcareprof', professional.id, 'movements']);
  }
  
  openAddModal(): void {
    this.isEditing = false;
    this.professionalForm.reset(this.getEmptyProfessional());
    this.showModal = true;
  }
  
  editProfessional(prof: any): void {
    this.isEditing = true;
    this.currentProfessional = prof;
    
    // Set form values
    this.professionalForm.patchValue({
      name: prof.name,
      medicalSpecialty: prof.medicalSpecialty,
      region: prof.region,
      mail: prof.mail,
      address: prof.address,
      numFiscal: prof.numFiscal,
      number1: prof.number1,
      number2: prof.number2,
      NumeroOrdre: prof.NumeroOrdre,
      ref: prof.ref,
      conventionne: prof.conventionne || 0
    });
    this.loadExistingPrestations(prof.id);
    this.loadingPrestations = true;
    this.http.get<any>(`http://localhost:5000/predict?specialty=${prof.medicalSpecialty}`).subscribe(
      (data) => {
        this.availablePrestations = data.suggestions || [];
        this.loadingPrestations = false;
      },
      (error) => {
        console.error('Error loading prestations:', error);
        this.loadingPrestations = false;
        this.availablePrestations = [];
      }
    );
    
    this.showModal = true;
  }

  
  closeModal(): void {
    this.showModal = false;
    this.professionalForm.reset();
  }
  
  saveProfessional(): void {
    if (this.professionalForm.invalid) {
      Object.keys(this.professionalForm.controls).forEach(key => {
        const control = this.professionalForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
  
    const professionalData: Professional = {
      ...this.professionalForm.value,
      deleted: 0
    };
  
    if (this.isEditing) {
      professionalData.id = this.currentProfessional.id;
      professionalData.deleted = this.currentProfessional.deleted;
      
      this.professionalService.updateProfessional(
        professionalData.id!, 
        professionalData,
        this.selectedPrestations
      ).subscribe(() => {
        this.closeModal();
        this.loadProfessionals();
      });
    } else {
      this.professionalService.createProfessional(
        professionalData,
        this.selectedPrestations
      ).subscribe(() => {
        this.closeModal();
        this.loadProfessionals();
      });
    }
  }
  
  isFieldInvalid(fieldName: string): boolean {
    const field = this.professionalForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }
  
  deleteProfessional(professional: Professional): void {
    this.professionalToAction = professional;
    this.confirmationType = 'delete';
    this.confirmationTitle = 'Deactivate Professional';
    this.confirmationMessage = `Are you sure you want to deactivate "${professional.name}"? This will make them inactive.`;
    this.showConfirmationModal = true;
  }
  
  restoreProfessional(professional: Professional): void {
    this.professionalToAction = professional;
    this.confirmationType = 'restore';
    this.confirmationTitle = 'Restore Professional';
    this.confirmationMessage = `Are you sure you want to restore "${professional.name}"? This will make them active again.`;
    this.showConfirmationModal = true;
  }
  
  cancelConfirmation(): void {
    this.showConfirmationModal = false;
    this.professionalToAction = null;
  }
  
  confirmAction(): void {
    if (!this.professionalToAction || !this.professionalToAction.id) return;
    
    if (this.confirmationType === 'delete') {
      this.professionalService.deleteProfessional(this.professionalToAction.id)
        .subscribe(() => {
          this.showConfirmationModal = false;
          this.professionalToAction = null;
          this.loadProfessionals();
        });
    } else {
      this.professionalService.restoreProfessional(this.professionalToAction.id)
        .subscribe(() => {
          this.showConfirmationModal = false;
          this.professionalToAction = null;
          this.loadProfessionals();
        });
    }
  }
  
  isDeleted(professional: Professional): boolean {
    return professional.deleted === 1;
  }
  
  getEmptyProfessional(): Professional {
    return {
      name: '',
      medicalSpecialty: '',
      number1: '',
      number2: '',
      mail: '',
      address: '',
      additionalAttributes: null,
      region: null,
      conventionne: 0,
      ref: "",
      numFiscal: "",
      NumeroOrdre: '',
      deleted: 0
    };
  }
  onSpecialtyChange(event: any): void {
    const specialty = event.target.value;
    if (!specialty) return;
    
    this.loadingPrestations = true;
    this.availablePrestations = [];
    this.http.get<any>(`http://localhost:5000/predict?specialty=${specialty}`).subscribe(
      (data) => {
        this.availablePrestations = data.suggestions || [];
        this.loadingPrestations = false;
        
        // If editing, we don't change the selected prestations
        // If creating new, we pre-select all available prestations
        if (!this.isEditing) {
          this.selectedPrestations = [...this.availablePrestations];
        }
      },
      (error) => {
        console.error('Error loading prestations:', error);
        this.loadingPrestations = false;
        this.availablePrestations = [];
      }
    );
  }
  togglePrestation(prestation: string, event: any): void {
    const isChecked = event.target.checked;
    
    if (isChecked && !this.selectedPrestations.includes(prestation)) {
      this.selectedPrestations.push(prestation);
    } else if (!isChecked && this.selectedPrestations.includes(prestation)) {
      this.selectedPrestations = this.selectedPrestations.filter(p => p !== prestation);
    }
  }

  loadExistingPrestations(professionalId: number): void {
    this.http.get<string[]>(`http://localhost:8090/api/ps/prestations/${professionalId}`).subscribe(
      (existingPrestations) => {
        // Set the selected prestations to the ones already associated with this professional
        this.selectedPrestations = [...existingPrestations];
        console.log('Loaded existing prestations:', this.selectedPrestations);
      },
      (error) => {
        console.error('Error loading existing prestations:', error);
        this.selectedPrestations = [];
      }
    );
  }
}