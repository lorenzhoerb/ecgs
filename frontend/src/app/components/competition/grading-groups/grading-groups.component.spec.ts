import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GradingGroupsComponent } from './grading-groups.component';

describe('GradingGroupsComponent', () => {
  let component: GradingGroupsComponent;
  let fixture: ComponentFixture<GradingGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GradingGroupsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GradingGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
