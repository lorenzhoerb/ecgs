import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {CompetitionService} from '../../../../services/competition.service';
import {concat, debounceTime, map, Observable, Subject, switchMap} from 'rxjs';
import {SimpleCompetitionListDto} from '../../../../dtos/simpleCompetitionListDto';
import {SimpleCompetitionListEntryDto} from '../../../../dtos/simpleCompetitionListEntryDto';
import LocalizationService, {LocalizeService} from '../../../../services/localization/localization.service';

@Component({
  selector: 'app-search-select-competition',
  templateUrl: './search-select-competition.component.html',
  styleUrls: ['./search-select-competition.component.scss']
})
export class SearchSelectCompetitionComponent implements OnInit {

  @Output() compSelect = new EventEmitter<SimpleCompetitionListEntryDto>();

  onChange: Subject<string> = new Subject<string>();
  competitions$: Observable<SimpleCompetitionListDto>;
  searchValue = '';

  constructor(private competitionService: CompetitionService) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.competitions$ = concat(
      this.getCompetitions(''),
      this.onChange
        .pipe(
          debounceTime(200),
          switchMap((competitionName) => this.getCompetitions(competitionName))
        ));
  }

  getCompetitions(competitionName: string) {
    return this.competitionService.searchCompetitionsAdvance({
      name: competitionName,
      isRegistrationOpen: true
    }).pipe(
      map(d => d.content)
    );
  }
}
