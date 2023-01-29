import {Condition} from '../condition-filter-input/condition-filter-input.component';
import LocalizationService, {LocalizeService} from '../../../../services/localization/localization.service';

const localize = () => LocalizationService;

export const CONDITIONS: Condition[] = [
  {
    title: localize().dateOfBirth, value: 'DATE_OF_BIRTH',
    searchType: 'date',
    operators:
      [
        {title: localize().bornBefore, value: 'BORN_BEFORE'},
        {title: localize().bornAfter, value: 'BORN_AFTER'}
      ]
  },
  {
    title: localize().gender,
    value: 'GENDER',
    searchType: 'select',
    operators:
      [
        {title: localize().equals, value: 'EQUALS'},
        {title: localize().notEquals, value: 'NOT_EQUALS'},
      ],
    searchSelect: [{title: localize().genderMan, value: 'MALE'}, {
      title: localize().genderWoman,
      value: 'FEMALE'
    }, {title: localize().genderOther, value: 'OTHER'}]
  },
  {
    title: localize().age,
    value: 'AGE',
    searchType: 'number',
    operators:
      [
        {title: localize().equals, value: 'EQUALS'},
        {title: localize().greaterThan, value: 'GREATER_THAN'},
        {title: localize().greaterOrEqualsThan, value: 'GREATER_EQUALS_THAN'},
        {title: localize().lessThan, value: 'LESS_THAN'},
        {title: localize().lessOrEqualsThan, value: 'LESS_EQUALS_THAN'},
        {title: localize().notEquals, value: 'NOT_EQUALS'},
      ],
    searchSelect: [{title: localize().genderMan, value: 'MALE'}, {
      title: localize().genderWoman,
      value: 'FEMALE'
    }, {title: localize().genderOther, value: 'OTHER'}]
  }
];
