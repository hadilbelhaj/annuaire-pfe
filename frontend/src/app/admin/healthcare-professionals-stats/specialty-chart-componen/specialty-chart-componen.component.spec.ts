import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialtyChartComponenComponent } from './specialty-chart-componen.component';

describe('SpecialtyChartComponenComponent', () => {
  let component: SpecialtyChartComponenComponent;
  let fixture: ComponentFixture<SpecialtyChartComponenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecialtyChartComponenComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SpecialtyChartComponenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
