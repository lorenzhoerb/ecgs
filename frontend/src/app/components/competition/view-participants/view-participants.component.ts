import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {CompetitionService} from '../../../services/competition.service';
import {Gender, genderMap, UserDetail} from '../../../dtos/user-detail';

@Component({
  selector: 'app-view-participants',
  templateUrl: './view-participants.component.html',
  styleUrls: ['./view-participants.component.scss']
})
export class ViewParticipantsComponent implements OnInit {
  @Input() id: number;
  participants: UserDetail[];
  error: Error;

  constructor(
    private router: Router,
    private service: CompetitionService) {
  }

  ngOnInit(): void {
     this.service.getParticipants(this.id).subscribe({
       next: data => {
         this.participants = data;
         console.log(data);
         this.error = null;
       },
       error: error => {
         this.participants = [];
         console.error('Error fetching competition information', error);
         this.error = error;
       }
     });
  }

  formatGender(gender: Gender) {
    return genderMap.get(gender);
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString();
  }
}
