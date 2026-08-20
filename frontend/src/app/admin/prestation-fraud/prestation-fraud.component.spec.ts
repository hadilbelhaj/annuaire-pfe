import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestationFraudComponent } from './prestation-fraud.component';

describe('PrestationFraudComponent', () => {
  let component: PrestationFraudComponent;
  let fixture: ComponentFixture<PrestationFraudComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestationFraudComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrestationFraudComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
