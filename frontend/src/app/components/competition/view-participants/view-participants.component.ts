import {Component, Input, OnInit} from '@angular/core';
import {Gender, genderMap, UserDetail} from '../../../dtos/user-detail';

@Component({
  selector: 'app-view-participants',
  templateUrl: './view-participants.component.html',
  styleUrls: ['./view-participants.component.scss']
})
export class ViewParticipantsComponent implements OnInit {
  @Input() participants: UserDetail[];

  constructor() {
  }

  ngOnInit(): void {
  }


  formatGender(gender: Gender) {
    return genderMap.get(gender);
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString();
  }
}
