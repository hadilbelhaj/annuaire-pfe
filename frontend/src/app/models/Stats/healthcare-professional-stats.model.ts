// src/app/models/healthcare-professional-stats.model.ts

export interface SpecialtyDistribution {
    specialtyName: string;
    count: number;
    percentage: number;
  }
  
  export interface RegionDistribution {
    regionName: string;
    count: number;
    percentage: number;
  }
  export interface TopProfessional {
    id: number;
    name: string;
    medicalSpecialty: string;
    region: string;
    visitCount: number;
    transactionCount: number;
    totalAmount: number;
    averageAmount: number;
  }
  
  export interface HealthcareProfessionalStats {
    specialtyDistribution: SpecialtyDistribution[];
    regionDistribution: RegionDistribution[];
    topByVisits: TopProfessional[];
    topByTransactionVolume: TopProfessional[];
    topByAverageAmount: TopProfessional[];
  }