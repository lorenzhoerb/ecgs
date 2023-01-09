import {ComponentFixture, TestBed} from '@angular/core/testing';

import {CompetitionHeaderComponent} from './competition-header.component';

describe('CompetitionHeaderComponent', () => {
  let component: CompetitionHeaderComponent;
  let fixture: ComponentFixture<CompetitionHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetitionHeaderComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompetitionHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
