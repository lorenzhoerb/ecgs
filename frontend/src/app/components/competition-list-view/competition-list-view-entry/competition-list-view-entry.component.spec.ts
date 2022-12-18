import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionListViewEntryComponent } from './competition-list-view-entry.component';

describe('CompetitionListViewEntryComponent', () => {
  let component: CompetitionListViewEntryComponent;
  let fixture: ComponentFixture<CompetitionListViewEntryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionListViewEntryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionListViewEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
