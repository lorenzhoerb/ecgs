/* eslint-disable @typescript-eslint/naming-convention */

import {ILanguage, LocalizationKeys, SupportedLanguages} from './language';

export class German implements ILanguage {
  language = SupportedLanguages.German;
  translations: LocalizationKeys = {
    Hello: 'Hallo',
    HelloFunction: (count: number) => count === 1 ? `Hallo, du bist alleine.` : `Hallo, ihr seid ${count} Personen`,
    login: 'Einloggen',
    register: 'Registrieren',
    registered: 'Registriert',
    SwitchLanguage: 'Sprache wechseln',
    MyTournaments: 'Meine Turniere',
    succRegComp: 'Anmeldung erfolgreich',
    errRegComp: 'Anmeldung fehlgeschlagen',
    registration: 'Anmeldung',
    competition: 'Wettkampf',
    endOfRegistration: 'Anmeldeschluss',
    groupPreference: 'Bevorzugte Gruppe',
    noPreference: 'keine Preferänz'
  };
}
