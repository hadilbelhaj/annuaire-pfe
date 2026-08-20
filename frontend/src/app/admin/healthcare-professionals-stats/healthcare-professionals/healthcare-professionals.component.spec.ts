import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthcareProfessionalsComponent } from './healthcare-professionals.component';

describe('HealthcareProfessionalsComponent', () => {
  let component: HealthcareProfessionalsComponent;
  let fixture: ComponentFixture<HealthcareProfessionalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HealthcareProfessionalsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HealthcareProfessionalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
