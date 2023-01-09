import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ClubManagerImportTeamComponent} from './club-manager-import-team.component';

describe('ClubManagerAddParticipantsComponent', () => {
  let component: ClubManagerImportTeamComponent;
  let fixture: ComponentFixture<ClubManagerImportTeamComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClubManagerImportTeamComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClubManagerImportTeamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
