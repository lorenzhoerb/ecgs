import {Component, Input, OnInit, OnChanges} from '@angular/core';
import LocalizationService, {LocalizeService} from 'src/app/services/localization/localization.service';
import {debounceTime, of, Subject, switchMap} from 'rxjs';

@Component({
  selector: 'app-bulk-editor',
  templateUrl: './bulk-editor.component.html',
  styleUrls: ['./bulk-editor.component.scss']
})
export class BulkEditorComponent<T> implements OnInit, OnChanges {

  @Input()
  header: string[] = [];

  @Input()
  recordsPerPage = 25;

  @Input()
  enablePagination = false;

  @Input()
  disableCount = false;

  @Input()
  disableCheckBox = false;

  @Input()
  disableDelete = false;

  @Input()
  updateCounter = 0;

  currentPage = 1;
  displayData: T[] = [];
  data: T[] = [];
  checks: boolean[] = [];
  masterChecked = false;
  pageChange = new Subject<number>();

  constructor() {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  @Input()
  fetchData = () => of([]);

  @Input()
  onRemoveFlagClick = (record: T) => null;

  @Input()
  mapRecord = (record: T): any[] => [];

  @Input()
  bulkAction = (records: T[]) => null;

  ngOnInit(): void {
    this.pageChange.pipe(debounceTime(50));
    this.pageChange.next(1);
    this.fetchDataInternal();
  }

  ngOnChanges(changes: any) {
    if(changes.updateCounter != null) {
      this.fetchDataInternal();
    }
    changes.bulkAction.currentValue(this.getCheckedData());
    this.updateDisplayData();
    this.bulkAction = null;
    this.setAllBulk(false);
    this.masterChecked = false;
  }

  public onMasterBulkChange(checked) {
    if (checked) {
      this.setAllBulk(true);
    } else {
      this.setAllBulk(false);
    }
  }

  public onPageChangeClick(change: number) {
    this.currentPage = Math.max(Math.min(this.currentPage + change, this.data.length / this.recordsPerPage), 1);
    this.updateDisplayData();
    this.pageChange.next(this.currentPage);
  }

  public onRemoveFlagClickInternal(index: number) {
    this.onRemoveFlagClick(this.data[index]);
    this.data.splice(index, 1);
    this.updateDisplayData();
  }

  private updateDisplayData() {
    this.displayData = this.data.slice((this.currentPage-1)*this.recordsPerPage, this.currentPage*this.recordsPerPage);
  }

  private setAllBulk(checked: boolean) {
    this.checks = this.checks.map(_ => checked);
  }

  private getCheckedData(): T[] {
    const data = [];
    for (let i = 0; i < this.checks.length; i++) {
      if (this.checks[i]) {
        data.push(this.data[i]);
      }
    }
    return data;
  }

  private fetchDataInternal() {
    this.fetchData().subscribe({
      next: data => {
        this.data = data;
        if(!this.enablePagination) {
          this.recordsPerPage = this.data.length;
        }
        this.updateDisplayData();
        this.checks = Array(this.data.length).fill(false);
      },
      error: err => {
        console.log('Error when fetching data for bulk table', err);
      }
    });
  }
}
