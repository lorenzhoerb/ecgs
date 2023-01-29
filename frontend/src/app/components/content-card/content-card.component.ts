import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { TemplateAction, TemplateState } from 'src/app/datatypes/templateAction';

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
  @Input() templateState = TemplateState.none;
  @Input() isCollapsed = false;
  @Input() isLargeExpanded = false;

  @Output() titleChange = new EventEmitter<string>();

  @Output() duplicate = new EventEmitter();
  @Output() delete = new EventEmitter();
  @Output() collapsed = new EventEmitter<boolean>();
  @Output() templateAction = new EventEmitter<TemplateAction>();
  @Output() largeExpandChanged = new EventEmitter<boolean>();

  public tState = TemplateState;




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
  collapse() {
    this.isCollapsed = !this.isCollapsed;
    this.collapsed.emit(this.isCollapsed);
  }

  expand() {
    this.isLargeExpanded = !this.isLargeExpanded;
    this.largeExpandChanged.emit(this.isLargeExpanded);
  }

  templateClicked() {
    this.templateAction.emit(TemplateAction.saveNew);
  }

}
