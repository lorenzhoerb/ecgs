export class AuthRequest {
  constructor(
    public email: string,
    public password: string
  ) {}
}

export enum Role {
  participant = 'PARTICIPANT',
  clubManager = 'CLUB_MANAGER',
  competitionManager = 'TOURNAMENT_MANAGER'
}

export type Gender = 'MALE' | 'FEMALE' |'OTHER';


export class RegisterRequest {
  constructor(
    public email: string,
    public password: string,
    public firstName: string,
    public lastName: string,
    public gender: Gender,
    public dateOfBirth: Date,
    public type: Role,
    public teamName?: string,
  ) {}
}

