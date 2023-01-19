import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionGradingStationComponent } from './competition-grading-station.component';

describe('CompetitionGradingStationComponent', () => {
  let component: CompetitionGradingStationComponent;
  let fixture: ComponentFixture<CompetitionGradingStationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionGradingStationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionGradingStationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
