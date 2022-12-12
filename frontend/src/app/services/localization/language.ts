/* eslint-disable @typescript-eslint/naming-convention */

//Disable the lint because localization keys should not be forced to obey naming conventions

export enum SupportedLanguages {
    German = 'de-De',
    English = 'en-US'
}

export class LocalizationKeys {
    Hello: string; // Demo for simple mapping
    HelloFunction: (count: number) => string; // Demo for complex mapping, e.g. can be used for singular/plural differentiation
    login: string;
    register: string;
    SwitchLanguage: string;
    MyTournaments: string;
}

export interface ILanguage {
    language: SupportedLanguages;
    translations: LocalizationKeys;
}
