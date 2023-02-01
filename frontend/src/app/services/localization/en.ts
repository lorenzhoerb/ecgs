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
    calculateCompetitionResults: 'Calculate final results',

    datesAndContacts: 'Dates & Contacts',
    description: 'Description',
    until: 'until',
    Tournamnet: 'Competition',
    contact: 'Contact',
    participants: 'Participants',
    managedParticipants: 'Participants managed by you',

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

    meanStrategy: 'Mean',
    equalStrategy: 'Equal',

    judge: 'Judge',

    time: 'Date',
    beginAtOrAfter: 'Begins at / after',
    registrationNotDone: 'Registration possible',
    search: 'Search',
    all: 'All',
    accepted: 'Accepted',
    outstanding: 'Outstanding',
    status: 'Status',
    action: 'Action',
    editStatus: 'Edit Status',
    editGradingGroup: 'Edit Grading Group',
    manageParticipants: 'Manage Participants',
    cancel: 'Cancel',
    oopsSomethingWentWrong: 'Oops, something went wrong.',

    logout: 'Logout',
    importCSV: 'New Team Members',
    member: 'Member',
    clearAll: 'Clear all',
    genderMan: 'Male',
    genderWoman: 'Female',
    genderOther: 'Other',
    importCSVButton: 'Import as CSV',
    exportCSVButton: 'Export as CSV',
    teamName: 'Team name',
    page: 'Page',
    import: 'Import',
    team: 'Team',

    createTournament: 'New Competition',
    myResults: 'My Results',
    myGradingSystems: 'Manage Gradingsystems',
    calendar: 'Calendar',

    importFlags: 'Assign flags',
    flag: 'Flag',
    optional: 'optional',
    add: 'add',
    delete: 'remove',
    new: 'new',
    create: 'create',
    createFlag: 'create Flag',
    selectAGroup: 'Select a Group',
    groupHasRegistrationReq: 'This group has registration requirements',
    showGroupInformation: 'Show group information',
    assignAll: 'Assign all',
    groups: 'Groups',

    registerToAGroup: 'Register to a group',
    close: 'Close',
    formula: 'Formula',
    chooseGradingGroup: 'Choose Grading Group',
    importGradingGroup: 'Choose Grading Group from Templates',
    createGradingSystem: 'Create Grading System',
    onlyEditables: 'Only editable',
    yes: 'Yes',
    no: 'No',
    publicTypeAll: 'Public & Private',
    publicTypePrivate: 'Private',
    publicTypePublic: 'Public',
    others: 'Miscellaneous',
    publicType: 'Type',
    gradingGroup: 'Grading Group',
    gradingSystems: 'Grading Systems',
    manageTemplateGradingSystems: 'Manage Grading Systems',
    reset: 'Reset changes',
    passwordResetLinkSent: 'A password reset link has been sent to your e-mail adress.',
    enterValidMailForReset: 'Please enter a valid e-mail adress.',

    username: 'Username / Email',
    firstname: 'Firstname',
    lastname: 'Lastname',
    password: 'Password',
    repeatPassword: 'Repeat Password',
    passwordMinLength8: 'Your password needs to have at least 8 characters!',
    userSettings: 'Settings',
    filePictureRestrictions: 'Only .png, .jpeg or .jpg, aswell as <= 1 Megabyte',
    uploadCompetitionPictureDisclaimer: 'Here you can upload a picture for your competition.',
    clearFileUpload: 'Choose file again',
    changePassword: 'Change password',
    resetPassword: 'Reset password',
    resetPasswordSuccess: 'Password successfully reset!',
    resetPasswordError: 'No user with given token found in the application!',
    resetPasswordErrorLoggedIn: 'You cant reset your password when logged in',
    resetPasswordErrorMalformedToken: 'Redirected because of malformed token!',
    passwordsMustMatch: 'Both passwords must match!',
    changeUserPicture: 'Change profile picture',
    uploadUserPicture: 'Upload picture',
    dateOfBirth: 'Date of Birth',
    gender: `Gender`,
    type: `User Role`,

    regMemToComp: 'Register Members to a Competition',
    searchAndSelectToRegister: 'Suche und wähle einen Wettkampf für die Anmeldung',
    registrationEnds: 'Registration ends',
    members: 'Members',
    membersPerPage: 'members per page',
    searchACompetition: 'Search a competition',
    back: 'Back',
    showRegistrationRequirements: 'Show registration requirements',
    addAnotherCondition: 'Add another condition',
    manageRegistrationConstraints: 'Manage registration constraints. Somebody can register if all constraints are fulfilled.',
    registerConstraints: 'Register Constraints',
    noConstraints: 'No Constraints',
    constraints: 'Constraints',
    everybodyCanRegisterToThisGroup: 'Everybody can register to this group.',
    age: 'Age',
    equals: 'equals',
    notEquals: 'not equals',
    greaterThan: 'greater than',
    greaterOrEqualsThan: 'greater or equals than',
    lessThan: 'less than',
    lessOrEqualsThan: 'less or equals than',
    bornBefore: 'born before',
    bornAfter: 'born after',


    editTeam: 'Edit Team',
    addRemoveFlags: 'Add/Remove Flags',

    liveResults: 'Live Results',

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
