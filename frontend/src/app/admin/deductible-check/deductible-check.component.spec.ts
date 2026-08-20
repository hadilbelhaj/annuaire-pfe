import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeductibleCheckComponent } from './deductible-check.component';

describe('DeductibleCheckComponent', () => {
  let component: DeductibleCheckComponent;
  let fixture: ComponentFixture<DeductibleCheckComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeductibleCheckComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DeductibleCheckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
