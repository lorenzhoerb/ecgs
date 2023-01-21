import {SimpleFlagDto} from './simpleFlagDto';

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
  email: string;
  firstName: string;
  lastName: string;
  gender: Gender;
  dateOfBirth: Date;
  picturePath: string;
  flags: SimpleFlagDto[];
}

export class UserRegisterDetail {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  gender: Gender;
  dateOfBirth: Date;
  picturePath: string;
  flags: SimpleFlagDto[];
  gradingGroup?: number;
  active?: boolean;
  groupId?: number;
}
