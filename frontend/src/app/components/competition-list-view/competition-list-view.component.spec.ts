import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionListViewComponent } from './competition-list-view.component';

describe('CompetitionListViewComponent', () => {
  let component: CompetitionListViewComponent;
  let fixture: ComponentFixture<CompetitionListViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionListViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionListViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
