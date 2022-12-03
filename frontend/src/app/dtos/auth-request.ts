export class AuthRequest {
  constructor(
    public email: string,
    public password: string
  ) {}
}

export enum Role {
  participant = 'PARTICIPANT',
  clubManager = 'CLUB_MANAGER',
  tournamentManager = 'TOURNAMENT_MANAGER'
}


export class RegisterRequest {
  constructor(
    public email: string,
    public password: string,
    public firstName: string,
    public lastName: string,
    public gender: 'MALE' | 'FEMALE' |'OTHER',
    public dateOfBirth: Date,
    public type: Role
  ) {}
}

