import {Gender} from './user-detail';
export interface UserDetailFilterDto {
  firstName?: string;
  lastName?: string;
  dateOfBirth?: Date;
  gender?: Gender;
  size?: number;
  page?: number;
  flagId?: number;
}
