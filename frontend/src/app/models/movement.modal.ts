import { Adherant } from './adherant.modal';
import { Professional } from './professional.modal';

export class Movement {
  constructor(
    public id: number,
    public date: Date,
    public amount: number,
    public description: string,
    public adherant: Adherant,
    public healthcareProfessional: Professional,
    public total: number
  ) {}
}
