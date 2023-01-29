import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GradingGroupDetailComponent } from './grading-group-detail.component';

describe('GradingGroupDetailComponent', () => {
  let component: GradingGroupDetailComponent;
  let fixture: ComponentFixture<GradingGroupDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GradingGroupDetailComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GradingGroupDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
