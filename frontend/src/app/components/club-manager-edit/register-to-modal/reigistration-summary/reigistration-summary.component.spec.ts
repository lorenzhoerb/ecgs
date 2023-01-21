import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReigistrationSummaryComponent } from './reigistration-summary.component';

describe('ReigistrationSummaryComponent', () => {
  let component: ReigistrationSummaryComponent;
  let fixture: ComponentFixture<ReigistrationSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReigistrationSummaryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReigistrationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
