import { Component, OnInit } from '@angular/core';
import { ContentCardComponent } from '../content-card/content-card.component';
import {CompetitionService} from '../../services/competition.service';
import {Competition} from '../../dtos/competition';
import {ActivatedRoute} from '@angular/router';
import {SupportedLanguages} from '../../services/localization/language';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';

@Component({
  selector: 'app-competition-view',
  templateUrl: './competition.component.html',
  styleUrls: ['./competition.component.scss']
})
export class CompetitionComponent implements OnInit {
  competition: Competition = {
    name: 'Competition',
    beginOfRegistration: new Date(),
    beginOfCompetition: new Date(),
    endOfRegistration: new Date(),
    endOfCompetition: new Date(),
    description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut ' +
      'labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.' +
      ' Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, ' +
      'consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed ' +
      'diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata ' +
      'sanctus est Lorem ipsum dolor sit amet.',
    picturePath: '',
    draft: false,
    isPublic: true,
    email: 'test@mail.com',
    phone: '+436606060666'
  };
  // Error flag
  error = false;
  errorMessage = '';
  currentLanguage = SupportedLanguages.English;

  constructor(private service: CompetitionService,
              private route: ActivatedRoute) {
    this.localize.changeLanguage(this.currentLanguage);
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if(params.id) {
        const id: number = parseInt(params.id, 10);

        this.service.getCompetitionById(id).subscribe({
          next: data => {
            this.competition = data;
            console.log(data);
          },
          error: error => {
            console.error('Error fetching competition information', error);
            this.errorMessage = 'Error fetching competition information: \n' + error.error.message;
            error = true;
            // @TODO: route to home or throw exception
          }
        });
      }
    });
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  toggleLanguage() {
    if(this.currentLanguage === SupportedLanguages.English) {
      this.currentLanguage = SupportedLanguages.German;
    } else {
      this.currentLanguage = SupportedLanguages.English;
    }
    this.localize.changeLanguage(this.currentLanguage);
  }

  pictureEmpty() {
    return this.competition.picturePath === '' || this.competition.picturePath === null;
  }
}
