/* eslint-disable @typescript-eslint/naming-convention */

//Disable the lint because localization keys should not be forced to obey naming conventions

export enum SupportedLanguages {
    German = 'de-DE',
    English = 'en-US'
}

export class LocalizationKeys {
  Hello: string; // Demo for simple mapping
  HelloFunction: (count: number) => string; // Demo for complex mapping, e.g. can be used for singular/plural differentiation
  login: string;
  register: string;
  registered: string;
  registration: string;
  SwitchLanguage: string;
  MyTournaments: string;
  succRegComp: string;
  errRegComp: string;
  competition: string;
  groupPreference: string;
  noPreference: string;
  forgotPassword: string;
  sendResetLink: string;
  gradingGroup: string;

  edit: string;

  datesAndContacts: string;
  description: string;
  until: string;
  Tournamnet: string;
  contact: string;
  participants: string;

  title: string;
  phoneNumber: string;
  beginOfCompetition: string;
  endOfCompetition: string;
  beginOfRegistration: string;
  endOfRegistration: string;
  settings: string;
  publish: string;
  public: string;
  onlyClubs: string;
  save: string;

  time: string;
  beginAtOrAfter: string;
  registrationNotDone: string;
  search: string;

  all: string;
  accepted: string;
  outstanding: string;
  status: string;
  action: string;
  editStatus: string;
  editGradingGroup: string;
  manageParticipants: string;
  cancel: string;
  oopsSomethingWentWrong: string;

  logout: string;
  importCSV: string;
  page: string;
  exportCSVButton: string;
  teamName: string;
  member: string;
  importCSVButton: string;
  clearAll: string;
  genderMan: string;
  genderWoman: string;
  genderOther: string;

  createTournament: string;

  importFlags: string;
  flag: string;
  optional: string;

  username: string;
  firstname: string;
  lastname: string;
  password: string;
  changePassword: string;
  dateOfBirth: string;
  gender: string;
  type: string;
  enter: (type: string) => string;
  reEnter: (type: string) => string;
  getType: (type: string) => string;
  getGender: (gender: string) => string;

  isRequired: (thing: string) => string;
  isMalformed: (thing: string) => string;
}

export interface ILanguage {
  language: SupportedLanguages;
  translations: LocalizationKeys;
}
