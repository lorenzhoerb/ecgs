import {Pipe, PipeTransform} from '@angular/core';
import {Gender} from '../dtos/user-detail';
import LocalizationService, {LocalizeService} from '../services/localization/localization.service';

@Pipe({
  name: 'gender'
})
export class GenderPipe implements PipeTransform {

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  transform(value: Gender, ...args: unknown[]): string {
    switch (value) {
      case Gender.female:
        return this.localize.genderWoman;
      case Gender.male:
        return this.localize.genderMan;
      case Gender.other:
        return this.localize.genderOther;
      default:
          return null;
    }
  }
}
