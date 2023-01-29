export interface DownloadReportRequestDto {
    gradingGroupsIds: number[];
    inclusionRule: DownloadReportRequestInclusionRule;
}

export enum DownloadReportRequestInclusionRule {
    onlyYou, onlyYourTeam, allParticipants
}
