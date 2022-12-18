import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionCalenderViewComponent } from './competition-calender-view.component';

describe('CompetitionCalenderViewComponent', () => {
  let component: CompetitionCalenderViewComponent;
  let fixture: ComponentFixture<CompetitionCalenderViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionCalenderViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionCalenderViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
