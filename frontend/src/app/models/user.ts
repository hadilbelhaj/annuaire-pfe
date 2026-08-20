export class User {
  
  constructor(
    public email: string,
    public password: string,
    public role?: string,
    public firstName?: string,
    public lastName?: string,
    public address?: string,
    public id?: number,
    public deleted?:number, 
  ) {}
  
}

export enum Role {
'USER',
  'ADMIN',
 'super-admin'
}

  
  
