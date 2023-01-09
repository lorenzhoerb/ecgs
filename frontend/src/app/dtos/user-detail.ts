export enum Gender {
  male = 'MALE',
  female = 'FEMALE',
  other = 'OTHER'
}

export const genderMap: Map<Gender, string> = new Map<Gender, string>([
  [ Gender.male,   'männlich' ],
  [ Gender.female, 'weiblich' ],
  [ Gender.other,  'andere' ]
]);

export class UserDetail {
  id: number;
  firstName: string;
  lastName: string;
  gender: Gender;
  dateOfBirth: Date;
  picturePath: string;
}
