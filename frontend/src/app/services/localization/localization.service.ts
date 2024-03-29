import { registerLocaleData } from '@angular/common';
import {German} from './de';
import {Englisch} from './en';
import {ILanguage, LocalizationKeys, SupportedLanguages} from './language';
import localeDe from '@angular/common/locales/de';
import localeDeExtra from '@angular/common/locales/extra/de';

export class LocalizeService extends LocalizationKeys {
    private language: SupportedLanguages;
    private locals: ILanguage;

    constructor() {
        super();
        //const language = navigator.language;
        registerLocaleData(localeDe, 'de-DE', localeDeExtra);
        this.changeLanguageInternal('de-DE');

    }

    public localize(key: string, ...params: any[]) {
        if (key === undefined || key === null || key === '') {
            return '';
        }

        const result = this.locals.translations[key];

        if (result === undefined || result === null || (typeof result === 'function' && params.length <= 0)) {
            return key;
        }

        if (params.length > 0 && typeof result === 'function') {
            return result(...params);
        }

        return result;
    }

    public changeLanguage(newLanguage: SupportedLanguages) {
        if (newLanguage === this.language) {
            return;
        }

        this.changeLanguageInternal(newLanguage);
    }

    public getLanguage(): string {
        return this.language;
    }

    private changeLanguageInternal(newLanguage: string) {
        switch (newLanguage) {
            case SupportedLanguages.German:
                this.language = newLanguage;
                this.locals = new German();
                break;
            case SupportedLanguages.English:
                this.language = newLanguage;
                this.locals = new Englisch();
                break;
            default:
                console.log(`Unsupported Language: ${newLanguage}; defaults to German`);
                this.language = SupportedLanguages.German;
                this.locals = new German();
                break;
        }
        //Disable the lint because the for loop is carefully constructed and perfectly correct the way it is
        // eslint-disable-next-line
        for (const key in this.locals.translations) {
            this[key] = this.locals.translations[key];
        }
    }
}

const localizationService = new LocalizeService();
export default localizationService;
