import { formatDate } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { SimpleCompetitionListEntryDto } from 'src/app/dtos/simpleCompetitionListEntryDto';

@Component({
  selector: 'app-competition-list-view-entry',
  templateUrl: './competition-list-view-entry.component.html',
  styleUrls: ['./competition-list-view-entry.component.scss']
})
export class CompetitionListViewEntryComponent implements OnInit {
  @Input()
  competition: SimpleCompetitionListEntryDto;

  constructor() { }

  ngOnInit(): void {
  }

  public formatCompetitionDate(date: Date): string {
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }
}
