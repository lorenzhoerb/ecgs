import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NavBarItem} from '../../../dtos/nav-bar-item';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {

  @Input()
  navigation: NavBarItem[] = [];

  @Input()
  active: string;

  @Output()
  navSelect = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit(): void {
  }

  onClick(key: string) {
    this.active = key;
    this.navSelect.emit(this.active);
  }
}
