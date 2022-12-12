import {Component, OnInit} from '@angular/core';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service' ;
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  constructor(public authService: AuthService) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit() {
  }

}
