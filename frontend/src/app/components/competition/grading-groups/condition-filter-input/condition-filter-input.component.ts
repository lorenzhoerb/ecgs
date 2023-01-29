import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {merge} from 'rxjs';
import {ControlValueAccessor, NG_VALUE_ACCESSOR, FormControl} from '@angular/forms';
import {DatePipe} from '@angular/common';

export interface Condition {
  title: string;
  value: string;
  operators: SelectOption[];
  searchType: string;
  searchSelect?: SelectOption[];
}

export interface SelectOption {
  title: string;
  value: string;
}

export interface Filter {
  key: string;
  operator: string;
  value: string;
}

@Component({
  selector: 'app-condition-filter-input',
  templateUrl: './condition-filter-input.component.html',
  styleUrls: ['./condition-filter-input.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: ConditionFilterInputComponent,
    multi: true,
  }]
})
export class ConditionFilterInputComponent implements OnInit, ControlValueAccessor {

  _conditions: Condition[] = [];

  public readonly keyControl = new FormControl('',);
  public readonly operatorControl = new FormControl('',);
  public readonly valueControl = new FormControl('',);

  activeCondition: Condition = null;

  constructor(private datePipe: DatePipe) {
  }

  get conditions(): Condition[] {
    return this._conditions;
  }

  @Input()
  set conditions(conditions: Condition[]) {
    if (conditions.length <= 0) {
      return;
    }

    this._conditions = conditions;
    const defaultCondition = conditions[0];
    this.keyControl.setValue(defaultCondition.value);
    this.operatorControl.setValue(defaultCondition.operators[0].value);

    if(defaultCondition.searchType === 'select') {
      this.valueControl.setValue(defaultCondition.searchSelect[0].value);
    } else if (defaultCondition.searchType === 'date') {
     this.valueControl.setValue(new Date().toISOString().substring(0,10));
    } else {
      this.valueControl.setValue('');
    }


    this.activeCondition = defaultCondition;
  }

  ngOnInit(): void {
    merge(
      this.keyControl.valueChanges,
      this.operatorControl.valueChanges,
      this.valueControl.valueChanges
    ).subscribe(() => {
      const filter = this._getValue();
      this.activeCondition = this.getActiveCondition();
      this._onChange(filter);
    });

    this.keyControl.valueChanges.subscribe(() => {
      this.valueControl.setValue('');
      if(this.activeCondition.searchType === 'select') {
        this.valueControl.setValue(this.activeCondition.searchSelect[0].value);
      } else if (this.activeCondition.searchType === 'date') {
        this.valueControl.setValue(new Date().toISOString().substring(0,10));
      }
    });
  }

  getActiveCondition() {
    return this._conditions.filter(c => c.value === this.keyControl.value)[0];
  }

  _onChange = (_value: Filter | null): void => undefined;
  registerOnChange(fn: (value: Filter | null) => void): void {
    this._onChange = fn;
  }

  onTouched = (): void => undefined;
  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  writeValue(obj: Filter): void {
    if (obj) {
      this.keyControl.setValue(obj.key);
      this.operatorControl.setValue(obj.operator);
      this.valueControl.setValue(obj.value);
    }
  }

  private _getValue(): Filter {
    return {
      key: this.keyControl.value,
      operator: this.operatorControl.value,
      value: this.valueControl.value
    };
  }
}
