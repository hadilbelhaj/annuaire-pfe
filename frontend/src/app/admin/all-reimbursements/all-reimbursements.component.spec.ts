import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllReimbursementsComponent } from './all-reimbursements.component';

describe('AllReimbursementsComponent', () => {
  let component: AllReimbursementsComponent;
  let fixture: ComponentFixture<AllReimbursementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllReimbursementsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllReimbursementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
