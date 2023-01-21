import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCompetitionSelectGradingSystemDialogComponent } from './create-competition-select-grading-system-dialog.component';

describe('CreateCompetitionSelectGradingSystemDialogComponent', () => {
  let component: CreateCompetitionSelectGradingSystemDialogComponent;
  let fixture: ComponentFixture<CreateCompetitionSelectGradingSystemDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateCompetitionSelectGradingSystemDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCompetitionSelectGradingSystemDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
