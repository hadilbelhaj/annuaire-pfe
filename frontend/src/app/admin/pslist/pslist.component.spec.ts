import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PslistComponent } from './pslist.component';

describe('PslistComponent', () => {
  let component: PslistComponent;
  let fixture: ComponentFixture<PslistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PslistComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PslistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
