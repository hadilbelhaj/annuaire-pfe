import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestationChartComponent } from './prestation-chart.component';

describe('PrestationChartComponent', () => {
  let component: PrestationChartComponent;
  let fixture: ComponentFixture<PrestationChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestationChartComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrestationChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
