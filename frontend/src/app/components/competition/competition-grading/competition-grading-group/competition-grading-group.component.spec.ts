import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionGradingGroupComponent } from './competition-grading-group.component';

describe('CompetitionGradingGroupComponent', () => {
  let component: CompetitionGradingGroupComponent;
  let fixture: ComponentFixture<CompetitionGradingGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionGradingGroupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionGradingGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
