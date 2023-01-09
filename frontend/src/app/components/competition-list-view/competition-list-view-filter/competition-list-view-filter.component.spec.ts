import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CompetitionListViewFilterComponent} from './competition-list-view-filter.component';

describe('CompetitionListViewFilterComponent', () => {
  let component: CompetitionListViewFilterComponent;
  let fixture: ComponentFixture<CompetitionListViewFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionListViewFilterComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionListViewFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
