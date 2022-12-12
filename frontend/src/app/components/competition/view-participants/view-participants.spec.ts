import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewParticipantsComponent } from './view-participants.component';

describe('ViewParticipantComponent', () => {
  let component: ViewParticipantsComponent;
  let fixture: ComponentFixture<ViewParticipantsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewParticipantsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewParticipantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
