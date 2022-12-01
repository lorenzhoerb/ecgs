import { Component, OnInit } from '@angular/core';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service' ;

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  public loggedIn = false;

  constructor() { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

}
