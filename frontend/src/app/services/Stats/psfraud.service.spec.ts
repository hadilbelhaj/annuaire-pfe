import { TestBed } from '@angular/core/testing';

import { PsfraudService } from './psfraud.service';

describe('PsfraudService', () => {
  let service: PsfraudService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PsfraudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
