import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TemplateAction, TemplateState} from 'src/app/datatypes/templateAction';
import {Router} from '@angular/router';
import {Location} from '@angular/common';

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
  @Input() hasBackButton = false;
  @Input() navigateBackPath: string;

  @Output() titleChange = new EventEmitter<string>();

  @Output() duplicate = new EventEmitter();
  @Output() delete = new EventEmitter();
  @Output() collapsed = new EventEmitter<boolean>();
  @Output() templateAction = new EventEmitter<TemplateAction>();
  @Output() largeExpandChanged = new EventEmitter<boolean>();

  public tState = TemplateState;



  constructor(
    private router: Router,
    private location: Location
  ) {
  }

  ngOnInit(): void {
  }

  updateTitle() {
    this.titleChange.emit(this.title);
  }

  deleteClicked() {
    this.delete.emit();
  }

  onBack() {
    if(this.navigateBackPath) {
      this.router.navigate([this.navigateBackPath]);
    } else {
      this.location.back();
    }
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
