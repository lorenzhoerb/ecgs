import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderCreateModalComponent} from './header-create-modal.component';

describe('HeaderModalComponent', () => {
  let component: HeaderCreateModalComponent;
  let fixture: ComponentFixture<HeaderCreateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeaderCreateModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderCreateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
