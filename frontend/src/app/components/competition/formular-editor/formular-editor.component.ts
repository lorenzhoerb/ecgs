import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {CdkDrag, CdkDropList, copyArrayItem, moveItemInArray, transferArrayItem} from '@angular/cdk/drag-drop';
import {cloneDeep} from 'lodash';

@Component({
  selector: 'app-formular-editor',
  templateUrl: './formular-editor.component.html',
  styleUrls: ['./formular-editor.component.scss']
})
export class FormularEditorComponent implements OnInit, OnChanges {

  @Input() vars: any[] = [
    { name: 'A', value: 1, typeHint: 'variableRef', type: 'variable', spaces: 0, priority: 0 },
    { name: 'B', value: 2, typeHint: 'variableRef', type: 'variable', spaces: 0, priority: 0 },
    { name: 'Abzug', value: 3, typeHint: 'variableRef', type: 'variable', spaces: 0, priority: 0 },
  ];


  @Input() formula = {
    valid: false,
    data: {}
  };

  @Input() name = 'Station';

  @Input() color = 'bc-ming';

  @Input() collapsed = false;

  @Output() formulaChange = new EventEmitter<any>();
  @Output() variablesChange = new EventEmitter<any>();

  variables = cloneDeep(this.vars);

  functions: any[] = [
    { name: '+', value: 'add', typeHint: 'add', type: 'function', spaces: 2, priority: 2 },
    { name: '-', value: 'subt',typeHint: 'subt', type: 'function', spaces: 2, priority: 2 },
    { name: '*', value: 'mult',typeHint: 'mult', type: 'function', spaces: 2, priority: 3 },
    { name: '/', value: 'div', typeHint: 'div', type: 'function', spaces: 2, priority: 3 },
    //{ name: "mean", type: "function", spaces: 'n', priority: 4 },
    //{ name: "x", type: "constant", spaces: 1, priority: 0},
  ];

  calculation: any[][] = [
    []
  ];

  constructor() { }

  ngOnChanges(changes: SimpleChanges): void {
    this.variables = cloneDeep(this.vars);

    if(changes.vars.previousValue === undefined) {
      return;
    }
    const t = this.calculation.filter(arr => arr.length === 0
      || this.variables.map(v => v.value).includes(arr[0].value)
      || this.functions.map(v => v.value).includes(arr[0].value));

      this.calculation = t.map(arr => arr.length === 0 ||
        this.functions.map(
          v => v.value).includes(arr[0].value) ? arr : arr.map(v => this.variables.find(x => x.value === v.value)));

      this.listRebuild();
      this.buildTree();

  }

  ngOnInit(): void {
    if(this.formula.data !== null && this.formula.data['type'] !== undefined) {
      this.calculation = [];
      this.calculationsFromTree(this.formula.data);
      this.listRebuild();
    }
  }

  calculationsFromTree(t) {
    if(t === null || t === undefined) {
      return;
    }

    if (['variable', 'constant'].includes(t.type)) {
      this.calculation.push([t]);
    } else {
      this.calculationsFromTree(t.left);
      this.calculation.push([t]);
      this.calculationsFromTree(t.right);
    }
  }

  buildTree() {
    this.formula.valid = true;
    const values = [];
    const operations = [];

    const calc = this.calculation.filter(arr => arr.length > 0).map(arr => arr[0]);

    // eslint-disable-next-line @typescript-eslint/prefer-for-of
    for(let i = 0; i < calc.length; i++) {
      const current = calc[i];

      if(['variable', 'constant'].includes(current.type)) {
        values.push(current);

        if(i > 0 && ['variable', 'constant'].includes(calc[i-1].type)) {
          this.formula.valid = false;
        }

      } else if(operations.length < 1 || operations[operations.length -1].priority < current.priority) {
        operations.push(current);
      } else if (current.spaces > values.length) {
        //this.formula.data = null;
        this.formula.valid = false;
        //this.formulaChange.emit(this.formula);
        //console.log('failed because of values');
        //return;
      } else if(current.spaces === 2) {
        const [right,left] = [values.pop(), values.pop()];
        const op = operations.pop();
        values.push(Object.assign({}, op, {left, right}));
        operations.push(current);
      }
    }

    for(const op of operations.reverse()) {
      if (op.spaces > values.length) {
        //this.formula.data = null;
        this.formula.valid = false;
        //this.formulaChange.emit(this.formula);
        //console.log('failed because of values');
        //return;
        const [left,right] = [values.pop(), values.pop()];
        values.push(Object.assign({}, op, {left, right}));
      } else {
        const [right,left] = [values.pop(), values.pop()];
        values.push(Object.assign({}, op, {left, right}));
      }

    }

    if(values.length !== 1) {
      //this.formula.data = null;
      this.formula.valid = false;
      //this.formulaChange.emit(this.formula);
      //console.log('failed because of values');
      //return;
    }

    this.formula.data = values.pop();
    if(this.formula.data === undefined) {
      this.formula.data = {};
    }

    this.formulaChange.emit(this.formula);
  }

