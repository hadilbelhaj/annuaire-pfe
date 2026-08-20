import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovementStatsComponent } from './main-stats.component';

describe('MainStatsComponent', () => {
  let component: MovementStatsComponent;
  let fixture: ComponentFixture<MovementStatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovementStatsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MovementStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
