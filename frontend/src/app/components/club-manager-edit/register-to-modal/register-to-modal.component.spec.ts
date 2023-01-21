import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterToModalComponent } from './register-to-modal.component';

describe('RegisterToModalComponent', () => {
  let component: RegisterToModalComponent;
  let fixture: ComponentFixture<RegisterToModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterToModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterToModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
