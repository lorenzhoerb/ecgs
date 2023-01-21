export class GradeDto {
    uuid: string;
    judgeId: number;
    participantId: number;
    competitionId: number;
    gradingGroupId: number;
    stationId: number;
    grade: string;
    isValid?: boolean;
    result?: number;
}
