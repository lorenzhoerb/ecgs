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
  id: number;
  competition: Competition = null;
  error: Error = null;
  currentLanguage = SupportedLanguages.English;

  constructor(private service: CompetitionService,
              private route: ActivatedRoute) {
    console.log('init');
    this.localize.changeLanguage(this.currentLanguage);
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if(params.id) {
        this.id = parseInt(params.id, 10);

        this.service.getCompetitionById(this.id).subscribe({
          next: data => {
            this.competition = data;
            this.error = null;
            console.log(data);
          },
          error: error => {
            console.error('Error fetching competition information', error);
            this.error = error;
            this.competition = null;
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
