import { TestBed } from '@angular/core/testing';

import { ProximitySearchService } from './proximity-search.service';

describe('ProximitySearchService', () => {
  let service: ProximitySearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProximitySearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
