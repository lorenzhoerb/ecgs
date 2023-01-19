import {SimpleCompetitionListEntryDto} from './simpleCompetitionListEntryDto';

export type SimpleCompetitionListDto = SimpleCompetitionListEntryDto[];



export interface Station {
  id: number;
  displayName: string;
  variables: {
    id: number;
    displayName: string;
    minJudgeCount: number;
    strategy: {
      type: string; // "" | ""
    };
  }[];
  idCount: number;
  formula: {
    typeHint: string;
    left: {
      typeHint: string;
      value: any;
    };
    right: {
      typeHint: string;
      value: any;
    };
};
};

export interface ParticipantResultDTO {
  participantId: number;
  gradingGroups: string |
  [
    {
      gradingGroupId: number;
      stations: StationResults[];
    }
  ];
}

export interface ParticipantResult {
  participantId: number;
  gradingGroups: [
    {
      gradingGroupId: number;
      stations: StationResults[];
    }
  ];
}

export interface StationResults {
  stationId: number;
  variables: [
    {
      id: number;
      value: any;
    }?
  ];
}


export interface ParticipantPartResult {
  participantId: number;
  result: StationResults;
}


export interface ParticipantPartGroupResult {
  participantId: number;
  gradingGroupId: number;
  result: StationResults;
}





