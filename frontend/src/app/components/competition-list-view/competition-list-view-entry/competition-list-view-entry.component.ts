import {Component, Input, OnInit} from '@angular/core';
import {SimpleCompetitionListEntryDto} from 'src/app/dtos/simpleCompetitionListEntryDto';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-competition-list-view-entry',
  templateUrl: './competition-list-view-entry.component.html',
  styleUrls: ['./competition-list-view-entry.component.scss']
})
export class CompetitionListViewEntryComponent implements OnInit {
  @Input()
  competition: SimpleCompetitionListEntryDto;

  constructor() { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  public formatCompetitionDate(date: Date): string {
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }
}
