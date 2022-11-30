import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UidemoComponent } from './uidemo.component';

describe('UidemoComponent', () => {
  let component: UidemoComponent;
  let fixture: ComponentFixture<UidemoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UidemoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UidemoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
