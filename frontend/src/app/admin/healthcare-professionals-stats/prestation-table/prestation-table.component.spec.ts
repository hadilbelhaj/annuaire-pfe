import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestationTableComponent } from './prestation-table.component';

describe('PrestationTableComponent', () => {
  let component: PrestationTableComponent;
  let fixture: ComponentFixture<PrestationTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestationTableComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrestationTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
