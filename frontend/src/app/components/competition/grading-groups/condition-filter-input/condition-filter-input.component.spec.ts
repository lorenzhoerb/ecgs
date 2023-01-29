import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConditionFilterInputComponent } from './condition-filter-input.component';

describe('ConditionFilterInputComponent', () => {
  let component: ConditionFilterInputComponent;
  let fixture: ComponentFixture<ConditionFilterInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConditionFilterInputComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConditionFilterInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
