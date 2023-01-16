import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BulkEditorComponent } from './bulk-editor.component';

describe('BulkEditorComponent', () => {
  let component: BulkEditorComponent<any>;
  let fixture: ComponentFixture<BulkEditorComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BulkEditorComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BulkEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
