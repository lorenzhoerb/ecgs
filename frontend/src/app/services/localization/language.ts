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
  registerToAGroup: string;
  noPreference: string;
  forgotPassword: string;
  sendResetLink: string;
  gradingGroup: string;

  passwordResetLinkSent: string;
  enterValidMailForReset: string;

  edit: string;
  selectAGroup: string;
  groupHasRegistrationReq: string;
  showGroupInformation: string;
  assignAll: string;
  groups: string;

  calculateCompetitionResults: string;

  datesAndContacts: string;
  description: string;
  until: string;
  Tournamnet: string;
  contact: string;
  participants: string;
  managedParticipants: string;

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

  meanStrategy: string;
  equalStrategy: string;

  judge: string;

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
  exportCSVButton: string;
  teamName: string;
  member: string;
  importCSVButton: string;
  page: string;
  clearAll: string;
  genderMan: string;
  genderWoman: string;
  genderOther: string;
  import: string;
  team: string;

  createTournament: string;
  myResults: string;
  myGradingSystems: string;

  calendar: string;

  importFlags: string;
  flag: string;
  optional: string;
  add: string;
  delete: string;
  new: string;
  create: string;
  createFlag: string;

  close: string;
  formula: string;
  chooseGradingGroup: string;
  importGradingGroup: string;
  createGradingSystem: string;
  others: string;
  publicType: string;
  publicTypeAll: string;
  publicTypePrivate: string;
  publicTypePublic: string;
  reset: string;
  onlyEditables: string;
  yes: string;
  no: string;

  gradingSystems: string;
  manageTemplateGradingSystems: string;

  username: string;
  firstname: string;
  lastname: string;
  password: string;
  repeatPassword: string;
  passwordMinLength8: string;
  userSettings: string;
  filePictureRestrictions: string;
  uploadCompetitionPictureDisclaimer: string;
  clearFileUpload: string;
  changePassword: string;
  resetPassword: string;
  resetPasswordSuccess: string;
  resetPasswordError: string;
  resetPasswordErrorLoggedIn: string;
  resetPasswordErrorMalformedToken: string;
  passwordsMustMatch: string;
  changeUserPicture: string;
  uploadUserPicture: string;
  dateOfBirth: string;
  gender: string;
  type: string;
  showRegistrationRequirements: string;
  addAnotherCondition: string;
  manageRegistrationConstraints: string;
  registerConstraints: string;
  noConstraints: string;
  constraints: string;
  everybodyCanRegisterToThisGroup: string;
  age: string;
  equals: string;
  notEquals: string;
  greaterThan: string;
  greaterOrEqualsThan: string;
  lessThan: string;
  lessOrEqualsThan: string;
  bornBefore: string;
  bornAfter: string;




  regMemToComp: string;
  searchAndSelectToRegister: string;
  registrationEnds: string;
  members: string;
  membersPerPage: string;
  searchACompetition: string;
  back: string;

  editTeam: string;
  addRemoveFlags: string;

  liveResults: string;

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
