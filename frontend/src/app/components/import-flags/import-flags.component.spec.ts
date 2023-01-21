import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportFlagsComponent } from './import-flags.component';

describe('ImportFlagsComponent', () => {
  let component: ImportFlagsComponent;
  let fixture: ComponentFixture<ImportFlagsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportFlagsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImportFlagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
