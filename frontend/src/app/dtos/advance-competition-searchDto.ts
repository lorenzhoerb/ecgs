export interface AdvanceCompetitionSearchDto {
  name?: string;
  endOfRegistrationBefore?: string;
  endOfRegistrationAfter?: string;
  beginOfRegistrationBefore?: string;
  beginOfRegistrationAfter?: string;
  endOfCompetitionBefore?: string;
  endOfCompetitionAfter?: string;
  beginOfCompetitionBefore?: string;
  beginOfCompetitionAfter?: string;
  isPublic?: boolean;
  isRegistrationOpen?: boolean;
  size?: number;
  page?: number;
}
