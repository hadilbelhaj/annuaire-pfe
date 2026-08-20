import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrestationStatisticsComponent } from './prestation-statistics.component';

describe('PrestationStatisticsComponent', () => {
  let component: PrestationStatisticsComponent;
  let fixture: ComponentFixture<PrestationStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrestationStatisticsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PrestationStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
