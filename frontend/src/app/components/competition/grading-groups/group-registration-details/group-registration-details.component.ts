import {Component, Input, OnInit} from '@angular/core';
import {SimpleGradingGroup} from '../../../../dtos/simple-grading-group';
import LocalizationService, {LocalizeService} from '../../../../services/localization/localization.service';

@Component({
  selector: 'app-group-registration-details',
  templateUrl: './group-registration-details.component.html',
  styleUrls: ['./group-registration-details.component.scss']
})
export class GroupRegistrationDetailsComponent implements OnInit {

  @Input() gradingGroups: SimpleGradingGroup[];

  constructor() { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }


}
