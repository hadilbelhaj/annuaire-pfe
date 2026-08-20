export interface Professional {
  id?: number;
  name: string;
  medicalSpecialty: string;
  number1: string;
  number2: string;
  mail: string;
  address: string;
  additionalAttributes: any;
  region: string | null;
  conventionne: number;
  ref: string | null;
  numFiscal: string | null;
  NumeroOrdre: string;
  deleted: number;
  latitude?: number;
  longitude?: number;
  location?: any;
}

export interface ProfessionalRequest {
  healthcareProfessional: Professional;
  prestationLabels: string[];
}