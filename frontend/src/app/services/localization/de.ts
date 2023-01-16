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
    groupPreference: 'Bevorzugte Gruppe',
    noPreference: 'keine Preferänz',
    forgotPassword: 'Passwort vergessen',
    sendResetLink: 'Sende Link zum zurücksetzen',
    gradingGroup: 'Bewertungsgruppe',

    edit: 'Bearbeiten',

    datesAndContacts:'Daten und Kontakt',
    description:'Beschreibung',
    until:'bis',
    Tournamnet:'Wettkampf',
    contact:'Kontakt',
    participants: 'Teilnehmer',

    title: 'Titel',
    phoneNumber:'Telefonnummer',
    beginOfCompetition:'Wettkampf Start',
    endOfCompetition:'Wettkampf Ende',
    beginOfRegistration: 'Anmeldung offen ab',
    endOfRegistration: 'Anmeldeschluss',
    settings: 'Einstellungen',
    publish: 'Veröffentlichen',
    public: 'Öffentlich',
    onlyClubs: 'Nur Vereine',
    save: 'Speichern',

    time: 'Veranstaltungszeit',
    beginAtOrAfter: 'Austragungsdatum ab',
    registrationNotDone: 'Registrierung nicht abgeschlossen',
    search: 'Suchen',

    all: 'Alle',
    accepted: 'Akzeptiert',
    outstanding: 'Ausstehend',
    status: 'Status',
    action: 'Aktion',
    editStatus: 'Status ändern',
    editGradingGroup: 'Grading Group ändern',
    manageParticipants: 'Teilnehmer Verwalten',
    cancel: 'Abbrechen',
    oopsSomethingWentWrong: 'Ups, da ist wohl was schief gelaufen.',

    logout: 'Abmelden',
    importCSV: 'Neue Teammitglieder',
    teamName: 'Teamname',
    page: 'Seite',
    member: 'Teammitglied',
    clearAll: 'Alle löschen',
    genderMan: 'Herr',
    genderWoman: 'Frau',
    genderOther: 'Anders',
    importCSVButton: 'Importieren als CSV',
    exportCSVButton: 'Exportieren als CSV',
    createTournament: 'Neuer Wettkampf',

    importFlags: 'Flags zuweisen',
    flag: 'Flag',
    optional: 'optional',

    username: 'Benutzername / Email',
    firstname: 'Vorname',
    lastname: 'Nachname',
    password: 'Passwort',
    changePassword: 'Passwort ändern',
    dateOfBirth: 'Geburtstag',
    gender: `Geschlecht`,
    type: `Rolle`,
    enter: (type: string) => `${type} eingeben...`,
    reEnter: (type: string) => `${type} wiederholen...`,
    getType: (type: string) => ({
        Participant: 'Teilnehmer:in',
        ClubManager: 'Vereinsverantwortliche:r',
        CompetitionManager: 'Turnier Manager:in',
      }[type]),
    getGender: (gender: string) => ({
        Female: 'Weiblich',
        Male: 'Männlich',
        Other: 'Divers',
      }[gender]),

    isRequired: (thing) => `${thing} wird benötigt!`,
    isMalformed: (thing) => `${thing} ist nicht gültige!`,
  };
}
