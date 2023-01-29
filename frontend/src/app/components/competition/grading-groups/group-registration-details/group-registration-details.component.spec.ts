import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupRegistrationDetailsComponent } from './group-registration-details.component';

describe('GroupRegistrationDetailsComponent', () => {
  let component: GroupRegistrationDetailsComponent;
  let fixture: ComponentFixture<GroupRegistrationDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupRegistrationDetailsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupRegistrationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
