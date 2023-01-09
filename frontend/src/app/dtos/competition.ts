export class Competition {
  name: string;
  beginOfRegistration: Date;
  endOfRegistration: Date;
  endOfCompetition: Date;
  beginOfCompetition: Date;
  description: string;
  picturePath: string;
  isPublic: boolean;
  draft: boolean;
  email: string;
  phone: string;
}

export interface CalendarViewCompetition {
  id: number;
  name: string;
  beginOfCompetition: Date;
  endOfCompetition: Date;
  description: string;
  picturePath: string;
  isPublic: boolean;
}

export interface CalendarWeek {
    weekNumber: number;
    year: number;
}
