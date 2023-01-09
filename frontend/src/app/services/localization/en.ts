/* eslint-disable @typescript-eslint/naming-convention */

import {ILanguage, LocalizationKeys, SupportedLanguages} from './language';

export class Englisch implements ILanguage {
  language = SupportedLanguages.English;
  translations: LocalizationKeys = {
    Hello: 'Hello',
    HelloFunction: (count: number) => count === 1 ? `Hello, you are alone.` : `Hello, you are ${count} people`,
    login: 'Login',
    register: 'Register',
    registered: 'Registered',
    SwitchLanguage: 'Switch Language',
    MyTournaments: 'My Tournaments',
    succRegComp: 'Sucessfully registered',
    errRegComp: 'Registration failed',
    registration: 'Registration',
    competition: 'Competition',
    groupPreference: 'Group Preference',
    noPreference: 'No Preference',
    forgotPassword: 'Forgot Password',
    sendResetLink: 'Send reset link',

    edit: 'Edit',

    datesAndContacts: 'Dates & Contacts',
    description: 'Description',
    until: 'until',
    Tournamnet: 'Competition',
    contact: 'Contact',
    participants: 'Participants',

    title: 'Title',
    phoneNumber: 'Phone number',
    beginOfCompetition: 'Begin of competition',
    endOfCompetition: 'End of competition',
    beginOfRegistration: 'Begin of registration',
    endOfRegistration: 'End of registration',
    settings: 'Settings',
    publish: 'Publish',
    public: 'Public',
    onlyClubs: 'Clubs only',
    save: 'Save',

    time: 'Date',
    beginAtOrAfter: 'Begins at / after',
    registrationNotDone: 'Registration not closed',
    search: 'Search',

    logout: 'Logout',
    importCSV: 'New Team Members',
    page: 'Page',
    member: 'Member',
    clearAll: 'Clear all',
    genderMan: 'Man',
    genderWoman: 'Woman',
    genderOther: 'Other',
    importCSVButton: 'Import as CSV',
    exportCSVButton: 'Export as CSV',
    teamName: 'Team name',
    createTournament: 'New Competition',


    username: 'Username / Email',
    firstname: 'Firstname',
    lastname: 'Lastname',
    password: 'Password',
    changePassword: 'Change password',
    dateOfBirth: 'Date of Birth',
    gender: `Gender`,
    type: `User Role`,
    enter: (type: string) => `Enter ${type}...`,
    reEnter: (type: string) => `Repeat ${type}...`,
    getType: (type: string) => ({
      Participant: 'Participant',
      ClubManager: 'Club Manager',
      CompetitionManager: 'Competition Manager',
    }[type]),
    getGender: (gender: string) => ({
      Female: 'Female',
      Male: 'Male',
      Other: 'Other',
    }[gender]),

    isRequired: (thing) => `Your ${thing} is required!`,
    isMalformed: (thing) => `Your ${thing} is malformed!`
  };
}
