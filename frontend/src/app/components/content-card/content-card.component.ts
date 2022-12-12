import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-content-card',
  templateUrl: './content-card.component.html',
  styleUrls: ['./content-card.component.scss']
})
export class ContentCardComponent implements OnInit {

  @Input() title = 'Titel...';
  @Input() color = 'bc-space';
  @Input() isEdit = false;
  @Input() actions = [];

  @Output() titleChange = new EventEmitter<string>();

  @Output() duplicate = new EventEmitter();
  @Output() delete = new EventEmitter();


  collapsed = false;



  constructor() { }

  ngOnInit(): void {
  }

  updateTitle() {
    this.titleChange.emit(this.title);
  }

  deleteClicked() {
    this.delete.emit();
  }

  duplicateClicked() {
    this.duplicate.emit();
  }

}
