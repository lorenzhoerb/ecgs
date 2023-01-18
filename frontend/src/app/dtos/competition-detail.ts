import {GradingGroupDetail} from './grading-group-detail';
import {UserDetail} from './user-detail';

export class CompetitionDetail {
  id: number;
  name: string;
  description: string;
  beginOfRegistration: string;
  endOfRegistration: string;
  beginOfCompetition: string;
  endOfCompetition: string;
  public: boolean;
  draft: boolean;
  email: string;
  phone: string;
  gradingGroups: GradingGroupDetail[];
  judges: UserDetail[];
}
