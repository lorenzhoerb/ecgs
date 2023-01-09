import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GoldenRatioContainerComponent} from './golden-ratio-container.component';

describe('GoldenRatioContainerComponent', () => {
  let component: GoldenRatioContainerComponent;
  let fixture: ComponentFixture<GoldenRatioContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GoldenRatioContainerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GoldenRatioContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
