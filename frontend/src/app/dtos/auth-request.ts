export class AuthRequest {
  constructor(
    public email: string,
    public password: string
  ) {}
}

export enum Role {
  participant = 'Participant',
  clubManager = 'ClubManager',
  tournamentManager = 'TournamentManager'
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

