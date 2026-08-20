import { TestBed } from '@angular/core/testing';

import { ProfessionalsPdfExportService } from './professionals-pdf-export.service';

describe('ProfessionalsPdfExportService', () => {
  let service: ProfessionalsPdfExportService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfessionalsPdfExportService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
