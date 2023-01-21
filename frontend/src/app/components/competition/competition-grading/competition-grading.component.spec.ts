import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetitionGradingComponent } from './competition-grading.component';

describe('CompetitionGradingComponent', () => {
  let component: CompetitionGradingComponent;
  let fixture: ComponentFixture<CompetitionGradingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionGradingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionGradingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
