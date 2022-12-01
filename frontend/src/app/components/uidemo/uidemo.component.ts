import { Component, OnInit } from '@angular/core';
import { SupportedLanguages } from 'src/app/services/localization/language';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service' ;

@Component({
  selector: 'app-uidemo',
  templateUrl: './uidemo.component.html',
  styleUrls: ['./uidemo.component.scss']
})
export class UidemoComponent implements OnInit {

  currentLanguage = SupportedLanguages.English;

  constructor() {
    this.localize.changeLanguage(this.currentLanguage);
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  toggleLanguage() {
    if(this.currentLanguage === SupportedLanguages.English) {
      this.currentLanguage = SupportedLanguages.German;
    } else {
      this.currentLanguage = SupportedLanguages.English;
    }
    this.localize.changeLanguage(this.currentLanguage);
  }

}
