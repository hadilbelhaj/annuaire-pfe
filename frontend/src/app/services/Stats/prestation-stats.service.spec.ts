import { TestBed } from '@angular/core/testing';

import { PrestationStatsService } from './prestation-stats.service';

describe('PrestationStatsService', () => {
  let service: PrestationStatsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrestationStatsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
