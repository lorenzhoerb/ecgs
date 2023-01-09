import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  @Output() showMenu = new EventEmitter();
  @Output() showCreateMenu = new EventEmitter();

  constructor(public authService: AuthService) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit() {
  }

}
