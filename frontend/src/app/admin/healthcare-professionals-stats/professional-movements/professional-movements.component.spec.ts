import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessionalMovementsComponent } from './professional-movements.component';

describe('ProfessionalMovementsComponent', () => {
  let component: ProfessionalMovementsComponent;
  let fixture: ComponentFixture<ProfessionalMovementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfessionalMovementsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProfessionalMovementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
