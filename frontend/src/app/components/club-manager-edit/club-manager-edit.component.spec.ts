import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClubManagerEditComponent } from './club-manager-edit.component';

describe('ClubManagerEditComponent', () => {
  let component: ClubManagerEditComponent;
  let fixture: ComponentFixture<ClubManagerEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClubManagerEditComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClubManagerEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
