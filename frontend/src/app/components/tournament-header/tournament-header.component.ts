import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import LocalizationService, { LocalizeService } from 'src/app/services/localization/localization.service' ;

@Component({
  selector: 'app-tournament-header',
  templateUrl: './tournament-header.component.html',
  styleUrls: ['./tournament-header.component.scss']
})
export class TournamentHeaderComponent implements OnInit, AfterViewInit {

  @Input() title = '...';

  @Input() isEdit = false;

  @ViewChild('header', { read: ElementRef })
  header: ElementRef;
  @ViewChild('ob', { read: ElementRef })
  ob: ElementRef;

  intersectionObserver?: IntersectionObserver;

  constructor() { }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.intersectionObserver = new IntersectionObserver(entries => {
      if (entries[0].intersectionRatio === 0) {
       this.header.nativeElement.classList.add('stuck');
        //newEl.classList.add("sticky-observer");
      } else if (entries[0].intersectionRatio === 1) {
       this.header.nativeElement.classList.remove('stuck');
        //newEl.classList.remove("sticky-observer");
      }
    }, {threshold: [0,1]});

    this.intersectionObserver.observe(this.ob.nativeElement);
  }

}
