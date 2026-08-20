import { TestBed } from '@angular/core/testing';

import { ReimbursementFraudService } from './reimbursement-fraud.service';

describe('ReimbursementFraudService', () => {
  let service: ReimbursementFraudService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReimbursementFraudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
