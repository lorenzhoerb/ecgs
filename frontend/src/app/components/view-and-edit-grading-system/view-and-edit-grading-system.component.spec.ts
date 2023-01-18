import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAndEditGradingSystemComponent } from './view-and-edit-grading-system.component';

describe('ViewAndEditGradingSystemComponent', () => {
  let component: ViewAndEditGradingSystemComponent;
  let fixture: ComponentFixture<ViewAndEditGradingSystemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewAndEditGradingSystemComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewAndEditGradingSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
