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
    endOfRegistration: 'End of registration',
    groupPreference: 'Group Preference',
    noPreference: 'No Preference'
  };
}
