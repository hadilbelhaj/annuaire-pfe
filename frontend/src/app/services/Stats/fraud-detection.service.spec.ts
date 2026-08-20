import { TestBed } from '@angular/core/testing';

import { FraudDetectionService } from './fraud-detection.service';

describe('FraudDetectionService', () => {
  let service: FraudDetectionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FraudDetectionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
