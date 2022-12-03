/* eslint-disable @typescript-eslint/naming-convention */

import { ILanguage, LocalizationKeys, SupportedLanguages } from './language';

export class Englisch implements ILanguage {
    language = SupportedLanguages.English;
    translations: LocalizationKeys = {
        Hello: 'Hello',
        HelloFunction: (count: number) => count === 1 ? `Hello, you are alone.` : `Hello, you are ${count} people`,
        login: 'Login',
        register: 'Register',
        SwitchLanguage: 'Switch Language'
    };
}
