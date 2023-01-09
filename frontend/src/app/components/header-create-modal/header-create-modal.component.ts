import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth.service';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';

@Component({
  selector: 'app-create-header-modal',
  templateUrl: './header-create-modal.component.html',
  styleUrls: ['./header-create-modal.component.scss']
})
export class HeaderCreateModalComponent implements OnInit {

  @Input() public showMenu = false;
  @Output() public showMenuChange = new EventEmitter<boolean>();
  public language: string;

  constructor(public authService: AuthService, private router: Router) { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
    this.language = this.localize.getLanguage();
  }

  logout() {
    this.authService.logoutUser();
    this.router.navigate(['/login']);
  }

  switchLanguage(evt) {
    this.localize.changeLanguage(evt.value);
  }

  hide() {
    this.showMenu = !this.showMenu;
    this.showMenuChange.emit(this.showMenu);
  }
}
