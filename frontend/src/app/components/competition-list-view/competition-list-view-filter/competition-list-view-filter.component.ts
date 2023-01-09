import {formatDate} from '@angular/common';
import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Subject} from 'rxjs';
import {CompetitionSearchDto} from 'src/app/dtos/competitionSearchDto';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-competition-list-view-filter',
  templateUrl: './competition-list-view-filter.component.html',
  styleUrls: ['./competition-list-view-filter.component.scss']
})
export class CompetitionListViewFilterComponent implements OnInit {
  @Output() competitionSearchChange = new EventEmitter<CompetitionSearchDto>();
  competitionSearch: CompetitionSearchDto = {
    name: '',
    begin: `${formatDate(new Date(), 'yyyy-MM-dd', 'en-US')}`,
    beginRegistration: `${formatDate(new Date(), 'yyyy-MM-dd', 'en-US')}T00:00:00`,
  };

  registrationPossible = true;
  currentDateFormatted = `${formatDate(new Date(), 'yyyy-MM-dd', 'en-US')}T00:00:00`;
  year1900Formatted = '1900-01-01T00:00:00';
  year2100Formatted = '2100-01-01T00:00:00';

  inputChange = new Subject<string>();

  constructor() { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    // this.registerDebounce();
    this.emitToFetchCompetitions();
  }

  // registerDebounce(){
  //   this.inputChange
  //   .pipe(
  //     debounceTime(300),
  //   )
  //   .subscribe({
  //     next: data => {
  //       this.emitToFetchCompetitions();
  //     },
  //     error: err => {
  //     },
  //   });
  // }

  emitToFetchCompetitions() {
    this.competitionSearchChange.emit({
      ...this.competitionSearch,
      begin: this.competitionSearch.begin + 'T00:00:00',
      end: this.year1900Formatted,
      endRegistration: (this.registrationPossible ? this.currentDateFormatted : this.year1900Formatted),
      beginRegistration: this.year1900Formatted,
    });
  }

  onSearchButtonClicked() {
    this.emitToFetchCompetitions();
  }
}
