import {GradingSystemDetailDto} from './gradingSystemDetailDto';
import {ParticipantDetailDto} from './participantDetailDto';

export class GradingGroupWithRegisterToDto{
  id: number;
  title: string;
  gradingSystem: GradingSystemDetailDto;
  registrations: ParticipantDetailDto[];
}
