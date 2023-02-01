import {ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderFolderModalComponent} from './header-folder-modal.component';

describe('HeaderModalComponent', () => {
  let component: HeaderFolderModalComponent;
  let fixture: ComponentFixture<HeaderFolderModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeaderFolderModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderFolderModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
