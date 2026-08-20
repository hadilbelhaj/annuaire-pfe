import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopProfessionalsComponent } from './top-professionals.component';

describe('TopProfessionalsComponent', () => {
  let component: TopProfessionalsComponent;
  let fixture: ComponentFixture<TopProfessionalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopProfessionalsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TopProfessionalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
