import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReimbursementFraudComponent } from './reimbursement-fraud.component';

describe('ReimbursementFraudComponent', () => {
  let component: ReimbursementFraudComponent;
  let fixture: ComponentFixture<ReimbursementFraudComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReimbursementFraudComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ReimbursementFraudComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
