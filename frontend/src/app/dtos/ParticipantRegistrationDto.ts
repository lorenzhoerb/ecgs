export interface ParticipantRegistrationDto {
  userId: number;
  groupPreference?: number;
}

export interface ResponseParticipantRegistrationDto {
  competitionId: number;
  registeredParticipants: ParticipantRegistrationDto[];
}
