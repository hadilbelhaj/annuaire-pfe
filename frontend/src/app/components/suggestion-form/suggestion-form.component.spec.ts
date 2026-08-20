import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuggestionFormComponent } from './suggestion-form.component';

describe('SuggestionFormComponent', () => {
  let component: SuggestionFormComponent;
  let fixture: ComponentFixture<SuggestionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuggestionFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SuggestionFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