  drop(event: any, index?: number) {
    if (event.previousContainer !== event.container && event.previousContainer.element.nativeElement.classList.contains('copy')) {
      copyArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      const spaces = event.item.data.spaces;

      /*if(spaces === 0) {
        this.calculation.splice(index+1,0, []);
      } */

      if(spaces === 2 || spaces === 0) {
        this.calculation.splice(index+1,0, []);
        this.calculation.splice(index,0, []);
      }

    } else if (event.previousContainer !== event.container) {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    }
    if (event.previousContainer.data) {
      this.variables = this.variables.filter((f) => !f.temp);
      this.functions = this.functions.filter((f) => !f.temp);
    }
    this.listRebuild();
    this.buildTree();
  }

  exitedVars(event: any) {
    const currentIdx = event.container.data.findIndex(
      (f) => f.name === event.item.data.name
    );
    this.variables.splice(currentIdx + 1, 0, {
      ...event.item.data,
      temp: true,
    });
  }

  exitedFuns(event: any) {
    const currentIdx = event.container.data.findIndex(
      (f) => f.name === event.item.data.name
    );
    this.functions.splice(currentIdx + 1, 0, {
      ...event.item.data,
      temp: true,
    });
  }

  entered() {
    //this.variables = this.variables.filter((f) => !f.temp);
    //this.functions = this.functions.filter((f) => !f.temp);
  }

  noEnterPredicate(): boolean {
    return false;
  }

  onlyOnePredicate(item: CdkDrag<any>, drop: CdkDropList): boolean {
    return drop.data.length < 1;
  }

  getFormulaClass(i: number): any {

    const inbetween =
      this.calculation.length > i + 1
      && this.calculation[i+1].length > 0
      && this.calculation[i+1][0].type === 'variable'
      && i > 0 && this.calculation[i-1].length > 0
      && this.calculation[i-1][0].type === 'variable';

    const inb = this.calculation[i].length === 0
        && (
            (this.calculation.length > i + 1
              && this.calculation[i+1].length > 0
              && this.calculation[i+1][0].type === 'variable')
          || (i > 0 && this.calculation[i-1].length > 0
              && this.calculation[i-1][0].type === 'variable')
          )
        && !inbetween;

    const opempty = this.calculation[i].length === 0 && !inb
          && (
            (i > 0 && this.calculation[i-1].length > 0 && this.calculation[i-1][0].type === 'variable')
            || ( i < this.calculation.length -1 && this.calculation[i+1].length > 0 && this.calculation[i+1][0].type === 'variable'));

    return {
      items: true,
      inbetween: inb,
      empty: this.calculation[i].length === 0 && !inb,
      opempty,
      borcol: this.color === 'bc-space',
      filled: this.calculation[i].length !== 0
    };
  }

  removeFrom(i) {
    this.calculation[i].pop();
    this.listRebuild();
    this.buildTree();
  }

  listRebuild() {
    const temp = this.calculation.filter(arr => arr.length !== 0);

    if(temp.length === 0) {
      this.calculation = [ [] ];
      return;
    }

    const res = [];

    for(let i = 0; i < temp.length; i++) {
      if(i === 0) {
       res.push([]);
      }
      res.push(temp[i]);
      res.push([]);
    }
    this.calculation = res;
  }

}


