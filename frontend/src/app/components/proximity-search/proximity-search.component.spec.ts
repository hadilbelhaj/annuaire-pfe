import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProximitySearchComponent } from './proximity-search.component';

describe('ProximitySearchComponent', () => {
  let component: ProximitySearchComponent;
  let fixture: ComponentFixture<ProximitySearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProximitySearchComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProximitySearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
