import { TestBed } from '@angular/core/testing';

import { HealthcareprofessionalstatsService } from './healthcareprofessionalstats.service';

describe('HealthcareprofessionalstatsService', () => {
  let service: HealthcareprofessionalstatsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HealthcareprofessionalstatsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
