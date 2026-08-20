import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExceededDeductiblesComponent } from './exceeded-deductibles.component';

describe('ExceededDeductiblesComponent', () => {
  let component: ExceededDeductiblesComponent;
  let fixture: ComponentFixture<ExceededDeductiblesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExceededDeductiblesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExceededDeductiblesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
