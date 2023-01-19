import {SimpleFlagDto} from './simpleFlagDto';
import {UserDetail} from './user-detail';

export class UserDetailSetFlagDto {
  flag: SimpleFlagDto;
  users: UserDetail[];
}
