import {Component, OnInit} from '@angular/core';
import {CompetitionService} from '../../../services/competition.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SimpleGradingGroup} from '../../../dtos/simple-grading-group';

@Component({
  selector: 'app-grading-groups',
  templateUrl: './grading-groups.component.html',
  styleUrls: ['./grading-groups.component.scss']
})
export class GradingGroupsComponent implements OnInit {

  competitionId: number;
  gradingGroups: SimpleGradingGroup[];
  url: string;


  constructor(
    private competitionService: CompetitionService,
    private route: ActivatedRoute,
    private router: Router) {
  }


  ngOnInit(): void {
    this.onUrlChange();
    this.url = this.router.url;
  }

  onUrlChange() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.competitionId = parseInt(params.id, 10);
        if (isNaN(this.competitionId)) {
          this.router.navigate(['/']);
        }
        this.fetchGroups();
      }
    });
  }

  fetchGroups() {
    this.competitionService.getGroups(this.competitionId).subscribe({
      next: value => this.gradingGroups = value,
      error: err => {
        console.error(err);
      }
    });
  }

  onClick(ggId: number) {
    this.router.navigate(['competition', this.competitionId, 'groups', ggId]);
  }
}
