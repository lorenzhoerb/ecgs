import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiveResultsComponent } from './live-results.component';

describe('LiveResultsComponent', () => {
  let component: LiveResultsComponent;
  let fixture: ComponentFixture<LiveResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LiveResultsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LiveResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
