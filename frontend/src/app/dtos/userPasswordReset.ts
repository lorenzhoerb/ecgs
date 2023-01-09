export class UserPasswordReset {
  constructor(
    public token: string,
    public password: string
  ) {}
}
