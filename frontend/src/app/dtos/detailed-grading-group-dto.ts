import {RegisterConstraint} from './register-constraint';

export interface DetailedGradingGroupDto {
  id: number;
  title: string;
  constraints: RegisterConstraint[];
}
