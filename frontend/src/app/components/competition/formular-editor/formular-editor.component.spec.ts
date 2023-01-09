import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FormularEditorComponent} from './formular-editor.component';

describe('FormularEditorComponent', () => {
  let component: FormularEditorComponent;
  let fixture: ComponentFixture<FormularEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormularEditorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormularEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
