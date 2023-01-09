import {Gender} from './auth-request';

export interface ClubManagerTeamImportDto {
    teamName: string;
    teamMembers: ClubManagerTeamMemberImportDto[];
}

export interface ClubManagerTeamMemberImportDto {
    firstName: string;
    lastName: string;
    gender: Gender;
    dateOfBirth: string;// Date;
    email: string;
}
