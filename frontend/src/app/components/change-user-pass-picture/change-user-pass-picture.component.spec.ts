import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeUserPassPictureComponent } from './change-user-pass-picture.component';

describe('ChangeUserPassPictureComponent', () => {
  let component: ChangeUserPassPictureComponent;
  let fixture: ComponentFixture<ChangeUserPassPictureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChangeUserPassPictureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeUserPassPictureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
