export interface SimpleCompetitionListEntryDto {
    id: number;
    name: string;
    description?: string;
    beginOfCompetition: Date;
    endOfCompetition: Date;
    beginOfRegistration: Date;
    endOfRegistration: Date;
    phone?: string;
    email?: string;
}
