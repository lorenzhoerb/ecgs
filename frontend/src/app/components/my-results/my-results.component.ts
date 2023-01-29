import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-my-results',
  templateUrl: './my-results.component.html',
  styleUrls: ['./my-results.component.scss']
})
export class MyResultsComponent implements OnInit {

  results: any[] = [];

  constructor(private userService: UserService,
    private toastr: ToastrService) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.userService.getMyResults().subscribe(res => {
      this.results = res.map(re => Object.assign(re, {modal: false}));
    });
  }

  openModal(result) {
    result.modal = true;
  }

  closeModal(result) {
    result.modal = false;
  }

  getResultHeaders(ranking) {
    return ranking.gradesRankingResults.map(x => x.name);
  }

  getResultValues(ranking) {
    return ranking.gradesRankingResults.map(x => x.results);
  }

}
